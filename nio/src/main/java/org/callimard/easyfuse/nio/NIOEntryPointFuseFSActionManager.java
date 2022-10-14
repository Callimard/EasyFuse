package org.callimard.easyfuse.nio;

import jnr.posix.util.Platform;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.struct.FileStat;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Singleton
public class NIOEntryPointFuseFSActionManager extends NIOFuseFSActionManager {

    // Constructors.

    @Inject
    NIOEntryPointFuseFSActionManager(@NonNull SeveralEntryPointPathRecover pathRecover, @NonNull FileAttributesUtil fileAttributesUtil) {
        super(pathRecover, fileAttributesUtil);
    }

    // Methods.

    @Override
    public int chown(String path, long uid, long gid) {
        var fusePath = Paths.get(path);
        if (isEntryPointRoot(fusePath)) {
            log.warn("Impossible to chown the path {} because it is a Entry Point root", fusePath);
            return -ErrorCodes.EACCES();
        } else {
            return super.chown(path, uid, gid);
        }
    }

    @Override
    public int getattr(String path, FileStat stat) {
        var fusePath = Paths.get(path);
        if (isFuseRootPath(fusePath)) {
            log.trace("Get attr of fuse root path {}", path);
            stat.st_mode.set(FileStat.S_IFDIR | 0777);
            stat.st_uid.set(getFuseFS().getContext().uid.get());
            stat.st_gid.set(getFuseFS().getContext().gid.get());

            stat.st_nlink.set(1);
            if (Platform.IS_MAC) {
                stat.st_flags.set(0);
                stat.st_gen.set(0);
            }

            return 0;
        } else {
            return super.getattr(path, stat);
        }
    }

    @Override
    public int rename(String oldPath, String newPath) {
        var fusePath = Paths.get(oldPath);
        if (isEntryPointRoot(fusePath)) {
            log.warn("Impossible to rename the path {} because it is a Entry Point root", fusePath);
            return -ErrorCodes.EACCES();
        } else {
            return super.rename(oldPath, newPath);
        }
    }

    @Override
    public int unlink(String path) {
        var fusePath = Paths.get(path);
        if (isEntryPointRoot(fusePath)) {
            log.warn("Impossible to unlink the path {} because it is a Entry Point root", fusePath);
            return -ErrorCodes.EACCES();
        } else {
            return super.unlink(path);
        }
    }

    @Override
    public int chmod(String path, long mode) {
        var fusePath = Paths.get(path);
        if (isEntryPointRoot(fusePath)) {
            log.warn("Impossible to chmod the path {} because it is a Entry Point root", fusePath);
            return -ErrorCodes.EACCES();
        } else {
            return super.chmod(path, mode);
        }
    }

    private boolean isFuseRootPath(Path path) {
        return path.getNameCount() == 0;
    }

    private boolean isEntryPointRoot(Path path) {
        return path.getNameCount() == 1;
    }
}
