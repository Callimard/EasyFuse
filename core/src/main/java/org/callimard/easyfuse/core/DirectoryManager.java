package org.callimard.easyfuse.core;

import jnr.ffi.Pointer;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.struct.FuseFileInfo;

public interface DirectoryManager extends FuseManager {

    int mkdir(String path, long mode);

    int readdir(String path, Pointer buf, FuseFillDir filter, long offset, FuseFileInfo fi);

    int rmdir(String path);
}
