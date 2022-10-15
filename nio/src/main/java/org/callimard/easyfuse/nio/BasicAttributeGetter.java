package org.callimard.easyfuse.nio;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BasicAttributeGetter {

    // Variables.

    @NonNull
    private final FileAttributesUtil fileAttributesUtil;
}
