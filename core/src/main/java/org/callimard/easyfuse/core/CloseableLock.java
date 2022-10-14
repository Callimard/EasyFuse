package org.callimard.easyfuse.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@RequiredArgsConstructor
public class CloseableLock implements Lock, Closeable {

    // Variables.

    @NonNull
    private final Lock lock;

    // Methods.

    @Override
    public void lock() {
        lock.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return lock.tryLock();
    }

    @Override
    public boolean tryLock(long time, @NonNull TimeUnit unit) throws InterruptedException {
        return lock.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        lock.unlock();
    }

    @Override
    public Condition newCondition() {
        return lock.newCondition();
    }

    @Override
    public void close() {
        unlock();
    }
}
