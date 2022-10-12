package org.bandrsoftwares.cipherbox.fuse;

import lombok.NonNull;

public interface FuseManager {

    ConcreteFuseFS getFuseFS();

    void init(@NonNull ConcreteFuseFS fuseFS);

    default boolean hasBeenInitialized() {
        return getFuseFS() != null;
    }
}
