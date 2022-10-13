package org.bandrsoftwares.cipherbox.fuse.nio;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bandrsoftwares.cipherbox.fuse.ConcreteFuseFS;
import org.bandrsoftwares.cipherbox.fuse.FuseManager;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NIOFuseManager implements FuseManager {

    // Variables.

    @NonNull
    private final PhysicalPathRecover pathRecover;

    /**
     * Must be initialized.
     */
    private ConcreteFuseFS fuseFS;

    // Methods.

    @Override
    public void init(@NonNull ConcreteFuseFS fuseFS) {
        if (!hasBeenInitialized()) this.fuseFS = fuseFS;
    }
}
