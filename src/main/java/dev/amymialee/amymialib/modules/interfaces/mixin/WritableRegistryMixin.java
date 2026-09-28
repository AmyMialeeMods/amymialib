package dev.amymialee.amymialib.modules.interfaces.mixin;

import dev.amymialee.amymialib.modules.interfaces.registerable.ScrawledRegisterable;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MappedRegistry.class, DefaultedMappedRegistry.class})
public class WritableRegistryMixin<T> {
    @SuppressWarnings("unchecked")
    @Inject(method = "register", at = @At(value = "RETURN"))
    private void amymialib$remember(ResourceKey<T> key, T value, RegistrationInfo registrationInfo, CallbackInfoReturnable<Holder.Reference<T>> cir) {
        if (value instanceof ScrawledRegisterable scrawl) scrawl.amymialib$onRegister(cir.getReturnValue());
    }
}