package org.callimard.easyfuse.nio.example;

import lombok.extern.slf4j.Slf4j;
import org.callimard.easyfuse.core.EasyFuseFS;

@Slf4j
public class MultiMountPointNIOExampleMain {

    public static void main(String[] args) {
        NIOFuseFSStartUtils.chargeAndStartFuseFS(EasyFuseFS.class, new MultiMountPointNIOGuiceModule());
    }
}
