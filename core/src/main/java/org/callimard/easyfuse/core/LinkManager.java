package org.callimard.easyfuse.core;

import jnr.ffi.Pointer;

public interface LinkManager extends FuseManager {

    int symlink(String targetPath, String linkPath);

    int readlink(String path, Pointer buf, long size);
}
