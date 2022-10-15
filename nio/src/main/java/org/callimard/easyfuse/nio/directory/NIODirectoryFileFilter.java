package org.callimard.easyfuse.nio.directory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;

@Singleton
public class NIODirectoryFileFilter implements DirectoryFileFilter {

    // Constructors.

    @Inject
    public NIODirectoryFileFilter() {
        // Nothing.
    }

    // Methods.

    /**
     * Accept all files except files which starts with a "." or which are a "desktop.ini" file.
     *
     * @param directoryPath the base directory
     * @param fileToFilter  the file into the directory to filter
     *
     * @return true for all files except file starting with a "." or which are a "desktop.ini" file.
     */
    @Override
    public boolean accept(Path directoryPath, Path fileToFilter) {
        return !fileToFilter.getFileName().toString().startsWith(".") && !fileToFilter.getFileName().toString().equals("desktop.ini");
    }
}
