package org.callimard.easyfuse.nio;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Singleton
public class BasicFileReferenceFactory implements FileReferenceFactory {

    // Variables.

    @NonNull
    private final FileReferenceGenerator fileReferenceGenerator;

    private final Map<Long, FileReference> mapFileRef = Maps.newConcurrentMap();

    private final AtomicLong fileHandleGenerator = new AtomicLong(0L);

    // Constructors.

    @Inject
    public BasicFileReferenceFactory(@NonNull FileReferenceGenerator fileReferenceGenerator) {
        this.fileReferenceGenerator = fileReferenceGenerator;
    }

    // Methods.

    @Override
    public FileReference get(long fileHandle) throws ClosedChannelException {
        var res = mapFileRef.get(fileHandle);
        if (res == null) {
            throw new ClosedChannelException();
        }
        return res;
    }

    @Override
    public long create(@NonNull Path physicalPath, @NonNull Set<OpenOption> openOptions, FileAttribute<?>... attributes)
            throws IOException, NotFileException {
        var fileChannel = generateFileChannelForCreate(physicalPath, openOptions, attributes);
        var fileHandle = putFileRef(fileReferenceGenerator.generate(physicalPath, fileChannel));
        log.trace("Open file {} with FH {}", physicalPath, fileHandle);
        return fileHandle;
    }

    private static FileChannel generateFileChannelForCreate(Path physicalPath, Set<OpenOption> openOptions, FileAttribute<?>[] attributes)
            throws IOException {
        openOptions.addAll(Sets.newHashSet(StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW));
        return FileChannel.open(physicalPath, openOptions, attributes);
    }

    @Override
    public long open(@NonNull Path physicalPath, @NonNull Set<OpenOption> openOptions, FileAttribute<?>... attributes)
            throws IOException, NotFileException {
        var fileChannel = generateFileChannelForAtLeastRead(physicalPath, openOptions, attributes);
        var fileHandle = putFileRef(fileReferenceGenerator.generate(physicalPath, fileChannel));
        log.trace("Open file {} with FH {}", physicalPath, fileHandle);
        return fileHandle;
    }

    private static FileChannel generateFileChannelForAtLeastRead(Path physicalPath, Set<OpenOption> openOptions, FileAttribute<?>[] attributes)
            throws IOException {
        openOptions.addAll(Sets.newHashSet(StandardOpenOption.READ)); // At least for read
        return FileChannel.open(physicalPath, openOptions, attributes);
    }

    private long putFileRef(FileReference fileRef) {
        long fileHandle = fileHandleGenerator.getAndIncrement();
        mapFileRef.put(fileHandle, fileRef);
        return fileHandle;
    }

    @Override
    public void close(long fileHandle) throws IOException {
        var fileRef = mapFileRef.remove(fileHandle);

        log.trace("Close file ref {} for {}", fileRef, fileHandle);

        if (fileRef == null) {
            throw new ClosedChannelException();
        }

        try {
            fileRef.close();
        } catch (Exception e) {
            log.error("Error during closing file ref", e);
            throw new IOException(e);
        }
    }

    @Override
    public void closeAll() throws IOException {
        log.trace("Closing all file reference");

        IOException ioE = new IOException("At least one close failed");

        Iterator<Map.Entry<Long, FileReference>> ite = mapFileRef.entrySet().iterator();
        while (ite.hasNext()) {
            var entry = ite.next();
            var fileRef = entry.getValue();

            try {
                fileRef.close();
            } catch (Exception e) {
                log.error("Error during closing file ref " + fileRef + " for " + entry.getKey(), e);
                ioE.addSuppressed(e);
            }

            ite.remove();
        }

        if (ioE.getSuppressed().length > 0) {
            throw ioE;
        }
    }
}
