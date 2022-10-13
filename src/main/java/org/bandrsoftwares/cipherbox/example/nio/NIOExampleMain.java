package org.bandrsoftwares.cipherbox.example.nio;

import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.bandrsoftwares.cipherbox.fuse.ConcreteFuseFS;
import ru.serce.jnrfuse.FuseStubFS;

import java.nio.file.Paths;
import java.util.Scanner;

@Slf4j
public class NIOExampleMain {

    public static void main(String[] args) {
        try {
            ConcreteFuseFS nioFuseFS;
            Injector injector = Guice.createInjector(new NIOGuiceModule());
            nioFuseFS = injector.getInstance(ConcreteFuseFS.class);

            log.info("Mount NIO Fuse FS");
            nioFuseFS.mount(Paths.get("R:\\"), false, false);
            waitEnd(nioFuseFS);

            log.info("Unmount NIO Fuse FS");
        } catch (Throwable e) {
            log.error("Fail during process", e);
        }
    }

    public static void waitEnd(FuseStubFS fuseFS) {
        Scanner sc = new Scanner(System.in);
        while (!(sc.next()).equals("q")) {
            // Nothing to do
        }
        fuseFS.umount();
    }
}
