package org.bandrsoftwares.cipherbox.fuse;

import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.Statvfs;

public interface FuseFSActionManager {

    int statfs(String path, Statvfs stbuf);

    int access(String path, int mask);

    int getattr(String path, FileStat stat);

    int rename(String oldPath, String newPath);

    int unlink(String path);

    default int chown(String path, long uid, long gid) {
        // Use static fs operations
        return 0;
    }

    int chmod(String path, long mode);
}
