package org.callimard.easyfuse.nio.example;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.callimard.easyfuse.core.*;
import org.callimard.easyfuse.nio.*;

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

        bind(NIOFileAttributeGetter.class).to(BasicFileAttributeGetter.class);
        bind(NIODirectoryAttributeGetter.class).to(BasicDirectoryAttributeGetter.class);
        bind(NIOLinkAttributeGetter.class).to(BasicLinkAttributeGetter.class);
        bind(FuseAttributeGetterManager.class).to(NIOEntryPointFuseAttributeGetterManager.class);

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

        entryPoints.add(new EntryPoint("ICloud", Paths.get("C:\\YOUR_DIRECTORY_1")));
        entryPoints.add(new EntryPoint("ShadowPlay", Paths.get("W:\\YOUR_DIRECTORY_2")));

        return entryPoints;
    }
}
