package org.callimard.easyfuse.nio;

import lombok.NonNull;
import org.callimard.easyfuse.core.ConcreteFuseFS;
import ru.serce.jnrfuse.struct.FileStat;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;

@Singleton
public class BasicFileAttributeGetter extends BasicAttributeGetter implements NIOFileAttributeGetter {

    // Constructors.

    @Inject
    public BasicFileAttributeGetter(@NonNull FileAttributesUtil fileAttributesUtil) {
        super(fileAttributesUtil);
    }

    // Methods.

    @Override
    public int getAttribute(BasicFileAttributes attributes, FileStat stat, ConcreteFuseFS fuseFS) {
        if (attributes instanceof PosixFileAttributes posixAttr) {
            long mode = getFileAttributesUtil().posixPermissionsToOctalMode(posixAttr.permissions());
            mode = mode & 0555;
            stat.st_mode.set(FileStat.S_IFREG | mode);
        } else {
            stat.st_mode.set(FileStat.S_IFREG | 0777);
        }
        getFileAttributesUtil().copyBasicFileAttributesFromNioToFuse(attributes, stat, fuseFS);
        return 0;
    }
}
