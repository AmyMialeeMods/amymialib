package dev.amymialee.amymialib.modules.scribbler.mixin;

import com.google.common.reflect.Reflection;
import dev.amymialee.amymialib.modules.scribbler.AutoQueue;
import dev.amymialee.amymialib.modules.scribbler.AutoScanner;
import dev.amymialee.amymialib.modules.scribbler.scribbles.Scribble;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleInit;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleTranslation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.AccessFlag;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {
    @Inject(method = "bootStrap", at = @At(value = "HEAD"), order = 900)
    private static void am$fullAuto(CallbackInfo ci) {
        AutoQueue.forEach(BuiltInRegistriesMixin::runAuto);
        AutoQueue.initializeScribbles();
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
        reflections.getFieldsAnnotatedWith(ScribbleTranslation.class).forEach(field -> {
            try {
                field.setAccessible(true);
                var translations = field.getAnnotation(ScribbleTranslation.class).value();
                for (var i = 0; i < translations.length;) {
                    if (translations.length > i + 1) {
                        scribbler.translation(translations[i], translations[i + 1]);
                        i += 2;
                    } else if (field.get(null) instanceof Scribble<?> scribble) {
                        var key = scribble.key();
                        scribbler.translation(Util.makeDescriptionId(key.registry().toLanguageKey(), key.identifier()), translations[i]);
                        i++;
                    }
                }
            } catch (Throwable _) {}
        });
    }
}