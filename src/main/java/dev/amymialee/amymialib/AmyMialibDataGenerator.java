package dev.amymialee.amymialib;

import com.google.common.collect.ImmutableSet;
import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class AmyMialibDataGenerator extends AMDatagen {
    public static final ResourceKey<FlatLevelGeneratorPreset> DEV_READY = ResourceKey.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, AmyMialib.id("dev_ready"));
    public static final ResourceKey<FlatLevelGeneratorPreset> BLAST_PROOF = ResourceKey.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, AmyMialib.id("blast_proof"));

    public AmyMialibDataGenerator() {
        super(AmyMialib.REGISTRY);
    }

    @Override
    public void generateTranslations(AMLanguageProvider provider, HolderLookup.Provider registryLookup, FabricLanguageProvider.@NonNull TranslationBuilder builder) {
        builder.add(DEV_READY.identifier().toLanguageKey("flat_world_preset"), "Dev Ready");
        builder.add(BLAST_PROOF.identifier().toLanguageKey("flat_world_preset"), "Blast Proof");
    }

    @Override
    public void generateFlatLevelGeneratorPresets(AMFlatLevelGeneratorPresetProvider provider, @NonNull Consumer<AMFlatLevelGeneratorPresetProvider.FlatLevelGeneratorPresetData> consumer) {
        consumer.accept(new AMFlatLevelGeneratorPresetProvider.FlatLevelGeneratorPresetData(DEV_READY, Items.AMETHYST_SHARD, Biomes.DARK_FOREST, ImmutableSet.of(), false, false, new FlatLayerInfo(15, Blocks.RED_SANDSTONE), new FlatLayerInfo(112, Blocks.DEEPSLATE), new FlatLayerInfo(1, Blocks.BEDROCK)));
        consumer.accept(new AMFlatLevelGeneratorPresetProvider.FlatLevelGeneratorPresetData(BLAST_PROOF, Items.TNT, Biomes.DARK_FOREST, ImmutableSet.of(), false, false, new FlatLayerInfo(15, Blocks.OBSIDIAN), new FlatLayerInfo(112, Blocks.REINFORCED_DEEPSLATE), new FlatLayerInfo(1, Blocks.BEDROCK)));
    }

    @Override
    public void generateFlatLevelGeneratorPresetTags(@NonNull AMFlatLevelGeneratorPresetTagsProvider provider, HolderLookup.Provider arg) {
        provider.builder(FlatLevelGeneratorPresetTags.VISIBLE).setReplace(false).addOptional(DEV_READY).addOptional(BLAST_PROOF);
    }
}