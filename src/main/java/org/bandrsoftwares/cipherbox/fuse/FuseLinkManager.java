package org.bandrsoftwares.cipherbox.fuse;

import jnr.ffi.Pointer;

public interface FuseLinkManager extends FuseManager{

    int symlink(String targetPath, String linkPath);

    int readlink(String path, Pointer buf, long size);
}
