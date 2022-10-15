package org.callimard.easyfuse.core;

import jnr.ffi.Pointer;
import ru.serce.jnrfuse.struct.FuseFileInfo;

public interface FileManager extends FuseManager {

    int create(String path, long mode, FuseFileInfo fi);

    int open(String path, FuseFileInfo fi);

    int read(FuseFileInfo fi, Pointer buf, long size, long offset);

    int write(FuseFileInfo fi, Pointer buf, long size, long offset);

    int ftruncate(FuseFileInfo fi, long size);

    int fsync(FuseFileInfo fi, boolean metadata);

    int release(FuseFileInfo fi);

    void close();
}
