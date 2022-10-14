package org.bandrsoftwares.cipherbox.fuse.nio;

import java.nio.file.Path;
import java.util.List;

public interface EntryPointFactory {

    /**
     * @return a {@link List} which contains all names of each entry points. Never returns null.
     */
    List<String> entryPointNames();

    /**
     * @param entryPointName the entry point name
     *
     * @return the physical directory associated to the entry point
     */
    Path getPhysicalDirectoryOf(String entryPointName);
}
