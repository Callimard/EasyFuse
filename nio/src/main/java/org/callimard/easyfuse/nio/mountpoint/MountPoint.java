package org.callimard.easyfuse.nio.mountpoint;

import java.nio.file.Path;

public record MountPoint(String name, Path directory) {
}
