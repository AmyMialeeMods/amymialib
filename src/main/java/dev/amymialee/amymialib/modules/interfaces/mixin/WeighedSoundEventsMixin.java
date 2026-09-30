package dev.amymialee.amymialib.modules.interfaces.mixin;

import dev.amymialee.amymialib.modules.interfaces.AmyMialibTranslationKeyHolder;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeighedSoundEvents.class)
public class WeighedSoundEventsMixin implements AmyMialibTranslationKeyHolder {
    @Unique private String key;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void amymialib$saveKey(Identifier location, String subtitle, CallbackInfo ci) {
        this.key = subtitle;
    }

    @Override
    public String amymialib$translationKey() {
        return this.key;
    }
}