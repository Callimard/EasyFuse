package org.callimard.easyfuse.core;

import lombok.NonNull;

public interface FuseManager {

    EasyFuseFS getFuseFS();

    void init(@NonNull EasyFuseFS fuseFS);

    default boolean hasBeenInitialized() {
        return getFuseFS() != null;
    }
}
