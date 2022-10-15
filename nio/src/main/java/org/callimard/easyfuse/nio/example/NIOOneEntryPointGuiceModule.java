package org.callimard.easyfuse.nio.example;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.callimard.easyfuse.core.*;
import org.callimard.easyfuse.nio.*;

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

        bind(NIOFileAttributeGetter.class).to(BasicFileAttributeGetter.class);
        bind(NIODirectoryAttributeGetter.class).to(BasicDirectoryAttributeGetter.class);
        bind(NIOLinkAttributeGetter.class).to(BasicLinkAttributeGetter.class);
        bind(FuseAttributeGetterManager.class).to(NIOFuseAttributeGetterManager.class);

        bind(FuseFSActionManager.class).to(NIOFuseFSActionManager.class);
    }

    @Provides
    @OneEntryPointPathRecover.RootDirectory
    public Path provideRootDirectory() {
        return Paths.get("C:\\YOUR_DIRECTORY");
    }

    @Provides
    @BasicFileReferenceGenerator.BufferSize
    public Integer provideBufferSize() {
        return 8192;
    }
}
