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
    private final Map<String, Path> entryPointMap = Maps.newConcurrentMap();

    // Constructors.

    @Inject
    public MountPointFactoryImpl(@EntryPointList @Nullable List<MountPoint> mountPoints) {
        if (mountPoints != null) for (MountPoint mountPoint : mountPoints) {
            addEntryPoint(mountPoint.name(), mountPoint.directory());
        }
    }

    // Methods.

    @Override
    public List<String> entryPointNames() {
        return Lists.newArrayList(entryPointMap.keySet());
    }

    @Override
    public Path getPhysicalDirectoryOf(String entryPointName) {
        Path directory = entryPointMap.get(entryPointName);
        if (directory == null) {
            log.warn("Not found entry point for {}", entryPointName);
            throw new EntryPointNotFoundException(entryPointName);
        }

        return directory;
    }

    @Override
    public boolean addEntryPoint(@NonNull String entryPointName, @NonNull Path directory) {
        if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) {
            log.warn("Try to add an entry point with not a directory");
            return false;
        }

        // TODO Not thread safe

        if (entryPointMap.containsKey(entryPointName)) {
            log.warn("Try to add an entry point with an already used name {}", entryPointName);
            return false;
        }

        entryPointMap.put(entryPointName, directory);
        return true;
    }
}
