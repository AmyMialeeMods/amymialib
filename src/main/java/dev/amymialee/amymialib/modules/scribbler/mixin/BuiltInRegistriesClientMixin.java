package dev.amymialee.amymialib.modules.scribbler.mixin;

import com.google.common.reflect.Reflection;
import com.mojang.datafixers.util.Pair;
import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleClientInit;
import net.minecraft.core.registries.BuiltInRegistries;
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
public class BuiltInRegistriesClientMixin {
    @Inject(method = "bootStrap", at = @At(value = "HEAD"))
    private static void am$fullAuto(CallbackInfo ci) {
        var auto = AmyMialib.AUTO_QUEUE;
        for (var i = 0; i < auto.size(); i++) runAutoClient(auto.get(i));
    }

    @Unique
    private static void runAutoClient(Pair<Scribbler, String> pair) {
        runAutoClient(pair.getFirst(), pair.getSecond());
    }

    @Unique
    private static void runAutoClient(Scribbler scribbler, String packageName) {
        var init = new Reflections(new ConfigurationBuilder().forPackage(packageName).filterInputsBy(new FilterBuilder().includePackage(packageName)).setScanners(Scanners.TypesAnnotated)).getTypesAnnotatedWith(ScribbleClientInit.class);
        init.forEach(clazz -> Arrays.stream(clazz.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(ScribbleClientInit.class)).forEach(method -> {
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