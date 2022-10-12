package org.bandrsoftwares.cipherbox.fuse.nio;

import lombok.Getter;
import lombok.NonNull;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.file.Path;

/**
 * Implementation of {@link PhysicalPathRecover} which is link to only ONE physical directory. This directory will be the root of the File System and
 * all manipulated files will be in it.
 * <p>
 * The method {@link #recover(Path)} only call {@link Path#resolve(Path)} to create the physical path of a fuse path.
 */
@Getter
@Singleton
public class OneDirectoryLinkPathRecover implements PhysicalPathRecover {

    // Variables.

    @NonNull
    private final Path rootDirectory;

    // Constructors.

    @Inject
    OneDirectoryLinkPathRecover(@RootDirectory @NonNull Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    // Methods.

    @Override
    public Path recover(@NonNull Path fusePath) {
        return rootDirectory.resolve(fusePath);
    }

    // Inner classes.

    @Documented
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface RootDirectory {
    }
}
