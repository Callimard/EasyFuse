package org.callimard.easyfuse.nio;

import com.google.common.base.CharMatcher;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Singleton
public class SeveralEntryPointPathRecover implements PhysicalPathRecover {

    // Variables.

    @NonNull
    private final EntryPointFactory entryPointFactory;

    // Constructors.

    @Inject
    SeveralEntryPointPathRecover(@NonNull EntryPointFactory entryPointFactory) {
        this.entryPointFactory = entryPointFactory;
    }

    // Methods.

    @Override
    public Path recover(@NonNull Path fusePath) {
        if (fusePath.getNameCount() == 0) {
            log.warn("Impossible to recover the physical path of {}, it is a Fuse Root path", fusePath);
            throw new NotPossiblePhysicalPathRecoverException(fusePath);
        }

        String entryPointName = fusePath.getName(0).toString();
        Path entryPointDirectory = entryPointFactory.getPhysicalDirectoryOf(entryPointName);

        Path relevantPath = extractRelevantPath(fusePath);


        return entryPointDirectory.resolve(relevantPath);
    }

    private static Path extractRelevantPath(Path fusePath) {
        Path relevantPath;
        if (fusePath.getNameCount() > 1) {
            relevantPath = fusePath.subpath(1, fusePath.getNameCount());
            relevantPath = Paths.get(CharMatcher.anyOf("/\\").trimLeadingFrom(relevantPath.toString())); // Useless but it is a protection
        } else {
            relevantPath = Paths.get("");
        }
        return relevantPath;
    }
}
