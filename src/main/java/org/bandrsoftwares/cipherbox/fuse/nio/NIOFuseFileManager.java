package org.bandrsoftwares.cipherbox.fuse.nio;

import com.google.common.collect.Sets;
import jnr.constants.platform.OpenFlags;
import jnr.ffi.Pointer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bandrsoftwares.cipherbox.fuse.FuseFileManager;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.struct.FuseFileInfo;
import ru.serce.jnrfuse.struct.Timespec;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Set;

@Slf4j
public abstract class NIOFuseFileManager extends NIOFuseManager implements FuseFileManager {

    // Constants.

    private static final long U_TIME_NOW = -1L; // https://github.com/apple/darwin-xnu/blob/xnu-4570.1.46/bsd/sys/stat.h#L538
    private static final long U_TIME_OMIT = -2L; // https://github.com/apple/darwin-xnu/blob/xnu-4570.1.46/bsd/sys/stat.h#L5

    // Variables.

    @NonNull
    private final FileReferenceFactory fileFactory;

    @NonNull
    private final FileAttributesUtil fileAttributesUtil;

    // Constructors.

    protected NIOFuseFileManager(@NonNull PhysicalPathRecover pathRecover, @NonNull FileReferenceFactory fileFactory,
                                 @NonNull FileAttributesUtil fileAttributesUtil) {
        super(pathRecover);
        this.fileFactory = fileFactory;
        this.fileAttributesUtil = fileAttributesUtil;
    }

    // Methods.

