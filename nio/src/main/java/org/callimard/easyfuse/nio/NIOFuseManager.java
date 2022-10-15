package org.callimard.easyfuse.nio;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.callimard.easyfuse.core.EasyFuseFS;
import org.callimard.easyfuse.core.FuseManager;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NIOFuseManager implements FuseManager {

    // Variables.

    @NonNull
    private final PhysicalPathRecover pathRecover;

    /**
     * Must be initialized.
     */
    private EasyFuseFS fuseFS;

    // Methods.

    @Override
    public void init(@NonNull EasyFuseFS fuseFS) {
        if (!hasBeenInitialized()) this.fuseFS = fuseFS;
    }
}
