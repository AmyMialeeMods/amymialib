package dev.amymialee.amymialib.modules.scribbler.mixin;

import dev.amymialee.amymialib.modules.scribbler.AutoQueue;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private static void am$fullAuto(CallbackInfo ci) {
        AutoQueue.clearDatagen();
    }
}