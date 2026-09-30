package dev.amymialee.amymialib.modules.scribbler.mixin;

import com.google.common.reflect.Reflection;
import dev.amymialee.amymialib.modules.scribbler.AutoQueue;
import dev.amymialee.amymialib.modules.scribbler.AutoScanner;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import dev.amymialee.amymialib.modules.scribbler.auto.Decipher;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleInit;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.AccessFlag;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {
    @Inject(method = "bootStrap", at = @At(value = "HEAD"), order = 900)
    private static void amymialib$fullAuto(CallbackInfo ci) {
        AutoQueue.forEach(BuiltInRegistriesMixin::runAuto);
    }

    @Unique
    private static void runAuto(Scribbler scribbler, String packageName) {
        var reflections = AutoScanner.findAnnotations(packageName);
        reflections.getTypesAnnotatedWith(ScribbleInit.class).forEach(Reflection::initialize);
        reflections.getMethodsAnnotatedWith(ScribbleInit.class).forEach(method -> {
            try {
                method.setAccessible(true);
                method.invoke(null);
            } catch (Throwable t) {
                var reason = "";
                if (method.getParameterCount() > 0) {
                    reason = "Unable to invoke method %s.%s(), as it needs to have no parameters.".formatted(method.getDeclaringClass().getName(), method.getName());
                } else if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                    reason = "Unable to invoke method %s.%s(), as it needs to be static.".formatted(method.getDeclaringClass().getName(), method.getName());
                } else {
                    scribbler.logger.error("Unable to invoke method {}.{}(), due to an unknown reason.", method.getDeclaringClass().getName(), method.getName());
                    throw new RuntimeException(t);
                }
                scribbler.logger.error(reason);
                throw new RuntimeException(reason, null, true, false) {};
            }
        });
        reflections.getFieldsAnnotatedWith(Decipher.class).forEach(f -> {
            try {
                for (var decipher : f.getAnnotationsByType(Decipher.class)) scribbler.translate(f, decipher);
            } catch (Throwable t) {
                var reason = "";
                if (!f.accessFlags().contains(AccessFlag.STATIC)) {
                    reason = "Unable to translate field %s.%s, as it needs to be static.".formatted(f.getDeclaringClass().getName(), f.getName());
                } else if (t instanceof RuntimeException runtimeException) {
                    throw runtimeException;
                } else {
                    scribbler.logger.error("Unable to translate field {}.{}, due to an unknown reason.", f.getDeclaringClass().getName(), f.getName());
                    throw new RuntimeException(t);
                }
                scribbler.logger.error(reason);
                throw new RuntimeException(reason, null, true, false) {};
            }
        });
    }
}