package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AMGenEntityTypeTags extends AMNameProvider {
    default void generateEntityTypeTags(AMEntityTypeTagsProvider provider, HolderLookup.Provider arg) {}

    static FabricDataGenerator.Pack.@NonNull RegistryDependentFactory<?> listProvider(String name, List<Object> list) {
        return (dataOutput, future) -> new AMEntityTypeTagsProvider(new AMGenEntityTypeTags() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void generateEntityTypeTags(AMEntityTypeTagsProvider provider, HolderLookup.Provider arg) {
                for (var object : list) if (object instanceof AMGenEntityTypeTags task) task.generateEntityTypeTags(provider, arg);
            }
        }, dataOutput, future);
    }

    class AMEntityTypeTagsProvider extends FabricTagsProvider.EntityTypeTagsProvider {
        private final AMGenEntityTypeTags dataGen;

        public AMEntityTypeTagsProvider(AMGenEntityTypeTags gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateEntityTypeTags(this, arg);
        }

        @Override
        public @NonNull TagAppender<EntityType<?>> builder(@NonNull TagKey<EntityType<?>> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}