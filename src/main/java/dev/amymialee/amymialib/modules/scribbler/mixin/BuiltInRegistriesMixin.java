package dev.amymialee.amymialib.modules.scribbler.mixin;

import com.google.common.reflect.Reflection;
import com.mojang.datafixers.util.Pair;
import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.modules.scribbler.Scribble;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleInit;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleTranslation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.AccessFlag;
import java.util.Arrays;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {
    @Inject(method = "bootStrap", at = @At(value = "HEAD"))
    private static void am$fullAuto(CallbackInfo ci) {
        var auto = AmyMialib.AUTO_QUEUE;
        for (var i = 0; i < auto.size(); i++) runAuto(auto.get(i));
        var scribbles = AmyMialib.SCRIBBLES;
        for (var i = 0; i < scribbles.size(); i++) scribbles.get(i).holder();
    }

    @Unique
    private static void runAuto(Pair<Scribbler, String> pair) {
        runAuto(pair.getFirst(), pair.getSecond());
    }

    @Unique
    private static void runAuto(Scribbler scribbler, String packageName) {
        new Reflections(new ConfigurationBuilder().forPackage(packageName).filterInputsBy(new FilterBuilder().includePackage(packageName)).setScanners(Scanners.TypesAnnotated))
                .getTypesAnnotatedWith(ScribbleTranslation.class).forEach(clazz -> Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(ScribbleTranslation.class)).forEach(field -> {
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
                }));
        var init = new Reflections(new ConfigurationBuilder().forPackage(packageName).filterInputsBy(new FilterBuilder().includePackage(packageName)).setScanners(Scanners.TypesAnnotated)).getTypesAnnotatedWith(ScribbleInit.class);
        init.forEach(clazz -> Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(ScribbleInit.class)).forEach(method -> {
            try {
                method.setAccessible(true);
                method.invoke(null);
            } catch (Throwable t) {
                if (method.getParameterCount() > 0) {
                    scribbler.logger.error("Unable to invoke method {}, it needs to have no parameters.", method.getName());
                } else if (!method.accessFlags().contains(AccessFlag.STATIC)) {
                    scribbler.logger.error("Unable to invoke method {}, it needs to be static.", method.getName());
                }
                throw new RuntimeException(t);
            }
        }));
        init.forEach(Reflection::initialize);
    }
}