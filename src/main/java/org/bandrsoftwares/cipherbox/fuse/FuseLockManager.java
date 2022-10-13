package org.bandrsoftwares.cipherbox.fuse;

import java.nio.file.Path;

public interface FuseLockManager extends FuseManager {

    PathLock getLock(Path path);
}
