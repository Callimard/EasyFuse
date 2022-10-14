package org.callimard.easyfuse.nio.example;

import lombok.extern.slf4j.Slf4j;
import org.callimard.easyfuse.core.ConcreteFuseFS;

@Slf4j
public class NIOSeveralEntryPointExampleMain {

    public static void main(String[] args) {
        NIOFuseFSStartUtils.chargeAndStartFuseFS(ConcreteFuseFS.class, new NIOSeveralEntryPointGuiceModule());
    }
}
