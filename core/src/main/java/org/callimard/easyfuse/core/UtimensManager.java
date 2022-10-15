package org.callimard.easyfuse.core;

import ru.serce.jnrfuse.struct.Timespec;

public interface UtimensManager extends FuseManager {

    int utimens(String path, Timespec[] timeSpec);
}
