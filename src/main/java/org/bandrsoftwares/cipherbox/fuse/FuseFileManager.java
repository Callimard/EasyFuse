package org.bandrsoftwares.cipherbox.fuse;

import jnr.ffi.Pointer;
import ru.serce.jnrfuse.struct.FuseFileInfo;
import ru.serce.jnrfuse.struct.Timespec;

public interface FuseFileManager {

    int create(String path, long mode, FuseFileInfo fi);

    int open(String path, FuseFileInfo fi);

    int read(FuseFileInfo fi, Pointer buf, long size, long offset);

    int write(FuseFileInfo fi, Pointer buf, long size, long offset);

    int truncate(String path, long size);

    int ftruncate(FuseFileInfo fi, long size);

    int utimens(String path, Timespec[] timeSpec);

    int fsync(FuseFileInfo fi, boolean metadata);

    int release(FuseFileInfo fi);

    void close();
}
