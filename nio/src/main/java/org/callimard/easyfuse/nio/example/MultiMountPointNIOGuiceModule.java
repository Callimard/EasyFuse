package org.callimard.easyfuse.nio.example;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.callimard.easyfuse.core.*;
import org.callimard.easyfuse.nio.attribute.AttributeUtil;
import org.callimard.easyfuse.nio.directory.DirectoryAttributeGetter;
import org.callimard.easyfuse.nio.directory.DirectoryFileFilter;
import org.callimard.easyfuse.nio.directory.NIODirectoryAttributeGetter;
import org.callimard.easyfuse.nio.mountpoint.MountPoint;
import org.callimard.easyfuse.nio.mountpoint.MountPointFactory;
import org.callimard.easyfuse.nio.file.*;
import org.callimard.easyfuse.nio.directory.NIODirectoryFileFilter;
import org.callimard.easyfuse.nio.mountpoint.MountPointFactoryImpl;
import org.callimard.easyfuse.nio.link.LinkAttributeGetter;
import org.callimard.easyfuse.nio.link.NIOLinkAttributeGetter;
import org.callimard.easyfuse.core.lock.PathLockManagerImpl;
import org.callimard.easyfuse.nio.manager.*;
import org.callimard.easyfuse.nio.pathrecover.MultiMountPointPathRecover;
import org.callimard.easyfuse.nio.pathrecover.PhysicalPathRecover;

import java.nio.file.Paths;
import java.util.List;

public class MultiMountPointNIOGuiceModule extends AbstractModule {

    // Methods.

    @Override
    protected void configure() {
        bind(EasyFuseFS.class);

        bind(org.callimard.easyfuse.core.lock.PathLockManager.class).to(PathLockManagerImpl.class);

        bind(AttributeUtil.class);

        bind(MountPointFactory.class).to(MountPointFactoryImpl.class);

        bind(PhysicalPathRecover.class).to(MultiMountPointPathRecover.class);

        bind(FileReferenceGenerator.class).to(NIOFileReferenceGenerator.class);
        bind(FileReferenceFactory.class).to(NIOFileReferenceFactory.class);
        bind(FileManager.class).to(NIOFileManager.class);

        bind(DirectoryFileFilter.class).to(NIODirectoryFileFilter.class);
        bind(DirectoryManager.class).to(MultiMountPointNIODirectoryManager.class);

        bind(LinkManager.class).to(NIOLinkManager.class);

        bind(FileAttributeGetter.class).to(NIOFileAttributeGetter.class);
        bind(DirectoryAttributeGetter.class).to(NIODirectoryAttributeGetter.class);
        bind(LinkAttributeGetter.class).to(NIOLinkAttributeGetter.class);
        bind(AttributeGetterManager.class).to(MultiMountPointNIOAttributeGetterManager.class);

        bind(TruncateManager.class).to(NIOTruncateManager.class);

        bind(UtimensManager.class).to(NIOUtimensManager.class);

        bind(GlobalActionManager.class).to(MultiMountPointNIOGlobalActionManager.class);
    }

    @Provides
    @NIOFileReferenceGenerator.BufferSize
    public Integer provideBufferSize() {
        return 8192;
    }

    @Provides
    @MountPointFactory.EntryPointList
    public List<MountPoint> provideEntryPoint() {
        List<MountPoint> mountPoints = Lists.newArrayList();

        mountPoints.add(new MountPoint("ICloud", Paths.get("C:\\YOUR_DIRECTORY_1")));
        mountPoints.add(new MountPoint("ShadowPlay", Paths.get("W:\\YOUR_DIRECTORY_2")));

        return mountPoints;
    }
}
