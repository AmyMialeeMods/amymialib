package dev.amymialee.amymialib.modules.dossier.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierRegistries;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.minecraft.core.RegistrySetBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FabricDataGenHelper.class)
public class FabricDataGenHelperMixin {
    @WrapOperation(method = "createWorldLookupProvider", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/datagen/v1/DataGeneratorEntrypoint;buildRegistry(Lnet/minecraft/core/RegistrySetBuilder;)V"))
    private static void am$more(DataGeneratorEntrypoint instance, RegistrySetBuilder registryBuilder, Operation<Void> original) {
        original.call(instance, registryBuilder);
        if (instance instanceof Dossier dossier)  dossier.registryEach(DossierRegistries.class, p -> p.commitRegistries(registryBuilder));
    }
}