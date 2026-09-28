package dev.amymialee.amymialib.modules.iinterfaces;

import net.minecraft.core.TypedInstance;

public interface AMTypedInstance<T> {
    default boolean am$is(TypedInstance<T> type) {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }
}