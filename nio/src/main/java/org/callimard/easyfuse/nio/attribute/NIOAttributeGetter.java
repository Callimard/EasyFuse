package org.callimard.easyfuse.nio.attribute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class NIOAttributeGetter {

    // Variables.

    @NonNull
    private final AttributeUtil attributeUtil;
}
