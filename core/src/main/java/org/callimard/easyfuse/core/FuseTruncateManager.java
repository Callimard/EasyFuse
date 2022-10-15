package org.callimard.easyfuse.core;

public interface FuseTruncateManager extends FuseManager {

    int truncate(String path, long size);

}
