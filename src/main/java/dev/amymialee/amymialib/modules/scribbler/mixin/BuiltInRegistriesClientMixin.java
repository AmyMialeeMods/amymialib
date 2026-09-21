package dev.amymialee.amymialib.modules.scribbler.mixin;

import com.google.common.reflect.Reflection;
import dev.amymialee.amymialib.modules.scribbler.AutoQueue;
import dev.amymialee.amymialib.modules.scribbler.AutoScanner;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleClientInit;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.AccessFlag;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesClientMixin {
    @Inject(method = "bootStrap", at = @At(value = "HEAD"))
    private static void am$fullAuto(CallbackInfo ci) {
        AutoQueue.forEach(BuiltInRegistriesClientMixin::runAutoClient);
    }

    @Unique
    private static void runAutoClient(Scribbler scribbler, String packageName) {
        var reflections = AutoScanner.findAnnotations(packageName);
        reflections.getTypesAnnotatedWith(ScribbleClientInit.class).forEach(Reflection::initialize);
        reflections.getMethodsAnnotatedWith(ScribbleClientInit.class).forEach(method -> {
            try {
                method.setAccessible(true);
                method.invoke(null);
            } catch (Throwable t) {
                if (method.getParameterCount() > 0) {
                    scribbler.logger.error("Unable to invoke method {}, it needs to have no parameters.", method.getName());
                } else if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                    scribbler.logger.error("Unable to invoke method {}, it needs to be static.", method.getName());
                } else {
                    scribbler.logger.error("Unable to invoke method {}, unknown reason.", method.getName());
                }
                throw new RuntimeException(t);
            }
        });
    }
}