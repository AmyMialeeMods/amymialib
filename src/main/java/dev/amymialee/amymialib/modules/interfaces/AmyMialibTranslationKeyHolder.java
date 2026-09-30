package dev.amymialee.amymialib.modules.interfaces;

import javax.annotation.Nullable;

public interface AmyMialibTranslationKeyHolder {
    default @Nullable String amymialib$translationKey() {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }
}