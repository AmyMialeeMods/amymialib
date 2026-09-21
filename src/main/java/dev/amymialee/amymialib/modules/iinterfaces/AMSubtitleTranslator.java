package dev.amymialee.amymialib.modules.iinterfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

public interface AMSubtitleTranslator {
    @Environment(EnvType.CLIENT)
    default void am$addSubtitle(FabricLanguageProvider.TranslationBuilder builder, String translation) {
        var soundEvent = Minecraft.getInstance().getSoundManager().getSoundEvent(((SoundEvent) this).location());
        if (soundEvent == null) return;
        var key = soundEvent.am$key();
        if (key == null) return;
        builder.add(key, translation);
    }
}