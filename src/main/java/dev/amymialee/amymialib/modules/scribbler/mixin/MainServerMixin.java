package dev.amymialee.amymialib.modules.scribbler.mixin;

import dev.amymialee.amymialib.modules.scribbler.AutoQueue;
import net.minecraft.server.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Main.class})
public class MainServerMixin {
    @Inject(method = "main", at = @At(value = "TAIL"))
    private static void amymialib$fullAuto(CallbackInfo ci) {
        AutoQueue.clearDatagen();
    }
}