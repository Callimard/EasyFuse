package org.callimard.easyfuse.core;

import ru.serce.jnrfuse.struct.FileStat;

public interface AttributeGetterManager extends FuseManager {

    int getAttribute(String path, FileStat stat);
}
