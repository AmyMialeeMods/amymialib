package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AMSubtitleTranslator;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SoundEvent.class)
public abstract class SoundEventMixin implements AMSubtitleTranslator {}