package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public interface AMGenLootTables extends AMNameProvider {
    default void generateLootTables(AMLootTableProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {}

    static FabricDataGenerator.Pack.@NonNull RegistryDependentFactory<?> listProvider(String name, List<Object> list) {
        return (dataOutput, future) -> new AMLootTableProvider(new AMGenLootTables() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void generateLootTables(AMLootTableProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
                for (var object : list) if (object instanceof AMGenLootTables task) task.generateLootTables(provider, consumer);
            }
        }, dataOutput, future);
    }

    class AMLootTableProvider extends SimpleFabricLootTableSubProvider {
        private final AMGenLootTables dataGen;

        public AMLootTableProvider(AMGenLootTables dataGen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture, LootTable.DEFAULT_PARAM_SET);
            this.dataGen = dataGen;
        }

        @Override
        public void generate(@NonNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
            this.dataGen.generateLootTables(this, lootTableBiConsumer);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}
