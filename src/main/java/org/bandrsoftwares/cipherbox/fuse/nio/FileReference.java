package org.bandrsoftwares.cipherbox.fuse.nio;

import jnr.ffi.Pointer;

import java.io.IOException;

public interface FileReference {

    int read(Pointer buf, long size, long offset) throws IOException;

    int write(Pointer buf, long size, long offset) throws IOException;

    void truncate(long size) throws IOException;

    void fsync(boolean metadata) throws IOException;
}
