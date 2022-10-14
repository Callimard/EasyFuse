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
     *
     * @throws EntryPointNotFoundException if the entry point is not found
     */
    Path getPhysicalDirectoryOf(String entryPointName);

    class EntryPointNotFoundException extends RuntimeException {

        // Constructors.

        public EntryPointNotFoundException(String entryPointName) {
            super("Entry Point with the name " + entryPointName + " has been not found");
        }
    }
}
