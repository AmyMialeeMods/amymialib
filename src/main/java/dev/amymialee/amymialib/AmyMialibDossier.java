package dev.amymialee.amymialib;

import com.google.common.collect.ImmutableSet;
import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.data.DossierDynamic;
import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.dossier.data.DossierTranslations;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

public class AmyMialibDossier extends Dossier {
    public static final ResourceKey<FlatLevelGeneratorPreset> DEV_READY = ResourceKey.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, AmyMialib.id("dev_ready"));
    public static final ResourceKey<FlatLevelGeneratorPreset> BLAST_PROOF = ResourceKey.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, AmyMialib.id("blast_proof"));

    public AmyMialibDossier() {
        super(AmyMialib.SCRIBBLER);
    }

    @Override
    public void commitDynamic(DossierDynamic.Provider provider, HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {
        provider.add(entries, Registries.FLAT_LEVEL_GENERATOR_PRESET);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.FLAT_LEVEL_GENERATOR_PRESET, registry -> {
            this.createFlatPreset(registry, DEV_READY, ItemIds.AMETHYST_SHARD, Biomes.DARK_FOREST, ImmutableSet.of(), false, false, new FlatLayerInfo(15, Blocks.RED_SANDSTONE), new FlatLayerInfo(112, Blocks.DEEPSLATE), new FlatLayerInfo(1, Blocks.BEDROCK));
            this.createFlatPreset(registry, BLAST_PROOF, BlockItemIds.TNT.item(), Biomes.DARK_FOREST, ImmutableSet.of(), false, false, new FlatLayerInfo(15, Blocks.OBSIDIAN), new FlatLayerInfo(112, Blocks.REINFORCED_DEEPSLATE), new FlatLayerInfo(1, Blocks.BEDROCK));
        });
    }

    @Override
    public void commitTranslations(DossierTranslations.Provider provider, HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder builder) {
        builder.add(DEV_READY.identifier().toLanguageKey("flat_world_preset"), "Dev Ready");
        builder.add(BLAST_PROOF.identifier().toLanguageKey("flat_world_preset"), "Blast Proof");
    }

    @Override
    public void commitTags(DossierTags.Provider provider) {
        provider.builder(FlatLevelGeneratorPresetTags.VISIBLE).add(DEV_READY).add(BLAST_PROOF);
    }
}