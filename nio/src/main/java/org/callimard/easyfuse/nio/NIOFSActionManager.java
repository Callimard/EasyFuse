package org.callimard.easyfuse.nio;

import com.google.common.collect.Iterables;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.callimard.easyfuse.core.GlobalActionManager;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.struct.Statvfs;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;

@Slf4j
@Singleton
public class NIOFSActionManager extends NIOFuseManager implements GlobalActionManager {

    // Constants.

    public static final int BLOCK_SIZE = 4096;

    // Variables.

    @NonNull
    private final FileAttributesUtil fileAttributesUtil;

    // Constructors.

    @Inject
    public NIOFSActionManager(@NonNull PhysicalPathRecover pathRecover, @NonNull FileAttributesUtil fileAttributesUtil) {
        super(pathRecover);
        this.fileAttributesUtil = fileAttributesUtil;
    }

    // Methods.

    @Override
    public int statfs(String path, Statvfs stbuf) {
        log.trace("Stat FS for {}", path);

        stbuf.f_bsize.set(BLOCK_SIZE);
        stbuf.f_frsize.set(BLOCK_SIZE);
        stbuf.f_blocks.set(BLOCK_SIZE * BLOCK_SIZE);
        stbuf.f_bavail.set(BLOCK_SIZE * BLOCK_SIZE);
        stbuf.f_bfree.set(BLOCK_SIZE * BLOCK_SIZE);
        return 0;
    }

    @Override
    public int access(String path, int mask) {
        Path physicalPath = getPathRecover().recover(Paths.get(path));
        try {
            log.trace("Check access for {}", path);

            Set<AccessMode> accessModes = fileAttributesUtil.accessModeMaskToSet(mask);
            physicalPath.getFileSystem().provider().checkAccess(physicalPath, Iterables.toArray(accessModes, AccessMode.class));
            return 0;
        } catch (NoSuchFileException e) {
            log.warn("Fail to check access for file " + physicalPath + " because it does not exist", e);
            return -ErrorCodes.ENOENT();
        } catch (IOException e) {
            log.error("IO error during check access for " + physicalPath + " with mask " + mask, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int rename(String oldPath, String newPath) {
        Path oldPhysicalPath = getPathRecover().recover(Paths.get(oldPath));
        Path newPhysicalPath = getPathRecover().recover(Paths.get(newPath));
        try {
            log.trace("Rename {} to {}", oldPath, newPath);

            Files.move(oldPhysicalPath, newPhysicalPath);
            return 0;
        } catch (DirectoryNotEmptyException e) {
            log.warn("Fail to rename file " + oldPath + " to " + newPath + " because directory not empty", e);
            return -ErrorCodes.ENOTEMPTY();
        } catch (FileAlreadyExistsException e) {
            log.warn("Fail to rename file " + oldPath + " to " + newPath + " because file already exists", e);
            return -ErrorCodes.EEXIST();
        } catch (IOException e) {
            log.error("IO error during rename file " + oldPath + " to " + newPath, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int unlink(String path) {
        Path physicalPath = getPathRecover().recover(Paths.get(path));
        try {
            log.trace("Unlink {}", path);

            if (Files.isDirectory(physicalPath, LinkOption.NOFOLLOW_LINKS)) {
                log.warn("Fail to unlink " + physicalPath + " because it is a directory");
                return -ErrorCodes.EISDIR();
            }

            Files.delete(physicalPath);
            return 0;
        } catch (NoSuchFileException e) {
            log.warn("Fail to unlink file " + physicalPath + " because it does not exists", e);
            return -ErrorCodes.ENOENT();
        } catch (IOException e) {
            log.error("IO error during unlink file " + physicalPath, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int chmod(String path, long mode) {
        Path physicalPath = getPathRecover().recover(Paths.get(path));
        try {
            log.trace("Chmod of {} with mode {}", path, mode);

            Files.setPosixFilePermissions(physicalPath, fileAttributesUtil.octalModeToPosixPermissions(mode));
            return 0;
        } catch (IOException e) {
            log.error("IO error during chmod file " + path, e);
            return -ErrorCodes.EIO();
        }
    }
}
