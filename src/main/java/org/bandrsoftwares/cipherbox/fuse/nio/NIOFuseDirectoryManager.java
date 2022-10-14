package org.bandrsoftwares.cipherbox.fuse.nio;

import com.google.common.collect.Iterators;
import jnr.ffi.Pointer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bandrsoftwares.cipherbox.fuse.FuseDirectoryManager;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.struct.FuseFileInfo;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;

@Slf4j
@Singleton
public class NIOFuseDirectoryManager extends NIOFuseManager implements FuseDirectoryManager {

    // Constants.

    public static final Path CURRENT_DIR = Paths.get(".");

    public static final Path PARENT_DIR = Paths.get("..");

    // Variables.

    private final DirectoryFileFilter directoryFileFilter;

    // Constructors.

    @Inject
    NIOFuseDirectoryManager(@NonNull PhysicalPathRecover pathRecover, @Nullable DirectoryFileFilter directoryFileFilter) {
        super(pathRecover);
        this.directoryFileFilter = directoryFileFilter;
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
            fillDirectory(physicalPath, stream, buf, filter, fi);
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

    private void fillDirectory(Path directoryPhysicalPath, DirectoryStream<Path> stream, Pointer buf, FuseFillDir filter, FuseFileInfo fi)
            throws BufferOutOfMemoryException {
        Iterator<Path> currentAndParent = Iterators.forArray(CURRENT_DIR, PARENT_DIR);
        Iterator<Path> iterator = Iterators.concat(currentAndParent, stream.iterator());
        while (iterator.hasNext()) {
            Path directoryFile = iterator.next();
            if (accept(directoryPhysicalPath, directoryFile)) {
                applyFile(directoryFile, buf, filter, fi);
            }
        }
    }

    private boolean accept(Path directoryPhysicalPath, Path directoryFile) {
        boolean accepted = true;
        if (directoryFileFilter != null) {
            accepted = directoryFileFilter.accept(directoryPhysicalPath, directoryFile);
        }
        return accepted;
    }

    protected void applyFile(Path filePath, Pointer buf, FuseFillDir filler, FuseFileInfo fi) throws BufferOutOfMemoryException {
        String displayedName = getDisplayedName(filePath.getFileName().toString());
        if (filler.apply(buf, displayedName, null, 0) != 0) {
            throw new BufferOutOfMemoryException();
        }
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
