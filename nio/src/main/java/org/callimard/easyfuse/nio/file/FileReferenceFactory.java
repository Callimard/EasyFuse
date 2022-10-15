package org.callimard.easyfuse.nio.file;

import lombok.NonNull;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Set;

public interface FileReferenceFactory {

    /**
     * @param fileHandle the file handle
     *
     * @return the {@link FileReference} associated to the file handle.
     *
     * @throws ClosedChannelException if no file is open with the file handle
     */
    FileReference get(long fileHandle) throws ClosedChannelException;

    /**
     * Create and open the file
     *
     * @param physicalPath physical path
     * @param openOptions  open options
     * @param attributes   file attributes
     *
     * @return a unique file handle which can be used to retrieve the {@link FileReference} with {@link #get(long)}.
     *
     * @throws NoSuchFileException        if the physical path does not exist
     * @throws FileAlreadyExistsException if the physical path already exists
     * @throws IOException                if IO error occurs
     */
    long create(@NonNull Path physicalPath, @NonNull Set<OpenOption> openOptions, FileAttribute<?>... attributes)
            throws IOException;

    /**
     * @param physicalPath physical path
     * @param openOptions  open options
     * @param attributes   file attributes
     *
     * @return a unique file handle which can be used to retrieve the {@link FileReference} with {@link #get(long)}.
     *
     * @throws NoSuchFileException if the physical path does not exist
     * @throws IOException         if IO error occurs
     */
    long open(@NonNull Path physicalPath, @NonNull Set<OpenOption> openOptions, FileAttribute<?>... attributes) throws IOException;

    /**
     * @param fileHandle the file handle
     *
     * @throws ClosedChannelException if no file is open with the file handle
     * @throws IOException            if IO error occurs
     */
    void close(long fileHandle) throws IOException;

    /**
     * Close all {@link FileReference} managed.
     *
     * @throws IOException IO error occurs
     */
    void closeAll() throws IOException;
}
