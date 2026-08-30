package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenItemTags extends AMNameProvider {
    default void generateItemTags(AMItemTagsProvider provider, HolderLookup.Provider arg) {}

    class AMItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
        private final AMGenItemTags dataGen;

        public AMItemTagsProvider(AMGenItemTags gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateItemTags(this, arg);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenItemTags.class, p -> p.generateItemTags(this, arg));
        }

        @Override
        public @NonNull BlockItemTagAppender<Item> builder(@NonNull TagKey<Item> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}