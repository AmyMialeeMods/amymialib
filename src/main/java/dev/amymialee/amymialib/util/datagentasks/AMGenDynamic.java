package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenDynamic extends AMNameProvider {
    default void generateDynamic(@NonNull AMDynamicProvider provider, HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.@NonNull Entries entries) {}

    class AMDynamicProvider extends FabricDynamicRegistryProvider {
        private final AMGenDynamic dataGen;

        public AMDynamicProvider(AMGenDynamic gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        protected void configure(HolderLookup.@NonNull Provider wrapperLookup, @NonNull Entries entries) {
            this.dataGen.generateDynamic(this, wrapperLookup, entries);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenDynamic.class, p -> p.generateDynamic(this, wrapperLookup, entries));
        }

        @Override
        public @NonNull CompletableFuture<?> run(@NonNull CachedOutput cache) {
            return super.run(cache);
        }

        public <T> void add(@NonNull Entries entries, ResourceKey<Registry<T>> key) {
            entries.addAll((HolderLookup.RegistryLookup<T>) entries.getLookup(key));
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " Dynamic";
        }
    }
}