package org.bandrsoftwares.cipherbox.example.nio;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.bandrsoftwares.cipherbox.fuse.*;
import org.bandrsoftwares.cipherbox.fuse.nio.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NIOOneEntryPointGuiceModule extends AbstractModule {

    // Methods.

    @Override
    protected void configure() {
        bind(ConcreteFuseFS.class);

        bind(FuseLockManager.class).to(BasicFuseLockManager.class);

        bind(FileAttributesUtil.class);

        bind(PhysicalPathRecover.class).to(OneEntryPointPathRecover.class);

        bind(FileReferenceGenerator.class).to(BasicFileReferenceGenerator.class);
        bind(FileReferenceFactory.class).to(BasicFileReferenceFactory.class);
        bind(FuseFileManager.class).to(NIOFuseFileManager.class);

        bind(DirectoryFileFilter.class).to(BasicDirectoryFileFilter.class);
        bind(FuseDirectoryManager.class).to(NIOFuseDirectoryManager.class);

        bind(FuseLinkManager.class).to(NIOFuseLinkManager.class);

        bind(FuseFSActionManager.class).to(NIOFuseFSActionManager.class);
    }

    @Provides
    @OneEntryPointPathRecover.RootDirectory
    public Path provideRootDirectory() {
        return Paths.get("C:\\Users\\guilr\\iCloudDrive");
    }

    @Provides
    @BasicFileReferenceGenerator.BufferSize
    public Integer provideBufferSize() {
        return 8192;
    }
}
