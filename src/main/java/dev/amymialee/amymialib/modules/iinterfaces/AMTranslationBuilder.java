package dev.amymialee.amymialib.modules.iinterfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sounds.SoundEvent;

public interface AMTranslationBuilder {
    @Environment(EnvType.CLIENT)
    default void am$addSubtitle(SoundEvent event, String translation) {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }
}