package dev.amymialee.amymialib.modules.iinterfaces;

import javax.annotation.Nullable;

public interface AMTranslationKeyHolder {
    default @Nullable String am$key() {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }
}