package org.callimard.easyfuse.nio.example;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.callimard.easyfuse.core.*;
import org.callimard.easyfuse.nio.attribute.AttributeUtil;
import org.callimard.easyfuse.nio.directory.DirectoryAttributeGetter;
import org.callimard.easyfuse.nio.directory.DirectoryFileFilter;
import org.callimard.easyfuse.nio.directory.NIODirectoryAttributeGetter;
import org.callimard.easyfuse.nio.file.*;
import org.callimard.easyfuse.nio.directory.NIODirectoryFileFilter;
import org.callimard.easyfuse.nio.link.LinkAttributeGetter;
import org.callimard.easyfuse.nio.link.NIOLinkAttributeGetter;
import org.callimard.easyfuse.nio.lock.PathLockManagerImpl;
import org.callimard.easyfuse.nio.manager.*;
import org.callimard.easyfuse.nio.pathrecover.OneMountPointPathRecover;
import org.callimard.easyfuse.nio.pathrecover.PhysicalPathRecover;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NIOGuiceModule extends AbstractModule {

    // Methods.

    @Override
    protected void configure() {
        bind(EasyFuseFS.class);

        bind(org.callimard.easyfuse.core.lock.PathLockManager.class).to(PathLockManagerImpl.class);

        bind(AttributeUtil.class);

        bind(PhysicalPathRecover.class).to(OneMountPointPathRecover.class);

        bind(FileReferenceGenerator.class).to(NIOFileReferenceGenerator.class);
        bind(FileReferenceFactory.class).to(NIOFileReferenceFactory.class);
        bind(FileManager.class).to(NIOFileManager.class);

        bind(DirectoryFileFilter.class).to(NIODirectoryFileFilter.class);
        bind(DirectoryManager.class).to(NIODirectoryManager.class);

        bind(LinkManager.class).to(NIOLinkManager.class);

        bind(FileAttributeGetter.class).to(NIOFileAttributeGetter.class);
        bind(DirectoryAttributeGetter.class).to(NIODirectoryAttributeGetter.class);
        bind(LinkAttributeGetter.class).to(NIOLinkAttributeGetter.class);
        bind(AttributeGetterManager.class).to(NIOAttributeGetterManager.class);

        bind(TruncateManager.class).to(NIOTruncateManager.class);

        bind(UtimensManager.class).to(NIOUtimensManager.class);

        bind(GlobalActionManager.class).to(NIOGlobalActionManager.class);
    }

    @Provides
    @OneMountPointPathRecover.RootDirectory
    public Path provideRootDirectory() {
        return Paths.get("C:\\YOUR_DIRECTORY");
    }

    @Provides
    @NIOFileReferenceGenerator.BufferSize
    public Integer provideBufferSize() {
        return 8192;
    }
}
