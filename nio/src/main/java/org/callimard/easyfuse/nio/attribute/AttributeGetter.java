package org.callimard.easyfuse.nio.attribute;

import org.callimard.easyfuse.core.EasyFuseFS;
import ru.serce.jnrfuse.struct.FileStat;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public interface AttributeGetter {

    int getAttribute(Path path, BasicFileAttributes attributes, FileStat stat, EasyFuseFS fuseFS);
}
