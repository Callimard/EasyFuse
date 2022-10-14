package org.callimard.easyfuse.core;

import java.nio.file.Path;

public interface FuseLockManager extends FuseManager {

    PathLock getLock(Path path);
}
