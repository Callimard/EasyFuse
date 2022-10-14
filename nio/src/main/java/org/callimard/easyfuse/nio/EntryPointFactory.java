package org.callimard.easyfuse.nio;

import lombok.NonNull;

import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

    /**
     * Add the entry point. The entry point is added only if the given name is unique and not already used by another entry point and if the given
     * directory is a directory path and exists.
     * <p>
     * Returns true if the entry point verify all previous conditions, else false.
     *
     * @param entryPointName the entry point name
     * @param directory      the physical path of the entry point directory
     *
     * @return true if the entry point has been added, else false.
     */
    boolean addEntryPoint(@NonNull String entryPointName, @NonNull Path directory);

    class EntryPointNotFoundException extends RuntimeException {

        // Constructors.

        public EntryPointNotFoundException(String entryPointName) {
            super("Entry Point with the name " + entryPointName + " has been not found");
        }
    }

    @Documented
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface EntryPointList {
    }
}
