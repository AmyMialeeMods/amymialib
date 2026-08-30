package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface AMGenLootTables extends AMNameProvider {
    default void generateLootTables(AMLootTableProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {}

    class AMLootTableProvider extends SimpleFabricLootTableSubProvider {
        private final AMGenLootTables dataGen;

        public AMLootTableProvider(AMGenLootTables dataGen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture, LootTable.DEFAULT_PARAM_SET);
            this.dataGen = dataGen;
        }

        @Override
        public void generate(@NonNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
            this.dataGen.generateLootTables(this, lootTableBiConsumer);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenLootTables.class, p -> p.generateLootTables(this, lootTableBiConsumer));
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}
