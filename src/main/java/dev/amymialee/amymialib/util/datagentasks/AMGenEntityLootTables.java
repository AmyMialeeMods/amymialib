package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootSubProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenEntityLootTables extends AMNameProvider {
    default void generateEntityLootTables(AMEntityLootTableProvider provider, HolderLookup.Provider registries) {}

    class AMEntityLootTableProvider extends FabricEntityLootSubProvider {
        private final AMGenEntityLootTables dataGen;

        public AMEntityLootTableProvider(AMGenEntityLootTables gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        public void generate() {
            this.dataGen.generateEntityLootTables(this, this.registries);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenEntityLootTables.class, p -> p.generateEntityLootTables(this, this.registries));
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}