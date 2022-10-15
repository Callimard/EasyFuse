package org.callimard.easyfuse.nio;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.callimard.easyfuse.core.FuseAttributeGetterManager;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.struct.FileStat;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;

@Slf4j
@Singleton
public class NIOFuseAttributeGetterManager extends NIOFuseManager implements FuseAttributeGetterManager {

    // Variables.

    @NonNull
    private final NIOFileAttributeGetter fileAttributeGetter;

    @NonNull
    private final NIODirectoryAttributeGetter directoryAttributeGetter;

    @NonNull
    private final NIOLinkAttributeGetter linkAttributeGetter;

    // Constructors.

    @Inject
    public NIOFuseAttributeGetterManager(@NonNull PhysicalPathRecover pathRecover, @NonNull NIOFileAttributeGetter fileAttributeGetter,
                                         @NonNull NIODirectoryAttributeGetter directoryAttributeGetter,
                                         @NonNull NIOLinkAttributeGetter linkAttributeGetter) {
        super(pathRecover);
        this.fileAttributeGetter = fileAttributeGetter;
        this.directoryAttributeGetter = directoryAttributeGetter;
        this.linkAttributeGetter = linkAttributeGetter;
    }


    // Methods.

    @Override
    public int getAttribute(String path, FileStat stat) {
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
                return fileAttributeGetter.getAttribute(attrs, stat, getFuseFS());
            } else if (attrs.isDirectory()) {
                return directoryAttributeGetter.getAttribute(attrs, stat, getFuseFS());
            } else if (attrs.isSymbolicLink()) {
                return linkAttributeGetter.getAttribute(attrs, stat, getFuseFS());
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
}
