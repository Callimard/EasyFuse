package org.callimard.easyfuse.core.lock;

import org.callimard.easyfuse.core.FuseManager;

import java.nio.file.Path;

public interface PathLockManager extends FuseManager {

    PathLock getLock(Path path);
}
