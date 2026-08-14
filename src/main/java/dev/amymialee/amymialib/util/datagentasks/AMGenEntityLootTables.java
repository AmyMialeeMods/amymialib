package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootSubProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AMGenEntityLootTables extends AMNameProvider {
    default void generateEntityLootTables(AMEntityLootTableProvider provider, HolderLookup.Provider registries) {}

    static FabricDataGenerator.Pack.@NonNull RegistryDependentFactory<?> listProvider(String name, List<Object> list) {
        return (dataOutput, future) -> new AMEntityLootTableProvider(new AMGenEntityLootTables() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void generateEntityLootTables(@NonNull AMEntityLootTableProvider provider, HolderLookup.Provider wrapperLookup) {
                for (var object : list) if (object instanceof AMGenEntityLootTables task) task.generateEntityLootTables(provider, wrapperLookup);
            }
        }, dataOutput, future);
    }

    class AMEntityLootTableProvider extends FabricEntityLootSubProvider {
        private final AMGenEntityLootTables dataGen;

        public AMEntityLootTableProvider(AMGenEntityLootTables gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        public void generate() {
            this.dataGen.generateEntityLootTables(this, this.registries);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}