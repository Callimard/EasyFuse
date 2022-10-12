package org.bandrsoftwares.cipherbox.fuse.nio;

import com.google.common.collect.Iterables;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bandrsoftwares.cipherbox.fuse.FuseFSActionManager;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.Statvfs;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.Set;

@Slf4j
public abstract class NIOFuseFSActionManager extends NIOFuseManager implements FuseFSActionManager {

    // Constants.

    public static final int BLOCK_SIZE = 4096;

    // Variables.

    @NonNull
    private final FileAttributesUtil fileAttributesUtil;

    // Constructors.

    protected NIOFuseFSActionManager(@NonNull PhysicalPathRecover pathRecover, @NonNull FileAttributesUtil fileAttributesUtil) {
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
    public int getattr(String path, FileStat stat) {
        Path physicalPath = getPathRecover().recover(Paths.get(path));
        try {
            log.trace("Get attr for {}", path);

            FileStore fileStore = Files.getFileStore(physicalPath);

            BasicFileAttributes attrs;
            if (fileStore.supportsFileAttributeView(PosixFileAttributeView.class)) {
                attrs = Files.readAttributes(physicalPath, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            } else {
                attrs = Files.readAttributes(physicalPath, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            }

            if (attrs.isRegularFile()) {
                return getFileAttr(attrs, stat);
            } else if (attrs.isDirectory()) {
                return getDirectoryAttr(attrs, stat);
            } else if (attrs.isSymbolicLink()) {
                return getSymbolicLinkAttr(attrs, stat);
            } else {
                log.warn("Fail to get attr of {} because file type is not supported", physicalPath);
                return -ErrorCodes.ENOTSUP();
            }
        } catch (NoSuchFileException e) {
            log.warn("Fail to get attr of file " + physicalPath + " because it does not exist", e);
            return -ErrorCodes.ENOENT();
        } catch (IOException e) {
            log.error("IO error during get attr of " + physicalPath, e);
            return -ErrorCodes.EIO();
        }
    }

    private int getFileAttr(BasicFileAttributes attributes, FileStat fileStat) {
        if (attributes instanceof PosixFileAttributes posixAttr) {
            long mode = fileAttributesUtil.posixPermissionsToOctalMode(posixAttr.permissions());
            mode = mode & 0555;
            fileStat.st_mode.set(FileStat.S_IFREG | mode);
        } else {
            fileStat.st_mode.set(FileStat.S_IFREG | 0444);
        }
        fileAttributesUtil.copyBasicFileAttributesFromNioToFuse(attributes, fileStat);
        return 0;
    }

    private int getDirectoryAttr(BasicFileAttributes attributes, FileStat fileStat) {
        if (attributes instanceof PosixFileAttributes posixAttr) {
            long mode = fileAttributesUtil.posixPermissionsToOctalMode(posixAttr.permissions());
            mode = mode & 0555;
            fileStat.st_mode.set(FileStat.S_IFDIR | mode);
        } else {
            fileStat.st_mode.set(FileStat.S_IFDIR | 0555);
        }
        fileAttributesUtil.copyBasicFileAttributesFromNioToFuse(attributes, fileStat);
        return 0;
    }

    private int getSymbolicLinkAttr(BasicFileAttributes attributes, FileStat fileStat) {
        if (attributes instanceof PosixFileAttributes posixAttr) {
            long mode = fileAttributesUtil.posixPermissionsToOctalMode(posixAttr.permissions());
            mode = mode & 0555;
            fileStat.st_mode.set(FileStat.S_IFLNK | mode);
        } else {
            fileStat.st_mode.set(FileStat.S_IFLNK | 0555);
        }
        fileAttributesUtil.copyBasicFileAttributesFromNioToFuse(attributes, fileStat);
        return 0;
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
