package org.callimard.easyfuse.nio.pathrecover;

import com.google.common.base.CharMatcher;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implementation of {@link PhysicalPathRecover} which is link to only ONE physical directory. This directory will be the root of the File System and
 * all manipulated files will be in it.
 * <p>
 * The method {@link #recover(Path)} only call {@link Path#resolve(Path)} to create the physical path of a fuse path.
 */
@Slf4j
@Getter
@Singleton
public class OneMountPointPathRecover implements PhysicalPathRecover {

    // Variables.

    @NonNull
    private final Path rootDirectory;

    // Constructors.

    @Inject
    public OneMountPointPathRecover(@RootDirectory @NonNull Path rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    // Methods.

    @Override
    public Path recover(@NonNull Path fusePath) {
        fusePath = Paths.get(CharMatcher.anyOf("/\\").trimLeadingFrom(fusePath.toString()));
        return rootDirectory.resolve(fusePath);
    }

    // Inner classes.

    @Documented
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RootDirectory {
    }
}
