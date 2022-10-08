package org.bandrsoftwares.cipherbox.fuse.nio;

import lombok.NonNull;

import java.nio.file.Path;

public interface PhysicalPathRecover {

    /**
     * Method in charge to recover the physical path of a given fuse path.
     * <p>
     * The returned {@link Path} is a path which can be manipulated with the object of the Java api {@link java.nio}
     *
     * @param fusePath the fuse path for which we want the physical path associated
     *
     * @return the physical path associated to the specified fuse path.
     */
    Path recover(@NonNull Path fusePath);
}
