package org.callimard.easyfuse.core;

public interface TruncateManager extends FuseManager {

    int truncate(String path, long size);

}
