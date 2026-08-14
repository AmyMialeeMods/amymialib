package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AMGenBlockLootTables extends AMNameProvider {
    default void generateBlockLootTables(AMBlockLootSubProvider provider, HolderLookup.Provider registries) {}

    static FabricDataGenerator.Pack.@NonNull RegistryDependentFactory<?> listProvider(String name, List<Object> list) {
        return (dataOutput, future) -> new AMBlockLootSubProvider(new AMGenBlockLootTables() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void generateBlockLootTables(AMBlockLootSubProvider provider, HolderLookup.Provider arg) {
                for (var object : list) if (object instanceof AMGenBlockLootTables task) task.generateBlockLootTables(provider, arg);
            }
        }, dataOutput, future);
    }

    class AMBlockLootSubProvider extends FabricBlockLootSubProvider {
        private final AMGenBlockLootTables dataGen;

        public AMBlockLootSubProvider(AMGenBlockLootTables gen, FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(packOutput, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        public void generate() {
            this.dataGen.generateBlockLootTables(this, this.registries);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}