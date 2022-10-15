package org.callimard.easyfuse.nio;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.callimard.easyfuse.core.UtimensManager;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.struct.Timespec;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.time.DateTimeException;
import java.time.Instant;

@Slf4j
@Singleton
public class NIOUtimensManager extends NIOFuseManager implements UtimensManager {

    // Constants.

    private static final long U_TIME_NOW = -1L; // https://github.com/apple/darwin-xnu/blob/xnu-4570.1.46/bsd/sys/stat.h#L538
    private static final long U_TIME_OMIT = -2L; // https://github.com/apple/darwin-xnu/blob/xnu-4570.1.46/bsd/sys/stat.h#L5

    // Constructors.

    @Inject
    public NIOUtimensManager(@NonNull PhysicalPathRecover pathRecover) {
        super(pathRecover);
    }

    // Methods.

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
}
