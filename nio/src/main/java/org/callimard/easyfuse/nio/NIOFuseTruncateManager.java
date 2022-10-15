package org.callimard.easyfuse.nio;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.callimard.easyfuse.core.FuseTruncateManager;
import ru.serce.jnrfuse.ErrorCodes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@Singleton
public class NIOFuseTruncateManager extends NIOFuseManager implements FuseTruncateManager {

    // Constructors.

    @Inject
    public NIOFuseTruncateManager(@NonNull PhysicalPathRecover pathRecover) {
        super(pathRecover);
    }

    // Methods.

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
}
