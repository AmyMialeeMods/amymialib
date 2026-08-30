package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenBlockEntityTypeTags extends AMNameProvider {
    default void generateBlockEntityTypeTags(AMBlockEntityTypeTagsProvider provider, HolderLookup.Provider arg) {}

    class AMBlockEntityTypeTagsProvider extends FabricTagsProvider.BlockEntityTypeTagsProvider {
        private final AMGenBlockEntityTypeTags dataGen;

        public AMBlockEntityTypeTagsProvider(AMGenBlockEntityTypeTags gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateBlockEntityTypeTags(this, arg);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenBlockEntityTypeTags.class, p -> p.generateBlockEntityTypeTags(this, arg));
        }

        @Override
        protected @NonNull TagAppender<BlockEntityType<?>> builder(@NonNull TagKey<BlockEntityType<?>> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}