package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AMGenDamageTypeTags extends AMNameProvider {
    default void generateDamageTypeTags(AMDamageTypeTagsProvider provider, HolderLookup.Provider arg) {}

    static FabricDataGenerator.Pack.@NonNull RegistryDependentFactory<?> listProvider(String name, List<Object> list) {
        return (dataOutput, future) -> new AMDamageTypeTagsProvider(new AMGenDamageTypeTags() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void generateDamageTypeTags(AMDamageTypeTagsProvider provider, HolderLookup.Provider arg) {
                for (var object : list) if (object instanceof AMGenDamageTypeTags task) task.generateDamageTypeTags(provider, arg);
            }
        }, dataOutput, future);
    }

    class AMDamageTypeTagsProvider extends FabricTagsProvider<DamageType> {
        private final AMGenDamageTypeTags dataGen;

        public AMDamageTypeTagsProvider(AMGenDamageTypeTags gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, Registries.DAMAGE_TYPE, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateDamageTypeTags(this, arg);
        }

        @Override
        public @NonNull TagAppender<DamageType> builder(@NonNull TagKey<DamageType> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}