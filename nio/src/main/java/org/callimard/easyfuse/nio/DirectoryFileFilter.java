package org.callimard.easyfuse.nio;

import java.nio.file.Path;

public interface DirectoryFileFilter {

    /**
     * @param directoryPath the base directory
     * @param fileToFilter  the file into the directory to filter
     *
     * @return true if the given file can be accepted, else false.
     */
    boolean accept(Path directoryPath, Path fileToFilter);
}
