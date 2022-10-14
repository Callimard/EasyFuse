package org.callimard.easyfuse.nio;

import jnr.ffi.Pointer;

import java.io.IOException;

public interface FileReference extends AutoCloseable {

    int read(Pointer buf, long size, long offset) throws IOException;

    int write(Pointer buf, long size, long offset) throws IOException;

    void truncate(long size) throws IOException;

    void fsync(boolean metadata) throws IOException;
}
