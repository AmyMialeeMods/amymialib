package dev.amymialee.amymialib.modules.interfaces;

import javax.annotation.Nullable;

public interface ScrawledTranslationKeyHolder {
    default @Nullable String amy$translationKey() {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }
}