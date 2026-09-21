package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AMTranslationKeyHolder;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WeighedSoundEvents.class)
public class WeighedSoundEventsMixin implements AMTranslationKeyHolder {
    @Unique private String key;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void am$saveKey(Identifier location, String subtitle, CallbackInfo ci) {
        this.key = subtitle;
    }

    @Override
    public String am$key() {
        return this.key;
    }
}