package org.bandrsoftwares.cipherbox.fuse.nio;

import com.google.common.collect.Iterators;
import jnr.ffi.Pointer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bandrsoftwares.cipherbox.fuse.FuseDirectoryManager;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.struct.FuseFileInfo;

import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;

@Slf4j
public abstract class NIOFuseDirectoryManager extends NIOFuseManager implements FuseDirectoryManager {

    // Constants.

    public static final Path CURRENT_DIR = Paths.get(".");

    public static final Path PARENT_DIR = Paths.get("..");

    // Constructors.

    protected NIOFuseDirectoryManager(@NonNull PhysicalPathRecover pathRecover) {
        super(pathRecover);
    }

    // Methods.

    @Override
    public int mkdir(String path, long mode) {
        Path physicalPath = getPathRecover().recover(Paths.get(path));
        try {
            log.trace("Create directory {}", path);
            Files.createDirectory(physicalPath);
            return 0;
        } catch (FileAlreadyExistsException e) {
            log.warn("Fail to create directory, already existing file for " + physicalPath, e);
            return -ErrorCodes.EEXIST();
        } catch (IOException e) {
            log.error("IO error during directory creation for " + physicalPath, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int readdir(String path, Pointer buf, FuseFillDir filter, long offset, FuseFileInfo fi) {
        Path physicalPath = getPathRecover().recover(Paths.get(path));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(physicalPath)) {
            log.trace("Read dir {}", path);
            fillDirectory(stream, buf, filter, fi);
            return 0;
        } catch (BufferOutOfMemoryException e) {
            log.warn("Buffer out of memory during read dir for {}", physicalPath);
            return -ErrorCodes.ENOMEM();
        } catch (NotDirectoryException e) {
            log.warn("Fail to to read directory {} because it is not a directory", physicalPath);
            return -ErrorCodes.ENOTDIR();
        } catch (IOException e) {
            log.error("IO error during reading of directory {}", physicalPath);
            return -ErrorCodes.EIO();
        }
    }

    private void fillDirectory(DirectoryStream<Path> stream, Pointer buf, FuseFillDir filter, FuseFileInfo fi) throws BufferOutOfMemoryException {
        Iterator<Path> currentAndParent = Iterators.forArray(CURRENT_DIR, PARENT_DIR);
        Iterator<Path> iterator = Iterators.concat(currentAndParent, stream.iterator());
        while (iterator.hasNext()) {
            Path directoryFile = iterator.next();
            applyFile(directoryFile, buf, filter, fi);
        }
    }

    protected void applyFile(Path filePath, Pointer buf, FuseFillDir filler, FuseFileInfo fi) throws BufferOutOfMemoryException {
        if (fileCanBeDisplayedInDirectory(filePath)) {
            String displayedName = getDisplayedName(filePath.getFileName().toString());
            if (filler.apply(buf, displayedName, null, 0) != 0) {
                throw new BufferOutOfMemoryException();
            }
        }
    }

    /**
     * Method called in {@link #applyFile(Path, Pointer, FuseFillDir, FuseFileInfo)} to know if a file can be displayed or not in a fuse fs directory.
     * By default, this method returns true if the file name does not start by a "." and is not the "desktop.ini" file.
     *
     * @param filePath the file path to verify
     *
     * @return true if the file can be displayed in a fuse fs directory, else false.
     */
    protected boolean fileCanBeDisplayedInDirectory(@NonNull Path filePath) {
        return !filePath.getFileName().startsWith(".") && !filePath.getFileName().equals(Paths.get("desktop.ini"));
    }

    /**
     * Transform the specified file name to get the displayed file name in a fuse directory.
     *
     * @param fileName the file name to transform
     *
     * @return a transformed file name which will be the file name displayed in a fuse fs directory
     */
    protected String getDisplayedName(@NonNull String fileName) {
        return fileName;
    }

    @Override
    public int rmdir(String path) {
        Path physicalPath = getPathRecover().recover(Paths.get(path));
        try {
            log.trace("Remove directory {}", path);
            if (!Files.isDirectory(physicalPath, LinkOption.NOFOLLOW_LINKS)) {
                log.warn("Fail to remove directory {} because it is not a directory", physicalPath);
                return -ErrorCodes.ENOTDIR();
            }
            Files.delete(physicalPath);
            return 0;
        } catch (DirectoryNotEmptyException e) {
            log.warn("Fail to remove directory {} because it is not empty", physicalPath);
            return -ErrorCodes.ENOTEMPTY();
        } catch (IOException e) {
            log.error("IO error during removing directory {}", physicalPath);
            return -ErrorCodes.EIO();
        }
    }

    // Inner classes.

    private static class BufferOutOfMemoryException extends Exception {

        // Constructors.

        public BufferOutOfMemoryException() {
            super();
        }
    }
}
