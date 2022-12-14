package org.callimard.easyfuse.core;

import ru.serce.jnrfuse.struct.Statvfs;

public interface GlobalActionManager extends FuseManager {

    int statfs(String path, Statvfs stbuf);

    int access(String path, int mask);

    int rename(String oldPath, String newPath);

    int unlink(String path);

    default int chown(String path, long uid, long gid) {
        // Use static fs operations
        return 0;
    }

    int chmod(String path, long mode);
}
