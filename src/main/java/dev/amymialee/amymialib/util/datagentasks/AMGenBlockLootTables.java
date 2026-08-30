package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenBlockLootTables extends AMNameProvider {
    default void generateBlockLootTables(AMBlockLootSubProvider provider, HolderLookup.Provider registries) {}

    class AMBlockLootSubProvider extends FabricBlockLootSubProvider {
        private final AMGenBlockLootTables dataGen;

        public AMBlockLootSubProvider(AMGenBlockLootTables gen, FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(packOutput, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        public void generate() {
            this.dataGen.generateBlockLootTables(this, this.registries);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenBlockLootTables.class, p -> p.generateBlockLootTables(this, this.registries));
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}