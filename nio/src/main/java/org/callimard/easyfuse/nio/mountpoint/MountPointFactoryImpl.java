package org.callimard.easyfuse.nio.mountpoint;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@Singleton
public class MountPointFactoryImpl implements MountPointFactory {

    // Variables.
    private final Map<String, Path> mountPointMap = Maps.newConcurrentMap();

    // Constructors.

    @Inject
    public MountPointFactoryImpl(@EntryPointList @Nullable List<MountPoint> mountPoints) {
        if (mountPoints != null) for (MountPoint mountPoint : mountPoints) {
            var added = addMountPoint(mountPoint.name(), mountPoint.directory());
            if (!added) {
                log.warn("Fail to add mount point {} at the MountPointFactoryImpl construction", mountPoint);
            }
        }
    }

    // Methods.

    @Override
    public List<String> mountPointNames() {
        return Lists.newArrayList(mountPointMap.keySet());
    }

    @Override
    public Path getPhysicalDirectoryOf(String mountPointName) {
        Path directory = mountPointMap.get(mountPointName);
        if (directory == null) {
            log.warn("Not found entry point for {}", mountPointName);
            throw new EntryPointNotFoundException(mountPointName);
        }

        return directory;
    }

    @Override
    public boolean addMountPoint(@NonNull String mountPointName, @NonNull Path directory) {
        if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) {
            log.warn("Try to add an entry point with not a directory");
            return false;
        }

        if (mountPointMap.putIfAbsent(mountPointName, directory) != null) {
            log.warn("Try to add an mount point with an already used name {}", mountPointName);
            return false;
        }

        return true;
    }
}
