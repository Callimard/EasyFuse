package org.callimard.easyfuse.nio.example;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.callimard.easyfuse.core.*;
import org.callimard.easyfuse.core.lock.PathLockManager;
import org.callimard.easyfuse.nio.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NIOOneEntryPointGuiceModule extends AbstractModule {

    // Methods.

    @Override
    protected void configure() {
        bind(EasyFuseFS.class);

        bind(PathLockManager.class).to(BasicPathLockManager.class);

        bind(FileAttributesUtil.class);

        bind(PhysicalPathRecover.class).to(OneEntryPointPathRecover.class);

        bind(FileReferenceGenerator.class).to(BasicFileReferenceGenerator.class);
        bind(FileReferenceFactory.class).to(BasicFileReferenceFactory.class);
        bind(FileManager.class).to(NIOFileManager.class);

        bind(DirectoryFileFilter.class).to(BasicDirectoryFileFilter.class);
        bind(DirectoryManager.class).to(NIODirectoryManager.class);

        bind(LinkManager.class).to(NIOLinkManager.class);

        bind(NIOFileAttributeGetter.class).to(BasicFileAttributeGetter.class);
        bind(NIODirectoryAttributeGetter.class).to(BasicDirectoryAttributeGetter.class);
        bind(NIOLinkAttributeGetter.class).to(BasicLinkAttributeGetter.class);
        bind(AttributeGetterManager.class).to(NIOAttributeGetterManager.class);

        bind(TruncateManager.class).to(NIOTruncateManager.class);

        bind(UtimensManager.class).to(NIOUtimensManager.class);

        bind(GlobalActionManager.class).to(NIOFSActionManager.class);
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
