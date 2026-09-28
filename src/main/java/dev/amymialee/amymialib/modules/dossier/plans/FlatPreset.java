package dev.amymialee.amymialib.modules.dossier.plans;

import dev.amymialee.amymialib.modules.dossier.DossierRegistries;
import dev.amymialee.amymialib.modules.dossier.data.DossierDynamic;
import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.dossier.data.DossierTranslations;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FlatLevelGeneratorPresetTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FlatPreset implements DossierDynamic, DossierRegistries, DossierTranslations, DossierTags {
    public final ResourceKey<FlatLevelGeneratorPreset> key;
    public final String name;
    public final ResourceKey<Item> icon;
    public final ResourceKey<Biome> biome;
    public final Set<ResourceKey<StructureSet>> structures;
    public final boolean decoration;
    public final boolean addLakes;
    public final FlatLayerInfo[] layers;

    private FlatPreset(Scribbler scribbler, String path, String name, ResourceKey<Item> icon, ResourceKey<Biome> biome, Set<ResourceKey<StructureSet>> structures, boolean decoration, boolean addLakes, FlatLayerInfo... layers) {
        this.key = ResourceKey.create(Registries.FLAT_LEVEL_GENERATOR_PRESET, scribbler.id(path));
        this.name = name;
        this.icon = icon;
        this.biome = biome;
        this.structures = structures;
        this.decoration = decoration;
        this.addLakes = addLakes;
        this.layers = layers;
        scribbler.dossierData.add(this);
    }

    public static ResourceKey<FlatLevelGeneratorPreset> of(Scribbler scribbler, String path, String name, ResourceKey<Item> icon, ResourceKey<Biome> biome, Set<ResourceKey<StructureSet>> structures, boolean decoration, boolean addLakes, FlatLayerInfo... layers) {
        var flatPreset = new FlatPreset(scribbler, path, name, icon, biome, structures, decoration, addLakes, layers);
        return flatPreset.key;
    }

    @Override
    public String name() {
        return "Flat Preset %s".formatted(this.name);
    }

    @Override
    public void commitTranslations(DossierTranslations.Provider provider, HolderLookup.Provider lookup, FabricLanguageProvider.TranslationBuilder builder) {
        builder.add(key.identifier().toLanguageKey("flat_world_preset"), this.name);
    }

    @Override
    public void commitTags(DossierTags.Provider provider) {
        provider.tag(FlatLevelGeneratorPresetTags.VISIBLE).add(key);
    }

    @Override
    public void commitDynamic(DossierDynamic.Provider provider, HolderLookup.Provider lookup, FabricDynamicRegistryProvider.Entries entries) {
        entries.add((HolderLookup.RegistryLookup<FlatLevelGeneratorPreset>) entries.getLookup(this.key.registryKey()), this.key);
    }

    @Override
    public void commitRegistries(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.FLAT_LEVEL_GENERATOR_PRESET, registry -> this.createFlatPreset(registry, this.key, this.icon, this.biome, this.structures, this.decoration, this.addLakes, this.layers));
    }

    public final void createFlatPreset(BootstrapContext<FlatLevelGeneratorPreset> registry, ResourceKey<FlatLevelGeneratorPreset> key, ResourceKey<Item> icon, ResourceKey<Biome> biome, Set<ResourceKey<StructureSet>> structures, boolean decoration, boolean addLakes, FlatLayerInfo... layers) {
        var structureSets = registry.lookup(Registries.STRUCTURE_SET);
        var generator = new FlatLevelGeneratorSettings(Optional.of(HolderSet.direct(structures.stream().map(structureSets::getOrThrow).collect(Collectors.toList()))), registry.lookup(Registries.BIOME).getOrThrow(biome), FlatLevelGeneratorSettings.createLakesList(registry.lookup(Registries.PLACED_FEATURE)));
        if (decoration) generator.setDecoration();
        if (addLakes) generator.setAddLakes();
        for (var i = layers.length - 1; i >= 0; i--) generator.getLayersInfo().add(layers[i]);
        registry.register(key, new FlatLevelGeneratorPreset(registry.lookup(Registries.ITEM).getOrThrow(icon), generator));
    }
}