package org.bandrsoftwares.cipherbox.fuse;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Lock associated to a specific {@link java.nio.file.Path}.
 * <p>
 * This class implements {@link AutoCloseable} to be used in "Try with resources" statement. In that way, methods {@link #lockToRead()} and
 * {@link #lockToWrite()} can be call in a try and the developer does not need to be preoccupied by lock release.
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
public class PathLock {

    // Variables.

    @NonNull
    private final Path path;

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    private final CloseableLock readPathLock = new CloseableLock(rwLock.readLock());
    private final CloseableLock writePathLock = new CloseableLock(rwLock.writeLock());

    // Methods.

    public CloseableLock lockToRead() {
        readPathLock.lock();
        return readPathLock;
    }

    public CloseableLock lockToWrite() {
        writePathLock.lock();
        return writePathLock;
    }
}
