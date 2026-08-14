package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AMGenDamageTypes extends AMNameProvider {
    default void generateDamageTypes(AMDamageTypeProvider provider, HolderLookup.Provider registries, FabricDynamicRegistryProvider.@NotNull Entries entries) {}

    static FabricDataGenerator.Pack.@NonNull RegistryDependentFactory<?> listProvider(String name, List<Object> list) {
        return (dataOutput, future) -> new AMDamageTypeProvider(new AMGenDamageTypes() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void generateDamageTypes(AMDamageTypeProvider provider, HolderLookup.Provider registries, FabricDynamicRegistryProvider.@NotNull Entries entries) {
                for (var object : list) if (object instanceof AMGenDamageTypes task) task.generateDamageTypes(provider, registries, entries);
            }
        }, dataOutput, future);
    }

    class AMDamageTypeProvider extends FabricDynamicRegistryProvider {
        private final AMGenDamageTypes dataGen;

        public AMDamageTypeProvider(AMGenDamageTypes gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        protected void configure(HolderLookup.@NonNull Provider registries, @NotNull Entries entries) {
            this.dataGen.generateDamageTypes(this, registries, entries);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " Tags for Damage Types";
        }
    }
}