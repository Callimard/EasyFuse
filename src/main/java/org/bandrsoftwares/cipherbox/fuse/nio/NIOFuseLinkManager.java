package org.bandrsoftwares.cipherbox.fuse.nio;

import jnr.ffi.Pointer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bandrsoftwares.cipherbox.fuse.FuseLinkManager;
import ru.serce.jnrfuse.ErrorCodes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

@Slf4j
@Singleton
public class NIOFuseLinkManager extends NIOFuseManager implements FuseLinkManager {

    // Constructors.

    @Inject
    NIOFuseLinkManager(@NonNull PhysicalPathRecover pathRecover) {
        super(pathRecover);
    }

    // Methods.

    @Override
    public int symlink(String targetPath, String linkPath) {
        Path linkPhysicalPath = getPathRecover().recover(Paths.get(linkPath));
        try {
            log.trace("Create symbolic link {} for target {}", linkPath, targetPath);
            Files.createSymbolicLink(linkPhysicalPath, Paths.get(targetPath));
            return 0;
        } catch (FileAlreadyExistsException e) {
            log.warn("Fail to symlink " + linkPath + " with " + linkPath + " because file already exists", e);
            return -ErrorCodes.EEXIST();
        } catch (IOException | RuntimeException e) {
            log.error("Fail to symlink " + linkPath + " with " + linkPath + " due to IO error");
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int readlink(String path, Pointer buf, long size) {
        Path physicalPath = getPathRecover().recover(Paths.get(path));
        try {
            log.trace("Read link {} with size {}", path, size);
            Path target = Files.readSymbolicLink(physicalPath);
            ByteBuffer fuseEncodedTarget = StandardCharsets.UTF_8.encode(target.toString());
            int len = (int) Math.min(fuseEncodedTarget.remaining(), size - 1);
            buf.put(0, fuseEncodedTarget.array(), 0, len);
            buf.putByte(len, (byte) 0x00); // add null terminator
            return 0;
        } catch (NotLinkException | NoSuchFileException e) {
            log.warn("Fail to read link " + physicalPath + " because not a link or a file", e);
            return -ErrorCodes.ENOENT();
        } catch (IOException | RuntimeException e) {
            log.error("Fail to read link " + physicalPath + " due toa IO error", e);
            return -ErrorCodes.EIO();
        }
    }
}
