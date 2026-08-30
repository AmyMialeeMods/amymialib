package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenBlockTags extends AMNameProvider {
    default void generateBlockTags(AMBlockTagsProvider provider, HolderLookup.Provider arg) {}

    class AMBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
        private final AMGenBlockTags dataGen;

        public AMBlockTagsProvider(AMGenBlockTags gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateBlockTags(this, arg);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenBlockTags.class, p -> p.generateBlockTags(this, arg));
        }

        @Override
        public @NonNull BlockItemTagAppender<Block> builder(@NonNull TagKey<Block> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}