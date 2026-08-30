package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenSounds extends AMNameProvider {
    default void generateSounds(AMSoundsProvider provider, HolderLookup.Provider registryLookup, FabricSoundsProvider.SoundExporter exporter) {}

    class AMSoundsProvider extends FabricSoundsProvider {
        private final AMGenSounds dataGen;

        public AMSoundsProvider(AMGenSounds gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(output, registryLookup);
            this.dataGen = gen;
        }

        @Override
        protected void configure(HolderLookup.@NonNull Provider registryLookup, @NonNull SoundExporter exporter) {
            this.dataGen.generateSounds(this, registryLookup, exporter);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenSounds.class, p -> p.generateSounds(this, registryLookup, exporter));
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " Sounds";
        }
    }
}