package org.bandrsoftwares.cipherbox.fuse.nio;

import jnr.ffi.Pointer;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

@Slf4j
@ToString
@Getter
public class BasicFileReference implements FileReference {

    // Constants.

    public static final int DEFAULT_BUFFER_SIZE = 4096;

    // Variables.

    @ToString.Exclude
    private final int bufferSize;

    @NonNull
    private final Path path;

    @ToString.Exclude
    @NonNull
    private final FileChannel fileChannel;

    // Constructors.

    public BasicFileReference(@NonNull Path path, @NonNull FileChannel fileChannel) {
        this(DEFAULT_BUFFER_SIZE, path, fileChannel);
    }

    public BasicFileReference(int bufferSize, @NonNull Path path, @NonNull FileChannel fileChannel) {
        this.bufferSize = bufferSize;
        if (this.bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size must be greater than 0");
        }

        this.path = path;
        this.fileChannel = fileChannel;
    }

    // Methods.

    @Override
    public int read(Pointer buf, long size, long offset) throws IOException {
        if (offset >= size) {
            log.warn("Try to read over file size");
            return 0;
        }

        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        fileChannel.position(offset);
        long pos = 0;
        do {
            long remaining = size - pos;
            int read = readNext(buffer, remaining);

            if (read != -1) {
                // Impossible case
                log.error("Read until EOF -> IMPOSSIBLE CASE");
                return (int) pos;
            } else {
                buf.put(pos, buffer.array(), 0, read);
                pos += read;
            }
        } while (pos < size);
        return (int) pos;
    }

    private int readNext(ByteBuffer buffer, long remaining) throws IOException {
        buffer.clear();
        buffer.limit((int) Math.min(buffer.capacity(), remaining));
        return fileChannel.read(buffer);
    }

    @Override
    public int write(Pointer buf, long size, long offset) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);

        fileChannel.position(offset);
        long pos = 0;
        do {
            long remaining = size - pos;

            buffer.clear();
            int length = (int) Math.min(buffer.capacity(), remaining);
            buf.get(pos, buffer.array(), 0, length);
            buffer.limit(length);

            int write = fileChannel.write(buffer);
            pos += write;
        } while (pos < size);

        return (int) pos;
    }

    @Override
    public void truncate(long size) throws IOException {
        fileChannel.truncate(size);
    }

    @Override
    public void fsync(boolean metadata) throws IOException {
        fileChannel.force(metadata);
    }

    @Override
    public void close() throws IOException {
        fileChannel.close();
    }
}
