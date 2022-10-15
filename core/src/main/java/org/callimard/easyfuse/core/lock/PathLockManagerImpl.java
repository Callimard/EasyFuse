package org.callimard.easyfuse.core.lock;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.callimard.easyfuse.core.EasyFuseFS;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

@Slf4j
@Singleton
public class PathLockManagerImpl implements PathLockManager {

    // Variables.

    private final CacheLoader<Path, PathLock> lockLoader = new CacheLoader<>() {
        @Override
        public @NonNull PathLock load(@NonNull Path key) {
            return new PathLock(key);
        }
    };

    private final LoadingCache<Path, PathLock> cache = CacheBuilder.newBuilder().build(lockLoader);

    private EasyFuseFS fuseFS;

    // Constructors.

    @Inject
    PathLockManagerImpl() {
        // Nothing.
    }

    // Methods.

    @Override
    public PathLock getLock(Path path) {
        try {
            return cache.get(path);
        } catch (ExecutionException e) {
            log.error("Fail to get lock in cache for the path " + path, e);
            throw new FailLoadingPathLockException(e);
        }
    }

    @Override
    public EasyFuseFS getFuseFS() {
        return fuseFS;
    }

    @Override
    public void init(@NonNull EasyFuseFS fuseFS) {
        if (!hasBeenInitialized())
            this.fuseFS = fuseFS;
    }

    // Inner classes.

    public static final class FailLoadingPathLockException extends RuntimeException {

        // Constructors.

        public FailLoadingPathLockException(Throwable cause) {
            super(cause);
        }
    }
}
