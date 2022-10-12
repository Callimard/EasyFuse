package org.bandrsoftwares.cipherbox.fuse;

import java.nio.file.Path;

public interface FuseLockManager {

    PathLock getLock(Path path);
}
