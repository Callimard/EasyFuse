package org.callimard.easyfuse.nio;

import java.nio.file.Path;

public class NotFileException extends RuntimeException {
    public NotFileException(Path path) {
        super(path + " is not a file");
    }
}
