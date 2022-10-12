package org.bandrsoftwares.cipherbox.fuse;

import jnr.ffi.Pointer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import ru.serce.jnrfuse.ErrorCodes;
import ru.serce.jnrfuse.FuseFillDir;
import ru.serce.jnrfuse.FuseStubFS;
import ru.serce.jnrfuse.struct.FileStat;
import ru.serce.jnrfuse.struct.FuseFileInfo;
import ru.serce.jnrfuse.struct.Statvfs;
import ru.serce.jnrfuse.struct.Timespec;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Paths;

@Slf4j
@Singleton
public class ConcreteFuseFS extends FuseStubFS {

    // Variables.

    @NonNull
    private final FuseLockManager fuseLockManager;

    @NonNull
    private final FuseFileManager fileManager;

    @NonNull
    private final FuseDirectoryManager directoryManager;

    @NonNull
    private final FuseLinkManager linkManager;

    @NonNull
    private final FuseFSActionManager fsActionManager;

    // Constructors.

    @Inject
    ConcreteFuseFS(@NonNull FuseLockManager fuseLockManager, @NonNull FuseFileManager fileManager, @NonNull FuseDirectoryManager directoryManager,
                   @NonNull FuseLinkManager linkManager, @NonNull FuseFSActionManager fsActionManager) {
        this.fuseLockManager = fuseLockManager;
        this.fileManager = fileManager;
        this.directoryManager = directoryManager;
        this.linkManager = linkManager;
        this.fsActionManager = fsActionManager;
    }

    // Methods.

    // ------------- Directory -------------

    @Override
    public int mkdir(String path, long mode) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return directoryManager.mkdir(path, mode);
        } catch (RuntimeException e) {
            log.error("Fail to create dir for " + path, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int readdir(String path, Pointer buf, FuseFillDir filter, long offset, FuseFileInfo fi) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockRead()) {
            return directoryManager.readdir(path, buf, filter, offset, fi);
        } catch (RuntimeException e) {
            log.error("Fail to readdir for " + path, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int rmdir(String path) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return directoryManager.rmdir(path);
        } catch (RuntimeException e) {
            log.error("Fail to remove dir for " + path, e);
            return -ErrorCodes.EIO();
        }
    }

    // ------------- File -------------

    @Override
    public int create(String path, long mode, FuseFileInfo fi) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return fileManager.create(path, mode, fi);
        } catch (RuntimeException e) {
            log.error("Fail to create and open file for " + path + " in mode " + mode, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int open(String path, FuseFileInfo fi) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockRead()) {
            return fileManager.open(path, fi);
        } catch (RuntimeException e) {
            log.error("Fail to open file for " + path, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int read(String path, Pointer buf, long size, long offset, FuseFileInfo fi) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockRead()) {
            return fileManager.read(fi, buf, size, offset);
        } catch (RuntimeException e) {
            log.error("Fail to read file for " + path + " with FH " + fi.fh, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int write(String path, Pointer buf, long size, long offset, FuseFileInfo fi) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return fileManager.write(fi, buf, size, offset);
        } catch (RuntimeException e) {
            log.error("Fail to write file for " + path + " with FH " + fi.fh, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int truncate(String path, long size) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return fileManager.truncate(path, size);
        } catch (RuntimeException e) {
            log.error("Fail to to truncate file for " + path, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int ftruncate(String path, long size, FuseFileInfo fi) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return fileManager.ftruncate(fi, size);
        } catch (RuntimeException e) {
            log.error("Fail to ftruncate file for " + path + " with FH " + fi.fh, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int utimens(String path, Timespec[] timeSpec) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return fileManager.utimens(path, timeSpec);
        } catch (RuntimeException e) {
            log.error("Fail to utimens file for " + path, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int fsync(String path, int isDataSync, FuseFileInfo fi) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return fileManager.fsync(fi, isDataSync == 0);
        } catch (RuntimeException e) {
            log.error("Fail to fsync file for " + path + " <with FH " + fi.fh, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int release(String path, FuseFileInfo fi) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return fileManager.release(fi);
        } catch (RuntimeException e) {
            log.error("Fail to release file for " + path + " with FH " + fi.fh, e);
            return -ErrorCodes.EIO();
        }
    }

    // ------------- Link -------------

    @Override
    public int symlink(String targetPath, String linkPath) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(linkPath)).lockWrite()) {
            return linkManager.symlink(targetPath, linkPath);
        } catch (RuntimeException e) {
            log.error("Fail to create symbolic link for targetPath " + targetPath + " and linkPath " + linkPath, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int readlink(String path, Pointer buf, long size) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockRead()) {
            return linkManager.readlink(path, buf, size);
        } catch (RuntimeException e) {
            log.error("Fail to read link for " + path, e);
            return -ErrorCodes.EIO();
        }
    }

    // ------------- General -------------

    @Override
    public int statfs(String path, Statvfs stbuf) {
        return fsActionManager.statfs(path, stbuf);
    }

    @Override
    public int access(String path, int mask) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockRead()) {
            return fsActionManager.access(path, mask);
        } catch (RuntimeException e) {
            log.error("Fail to get access for " + path + " and mask " + mask, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int getattr(String path, FileStat stat) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockRead()) {
            return fsActionManager.getattr(path, stat);
        } catch (RuntimeException e) {
            log.error("Fail to get attributes for " + path, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int rename(String oldPath, String newPath) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(oldPath)).lockWrite();
             PathLock ignoredNewPath = fuseLockManager.getLock(Paths.get(newPath)).lockWrite()) {
            return fsActionManager.rename(oldPath, newPath);
        } catch (RuntimeException e) {
            log.error("Fail to rename file with oldPath " + oldPath + " and newPath " + newPath, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int unlink(String path) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return fsActionManager.unlink(path);
        } catch (RuntimeException e) {
            log.error("Fail to delete file " + path, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int chown(String path, long uid, long gid) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return fsActionManager.chown(path, uid, gid);
        } catch (RuntimeException e) {
            log.error("Fail to chown " + path + " with uid " + uid + " and gid " + gid, e);
            return -ErrorCodes.EIO();
        }
    }

    @Override
    public int chmod(String path, long mode) {
        try (PathLock ignored = fuseLockManager.getLock(Paths.get(path)).lockWrite()) {
            return fsActionManager.chmod(path, mode);
        } catch (RuntimeException e) {
            log.error("Fail to chmod " + path + " with mode " + mode, e);
            return -ErrorCodes.EIO();
        }
    }

    // ------------- FS -------------

    @Override
    public Pointer init(Pointer conn) {
        return conn;
    }

    @Override
    public void destroy(Pointer initResult) {
        fileManager.close();
    }
}
