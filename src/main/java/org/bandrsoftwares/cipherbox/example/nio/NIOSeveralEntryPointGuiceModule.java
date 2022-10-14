package org.bandrsoftwares.cipherbox.example.nio;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.bandrsoftwares.cipherbox.fuse.*;
import org.bandrsoftwares.cipherbox.fuse.nio.*;

import java.nio.file.Paths;
import java.util.List;

public class NIOSeveralEntryPointGuiceModule extends AbstractModule {

    // Methods.

    @Override
    protected void configure() {
        bind(ConcreteFuseFS.class);

        bind(FuseLockManager.class).to(BasicFuseLockManager.class);

        bind(FileAttributesUtil.class);

        bind(EntryPointFactory.class).to(BasicEntryPointFactory.class);

        bind(PhysicalPathRecover.class).to(SeveralEntryPointPathRecover.class);

        bind(FileReferenceGenerator.class).to(BasicFileReferenceGenerator.class);
        bind(FileReferenceFactory.class).to(BasicFileReferenceFactory.class);
        bind(FuseFileManager.class).to(NIOFuseFileManager.class);

        bind(DirectoryFileFilter.class).to(BasicDirectoryFileFilter.class);
        bind(FuseDirectoryManager.class).to(NIOEntryPointFuseDirectoryManager.class);

        bind(FuseLinkManager.class).to(NIOFuseLinkManager.class);

        bind(FuseFSActionManager.class).to(NIOEntryPointFuseFSActionManager.class);
    }

    @Provides
    @BasicFileReferenceGenerator.BufferSize
    public Integer provideBufferSize() {
        return 8192;
    }

    @Provides
    @EntryPointFactory.EntryPointList
    public List<EntryPoint> provideEntryPoint() {
        List<EntryPoint> entryPoints = Lists.newArrayList();

        entryPoints.add(new EntryPoint("ICloud", Paths.get("C:\\Users\\guilr\\iCloudDrive")));
        entryPoints.add(new EntryPoint("ShadowPlay", Paths.get("W:\\Shadow Play")));

        return entryPoints;
    }
}
