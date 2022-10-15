package org.callimard.easyfuse.nio.file;

import lombok.Getter;
import lombok.NonNull;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

@Singleton
@Getter
public class NIOFileReferenceGenerator implements FileReferenceGenerator {

    // Variables.

    private final int fileReferenceBufferSize;

    // Constructors.

    @Inject
    public NIOFileReferenceGenerator(@BufferSize @Nullable Integer fileReferenceBufferSize) {
        this.fileReferenceBufferSize = fileReferenceBufferSize != null ? fileReferenceBufferSize : -1;
    }

    // Methods.

    @Override
    public FileReference generate(@NonNull Path path, @NonNull FileChannel fileChannel) {
        if (fileReferenceBufferSize <= 0) {
            return new NIOFileReference(path, fileChannel);
        } else {
            return new NIOFileReference(fileReferenceBufferSize, path, fileChannel);
        }
    }

    @Documented
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface BufferSize {
    }
}
