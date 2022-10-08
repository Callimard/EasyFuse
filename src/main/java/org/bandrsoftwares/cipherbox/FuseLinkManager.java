package org.bandrsoftwares.cipherbox;

import jnr.ffi.Pointer;

public interface FuseLinkManager {

    int symlink(String oldPath, String newPath);

    int readlink(String path, Pointer buf, long size);
}
