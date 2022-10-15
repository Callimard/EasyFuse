package org.callimard.easyfuse.nio;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.serce.jnrfuse.ErrorCodes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Singleton
public class NIOEntryPointGlobalActionManager extends NIOFSActionManager {

    // Constructors.

    @Inject
    public NIOEntryPointGlobalActionManager(@NonNull SeveralEntryPointPathRecover pathRecover, @NonNull FileAttributesUtil fileAttributesUtil) {
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

    private boolean isEntryPointRoot(Path path) {
        return path.getNameCount() == 1;
    }
}
