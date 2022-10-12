package org.bandrsoftwares.cipherbox.fuse;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Lock associated to a specific {@link java.nio.file.Path}.
 * <p>
 * This class implements {@link AutoCloseable} to be used in "Try with resources" statement. In that way, methods {@link #lockRead()} and
 * {@link #lockWrite()} can be call in a try and the developer does not need to be preoccupied by lock release.
 * <p>
 * Here an example how to do it:
 * <pre>{@code
 * PathLock pLock = ...;
 * try (PathLock ignored = pLock.lockRead()) {
 *     // Do something
 * }
 * }</pre>
 */
@Getter
@RequiredArgsConstructor
public class PathLock implements Closeable {

    // Variables.

    @NonNull
    private final Path path;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    // Methods.

    public PathLock lockRead() {
        rwLock.readLock().lock();
        return this;
    }

    public PathLock lockWrite() {
        rwLock.writeLock().lock();
        return this;
    }

    /**
     * Unlock all lock (read and write).
     */
    @Override
    public void close() {
        rwLock.readLock().unlock();
        rwLock.writeLock().unlock();
    }
}
