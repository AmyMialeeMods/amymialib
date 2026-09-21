package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AMTranslationBuilder;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FabricLanguageProvider.TranslationBuilder.class)
public interface TranslationBuilderMixin extends AMTranslationBuilder {
    @Shadow void add(String translationKey, String value);

    @Override
    default void am$addSubtitle(SoundEvent event, String translation) {
        var soundEvent = Minecraft.getInstance().getSoundManager().getSoundEvent(event.location());
        if (soundEvent == null || soundEvent.am$getSubtitleKey() == null) return;
        this.add(soundEvent.am$getSubtitleKey(), translation);
    }
}