package org.callimard.easyfuse.nio.file;

import lombok.NonNull;
import org.callimard.easyfuse.core.EasyFuseFS;
import org.callimard.easyfuse.nio.attribute.AttributeUtil;
import org.callimard.easyfuse.nio.attribute.NIOAttributeGetter;
import ru.serce.jnrfuse.struct.FileStat;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributes;

@Singleton
public class NIOFileAttributeGetter extends NIOAttributeGetter implements FileAttributeGetter {

    // Constructors.

    @Inject
    public NIOFileAttributeGetter(@NonNull AttributeUtil attributeUtil) {
        super(attributeUtil);
    }

    // Methods.

    @Override
    public int getAttribute(Path path, BasicFileAttributes attributes, FileStat stat, EasyFuseFS fuseFS) {
        if (attributes instanceof PosixFileAttributes posixAttr) {
            long mode = getAttributeUtil().posixPermissionsToOctalMode(posixAttr.permissions());
            mode = mode & 0555;
            stat.st_mode.set(FileStat.S_IFREG | mode);
        } else {
            stat.st_mode.set(FileStat.S_IFREG | 0777);
        }
        getAttributeUtil().copyBasicFileAttributesFromNioToFuse(attributes, stat, fuseFS);
        return 0;
    }
}
