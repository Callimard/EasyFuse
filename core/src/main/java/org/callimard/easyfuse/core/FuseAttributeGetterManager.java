package org.callimard.easyfuse.core;

import ru.serce.jnrfuse.struct.FileStat;

public interface FuseAttributeGetterManager extends FuseManager {

    int getAttribute(String path, FileStat stat);
}
