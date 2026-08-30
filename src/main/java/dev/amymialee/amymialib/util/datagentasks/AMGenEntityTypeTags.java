package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenEntityTypeTags extends AMNameProvider {
    default void generateEntityTypeTags(AMEntityTypeTagsProvider provider, HolderLookup.Provider arg) {}

    class AMEntityTypeTagsProvider extends FabricTagsProvider.EntityTypeTagsProvider {
        private final AMGenEntityTypeTags dataGen;

        public AMEntityTypeTagsProvider(AMGenEntityTypeTags gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateEntityTypeTags(this, arg);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenEntityTypeTags.class, p -> p.generateEntityTypeTags(this, arg));
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