package org.bandrsoftwares.cipherbox.fuse.nio;

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Singleton
public class BasicEntryPointFactory implements EntryPointFactory {

    // Variables.

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private final Map<String, Path> entryPointMap = Maps.newHashMap();

    // Constructors.

    @Inject
    BasicEntryPointFactory(@EntryPointList @Nullable List<EntryPoint> entryPoints) {
        if (entryPoints != null) for (EntryPoint entryPoint : entryPoints) {
            addEntryPoint(entryPoint.name(), entryPoint.directory());
        }
    }

    // Methods.

    @Override
    public List<String> entryPointNames() {
        try {
            readLock.lock();
            return Lists.newArrayList(entryPointMap.keySet());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Path getPhysicalDirectoryOf(String entryPointName) {
        try {
            readLock.lock();

            Path directory = entryPointMap.get(entryPointName);
            if (directory == null) throw new EntryPointNotFoundException(entryPointName);

            return directory;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean addEntryPoint(@NonNull String entryPointName, @NonNull Path directory) {
        if (!Files.isDirectory(directory, LinkOption.NOFOLLOW_LINKS)) {
            log.warn("Try to add an entry point with not a directory");
            return false;
        }

        try {
            writeLock.lock();

            if (entryPointMap.containsKey(entryPointName)) {
                log.warn("Try to add an entry point with an already used name {}", entryPointName);
                return false;
            }

            entryPointMap.put(entryPointName, directory);
            return true;
        } finally {
            writeLock.lock();
        }
    }
}
