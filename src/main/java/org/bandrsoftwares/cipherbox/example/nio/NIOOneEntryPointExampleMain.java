package org.bandrsoftwares.cipherbox.example.nio;

import lombok.extern.slf4j.Slf4j;
import org.bandrsoftwares.cipherbox.fuse.ConcreteFuseFS;

@Slf4j
public class NIOOneEntryPointExampleMain {

    public static void main(String[] args) {
        NIOFuseFSStartUtils.chargeAndStartFuseFS(ConcreteFuseFS.class, new NIOOneEntryPointGuiceModule());
    }

}
