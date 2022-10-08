package org.bandrsoftwares.cipherbox.fuse.nio;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NIOFuseManager {

    // Variables.

    @NonNull
    private final PhysicalPathRecover pathRecover;

}
