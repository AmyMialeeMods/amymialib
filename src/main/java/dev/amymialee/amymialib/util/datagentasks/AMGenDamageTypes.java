package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenDamageTypes extends AMNameProvider {
    default void generateDamageTypes(AMDamageTypeProvider provider, HolderLookup.Provider registries, FabricDynamicRegistryProvider.@NotNull Entries entries) {}

    class AMDamageTypeProvider extends FabricDynamicRegistryProvider {
        private final AMGenDamageTypes dataGen;

        public AMDamageTypeProvider(AMGenDamageTypes gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        protected void configure(HolderLookup.@NonNull Provider registries, @NotNull Entries entries) {
            this.dataGen.generateDamageTypes(this, registries, entries);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenDamageTypes.class, p -> p.generateDamageTypes(this, registries, entries));
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " Tags for Damage Types";
        }
    }
}