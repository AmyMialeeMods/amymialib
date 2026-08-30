package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenFlatGeneratorPresetTags extends AMNameProvider {
    default void generateFlatLevelGeneratorPresetTags(AMFlatLevelGeneratorPresetTagsProvider provider, HolderLookup.Provider arg) {}

    class AMFlatLevelGeneratorPresetTagsProvider extends FabricTagsProvider<FlatLevelGeneratorPreset> {
        private final AMGenFlatGeneratorPresetTags dataGen;

        public AMFlatLevelGeneratorPresetTagsProvider(AMGenFlatGeneratorPresetTags gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, Registries.FLAT_LEVEL_GENERATOR_PRESET, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateFlatLevelGeneratorPresetTags(this, arg);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenFlatGeneratorPresetTags.class, p -> p.generateFlatLevelGeneratorPresetTags(this, arg));
        }

        @Override
        public @NonNull TagAppender<FlatLevelGeneratorPreset> builder(@NonNull TagKey<FlatLevelGeneratorPreset> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}