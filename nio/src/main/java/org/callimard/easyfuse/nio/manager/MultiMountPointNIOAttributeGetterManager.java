package org.callimard.easyfuse.nio.manager;

import jnr.posix.util.Platform;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.callimard.easyfuse.nio.pathrecover.PhysicalPathRecover;
import org.callimard.easyfuse.nio.directory.DirectoryAttributeGetter;
import org.callimard.easyfuse.nio.file.FileAttributeGetter;
import org.callimard.easyfuse.nio.link.LinkAttributeGetter;
import ru.serce.jnrfuse.struct.FileStat;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Singleton
public class MultiMountPointNIOAttributeGetterManager extends NIOAttributeGetterManager {

    // Constructors.

    @Inject
    public MultiMountPointNIOAttributeGetterManager(@NonNull PhysicalPathRecover pathRecover, @NonNull FileAttributeGetter fileAttributeGetter,
                                                    @NonNull DirectoryAttributeGetter directoryAttributeGetter,
                                                    @NonNull LinkAttributeGetter linkAttributeGetter) {
        super(pathRecover, fileAttributeGetter, directoryAttributeGetter, linkAttributeGetter);
    }

    // Methods.

    @Override
    public int getAttribute(String path, FileStat stat) {
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
            return super.getAttribute(path, stat);
        }
    }

    private boolean isFuseRootPath(Path path) {
        return path.getNameCount() == 0;
    }
}
