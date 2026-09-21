package dev.amymialee.amymialib.modules.iinterfaces.mixin.notinjected;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.amymialee.amymialib.modules.iinterfaces.AMSilencer;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Reflections.class)
public class ReflectionsMixin {
    @Shadow @Final protected transient Configuration configuration;

    @WrapOperation(method = "scan", at = @At(value = "FIELD", target = "Lorg/reflections/Reflections;log:Lorg/slf4j/Logger;", opcode = Opcodes.GETSTATIC, ordinal = 0))
    private @Nullable Logger am$explode(Operation<Logger> original) {
        if (this.configuration instanceof AMSilencer<?> silencer && silencer.am$isSilenced()) return null;
        return original.call();
    }
}