    @Override
    public int create(String path, long mode, FuseFileInfo fi) {
        Set<OpenOption> openOptions = getOpenOptions(fi);
        Path physicalPath = getPathRecover().recover(Paths.get(path));
        try {
            log.trace("Create and open file {}", path);

            long fileHandle;
            FileStore fileStore = Files.getFileStore(physicalPath.getRoot());
            if (fileStore.supportsFileAttributeView(PosixFileAttributeView.class)) {
                FileAttribute<?> attrs = PosixFilePermissions.asFileAttribute(fileAttributesUtil.octalModeToPosixPermissions(mode));
                fileHandle = fileFactory.create(physicalPath, openOptions, attrs);
            } else {
                fileHandle = fileFactory.create(physicalPath, openOptions);
            }
            fi.fh.set(fileHandle);
            return 0;
        } catch (NotFileException e) {
            log.warn("Fail to create file " + physicalPath + " because it is not a file", e);
            return -ErrorCodes.EISDIR();
        } catch (FileAlreadyExistsException e) {
            log.warn("Fail to create file " + physicalPath + " because it already exists", e);
            return -ErrorCodes.EEXIST();
        } catch (AccessDeniedException e) {
            log.warn("Fail to create file " + physicalPath + " because access denied", e);
            return -ErrorCodes.EACCES();
        } catch (IOException e) {
            log.error("Fail to create file " + physicalPath + " due to IO error", e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int open(String path, FuseFileInfo fi) {
        Set<OpenOption> openOptions = getOpenOptions(fi);
        Path physicalPath = getPathRecover().recover(Paths.get(path));

        try {
            log.trace("Open file {}", path);
            long fileHandle = fileFactory.open(physicalPath, openOptions);
            fi.fh.set(fileHandle);
            return 0;
        } catch (NotFileException e) {
            log.warn("Fail to open file " + physicalPath + " because it is not a file", e);
            return -ErrorCodes.EISDIR();
        } catch (NoSuchFileException e) {
            log.warn("Fail to open file " + physicalPath + " file does not exists", e);
            return -ErrorCodes.ENOENT();
        } catch (AccessDeniedException e) {
            log.warn("Fail to open file " + physicalPath + " because access denied", e);
            return -ErrorCodes.EACCES();
        } catch (IOException e) {
            log.error("Fail to open file " + physicalPath + " due to IO error", e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int read(FuseFileInfo fi, Pointer buf, long size, long offset) {
        try {
            log.trace("Read file {} from offset {} and {} bytes to read", fi.fh.longValue(), offset, size);
            FileReference fileReference = fileFactory.get(fi.fh.longValue());
            return fileReference.read(buf, size, offset);
        } catch (ClosedChannelException e) {
            log.warn("Fail to read file " + fi.fh.longValue() + " because already closed channel", e);
            return -ErrorCodes.EBADF();
        } catch (IOException e) {
            log.error("Fail to read file " + fi.fh.longValue() + " due to IO error", e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int write(FuseFileInfo fi, Pointer buf, long size, long offset) {
        try {
            log.trace("Write file {} from offset {} and {} bytes to write", fi.fh.longValue(), offset, size);
            FileReference fileReference = fileFactory.get(fi.fh.longValue());
            return fileReference.write(buf, size, offset);
        } catch (ClosedChannelException e) {
            log.warn("Fail to write file " + fi.fh.longValue() + " because already closed channel", e);
            return -ErrorCodes.EBADF();
        } catch (IOException e) {
            log.error("Fail to write file " + fi.fh.longValue() + " due to IO error", e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int truncate(String path, long size) {
        Path physicalPath = getPathRecover().recover(Paths.get(path));
        log.trace("Truncate file {} with the size {}", path, size);
        try (FileChannel fileChannel = FileChannel.open(physicalPath, StandardOpenOption.WRITE)) {
            fileChannel.truncate(size);
            return 0;
        } catch (NoSuchFileException e) {
            log.warn("Fail to truncate file " + physicalPath + " file does not exists", e);
            return -ErrorCodes.ENOENT();
        } catch (IOException e) {
            log.error("Fail to truncate file " + physicalPath + " due to IO error", e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int ftruncate(FuseFileInfo fi, long size) {
        try {
            log.trace("FTruncate file {} with the size {}", fi.fh.longValue(), size);
            FileReference fileReference = fileFactory.get(fi.fh.longValue());
            fileReference.truncate(size);
            return 0;
        } catch (ClosedChannelException e) {
            log.warn("Fail to ftruncate file " + fi.fh.longValue() + " because already closed channel", e);
            return -ErrorCodes.EBADF();
        } catch (IOException e) {
            log.error("Fail to ftruncate file " + fi.fh.longValue() + " due to IO error", e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int utimens(String path, Timespec[] timeSpec) {
        Path physicalPath = getPathRecover().recover(Paths.get(path));

        log.trace("Utimens file {}", path);

        if (timeSpec.length != 2) {
            return -ErrorCodes.EINVAL();
        }

        try {
            Timespec lastAccessSpec = timeSpec[0];
            Timespec lastModificationSpec = timeSpec[1];

            FileTime lastAccessTime = toFileTime(lastAccessSpec);
            FileTime lastModificationTime = toFileTime(lastModificationSpec);
            BasicFileAttributeView view = Files.getFileAttributeView(physicalPath, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
            view.setTimes(lastAccessTime, lastModificationTime, null);
            return 0;
        } catch (DateTimeException | ArithmeticException e) {
            log.warn("Fail to utimens file " + physicalPath + " because invalid arguments", e);
            return -ErrorCodes.EINVAL();
        } catch (NoSuchFileException e) {
            log.warn("Fail to utimens file " + physicalPath + " file does not exists", e);
            return -ErrorCodes.ENOENT();
        } catch (IOException e) {
            log.error("Fail to utimens file " + physicalPath + " due to IO error", e);
            return -ErrorCodes.EIO();
        }
    }

    private FileTime toFileTime(Timespec timespec) {
        long seconds = timespec.tv_sec.longValue();
        long nanoseconds = timespec.tv_nsec.longValue();
        if (nanoseconds == U_TIME_NOW) {
            return FileTime.from(Instant.now());
        } else if (nanoseconds == U_TIME_OMIT) {
            return null;
        } else {
            return FileTime.from(Instant.ofEpochSecond(seconds, nanoseconds));
        }
    }

    @Override
    public int fsync(FuseFileInfo fi, boolean metadata) {
        try {
            log.trace("FSync file {} with metadata {}", fi.fh.longValue(), metadata);
            FileReference fileReference = fileFactory.get(fi.fh.longValue());
            fileReference.fsync(metadata);
            return 0;
        } catch (ClosedChannelException e) {
            log.warn("Fail to fsync file " + fi.fh.longValue() + " because already closed channel", e);
            return -ErrorCodes.EBADF();
        } catch (IOException e) {
            log.error("Fail to fsync file " + fi.fh.longValue() + " due to IO error", e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int release(FuseFileInfo fi) {
        try {
            log.trace("Release file {}", fi.fh.longValue());
            fileFactory.close(fi.fh.longValue());
            return 0;
        } catch (ClosedChannelException e) {
            log.warn("Fail to release file " + fi.fh.longValue() + " because already closed channel", e);
            return -ErrorCodes.EBADF();
        } catch (IOException e) {
            log.error("Fail to release file " + fi.fh.longValue() + " due to IO error", e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public void close() {
        try {
            fileFactory.closeAll();
        } catch (IOException e) {
            log.error("IOException during close of all files", e);
        }
    }

    private Set<OpenOption> getOpenOptions(FuseFileInfo fi) {
        return fuseOpenFlagsToNioOpenOption(extractOpenFlags(fi.flags.longValue()));
    }

    private Set<OpenFlags> extractOpenFlags(long mask) {
        Set<OpenFlags> res = Sets.newHashSet();
        for (OpenFlags openFlag : OpenFlags.values()) {
            long openFlagValue = openFlag.longValue();
            if ((openFlagValue & mask) == openFlagValue) {
                res.add(openFlag);
            }
        }

        return res;
    }

    private Set<OpenOption> fuseOpenFlagsToNioOpenOption(Set<OpenFlags> openFlags) {
        Set<OpenOption> res = Sets.newHashSet();

        if (openFlags.contains(OpenFlags.O_RDWR)) {
            res.add(StandardOpenOption.READ);
            res.add(StandardOpenOption.WRITE);
        } else if (openFlags.contains(OpenFlags.O_WRONLY)) {
            res.add(StandardOpenOption.WRITE);
        } else if (openFlags.contains(OpenFlags.O_RDONLY)) {
            res.add(StandardOpenOption.READ);
        }

        if (openFlags.contains(OpenFlags.O_APPEND)) {
            res.add(StandardOpenOption.APPEND);
        }

        if (openFlags.contains(OpenFlags.O_TRUNC)) {
            res.add(StandardOpenOption.TRUNCATE_EXISTING);
        }

        return res;
    }
}
