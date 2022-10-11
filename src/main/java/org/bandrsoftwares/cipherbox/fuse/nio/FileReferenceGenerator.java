package org.bandrsoftwares.cipherbox.fuse.nio;

import lombok.NonNull;

import java.nio.channels.FileChannel;
import java.nio.file.Path;

public interface FileReferenceGenerator {

    FileReference generate(@NonNull Path path, @NonNull FileChannel fileChannel);
}
