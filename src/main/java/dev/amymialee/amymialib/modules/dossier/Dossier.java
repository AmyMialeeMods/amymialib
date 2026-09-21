package dev.amymialee.amymialib.modules.dossier;

import dev.amymialee.amymialib.modules.dossier.data.*;
import dev.amymialee.amymialib.modules.dossier.data.DossierModels;
import dev.amymialee.amymialib.modules.dossier.data.DossierSounds;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Fabric Data Generator with many fabric data providers pre-included for easy overriding.
 */
public class Dossier implements DataGeneratorEntrypoint, DossierExtras, DossierDynamic, DossierLanyard,
        DossierAdvancements, DossierBlockLoot, DossierEntityLoot, DossierTranslations, DossierLoot,
        DossierModels.DossierBlockModels, DossierModels.DossierItemModels, DossierRecipes, DossierSounds, DossierTags {
    public final Scribbler scribbler;
    private String name = "Dossier";

    public Dossier(String modId) {
        this(new Scribbler(modId));
    }

    public Dossier(Scribbler scribbler) {
        this.scribbler = scribbler;
        this.scribbler.dossierData.add(this);
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        this.name = generator.getModContainer().getMetadata().getName() + " " + this.name;
        var pack = generator.createPack();
        var set = new HashSet<String>();
        for (var method : this.getClass().getDeclaredMethods()) set.add(method.getName());
        /* Extras */
        if (this.should(set, "commitProviders", DossierExtras.class)) for (var task : this.scribbler.dossierData) if (task instanceof DossierExtras extra) extra.commitProviders(pack);
        if (this.should(set, "commitDynamic", DossierDynamic.class)) pack.addProvider((dataOutput, future) -> new DossierDynamic.Provider(this, dataOutput, future));
        /* Data */
        if (this.should(set, "commitAdvancements", DossierAdvancements.class)) pack.addProvider((dataOutput, future) -> new DossierAdvancements.Provider(this, dataOutput, future));
        if (this.should(set, "commitBlockLoot", DossierBlockLoot.class)) pack.addProvider((dataOutput, future) -> new DossierBlockLoot.Provider(this, dataOutput, future));
        if (this.should(set, "commitEntityLoot", DossierEntityLoot.class)) pack.addProvider((dataOutput, future) -> new DossierEntityLoot.Provider(this, dataOutput, future));
        if (this.should(set, "commitTranslations", DossierTranslations.class)) pack.addProvider((dataOutput, future) -> new DossierTranslations.Provider(this, dataOutput, future));
        if (this.should(set, "commitLoot", DossierLoot.class)) pack.addProvider((dataOutput, future) -> new DossierLoot.Provider(this, dataOutput, future));
        if (this.should(set, "commitRecipes", DossierRecipes.class)) pack.addProvider((dataOutput, future) -> new DossierRecipes.Provider(this, dataOutput, future));
        if (this.should(set, "commitBlockModels", DossierBlockModels.class) || this.should(set, "commitItemModels", DossierItemModels.class)) pack.addProvider((dataOutput, future) -> new DossierModels.Provider(this, dataOutput, future));
        if (this.should(set, "commitSounds", DossierSounds.class)) pack.addProvider((dataOutput, future) -> new DossierSounds.Provider(this, dataOutput, future));
        if (this.should(set, "commitTags", DossierTags.class)) pack.addProvider((dataOutput, future) -> new DossierTags.Provider(this, dataOutput, future));
    }

    private boolean should(HashSet<String> set, String method, Class<?> clazz) {
        return set.contains(method) || this.scribbler.dossierData.stream().anyMatch(o -> clazz.isInstance(o) && o != this);
    }

    public @SuppressWarnings("unchecked") <T> void registryEach(Class<T> clazz, Consumer<T> consumer) {
        this.scribbler.dossierData.forEach(t -> { if (clazz.isInstance(t)) consumer.accept((T) t); });
    }

    public final void createFlatPreset(BootstrapContext<FlatLevelGeneratorPreset> registry, ResourceKey<FlatLevelGeneratorPreset> key, ResourceKey<Item> icon, ResourceKey<Biome> biome, Set<ResourceKey<StructureSet>> structures, boolean decoration, boolean addLakes, FlatLayerInfo... layers) {
        var structureSets = registry.lookup(Registries.STRUCTURE_SET);
        var generator = new FlatLevelGeneratorSettings(Optional.of(HolderSet.direct(structures.stream().map(structureSets::getOrThrow).collect(Collectors.toList()))), registry.lookup(Registries.BIOME).getOrThrow(biome), FlatLevelGeneratorSettings.createLakesList(registry.lookup(Registries.PLACED_FEATURE)));
        if (decoration) generator.setDecoration();
        if (addLakes) generator.setAddLakes();
        for (var i = layers.length - 1; i >= 0; i--) generator.getLayersInfo().add(layers[i]);
        registry.register(key, new FlatLevelGeneratorPreset(registry.lookup(Registries.ITEM).getOrThrow(icon), generator));
    }

    /* Extras */
    @Override public void commitProviders(FabricDataGenerator.Pack pack) {}
    @Override public void commitDynamic(DossierDynamic.Provider provider, HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries) {}
    /* Data */
    @Override public void commitAdvancements(DossierAdvancements.Provider provider, HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {}
    @Override public void commitBlockLoot(DossierBlockLoot.Provider provider) {}
    @Override public void commitEntityLoot(DossierEntityLoot.Provider provider) {}
    @Override public void commitTranslations(DossierTranslations.Provider provider, HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder builder) {}
    @Override public void commitLoot(DossierLoot.Provider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {}
    @Override public void commitItemModels(DossierModels.Provider provider, ItemModelGenerators generator) {}
    @Override public void commitBlockModels(DossierModels.Provider provider, BlockModelGenerators generator) {}
    @Override public void commitRecipes(DossierRecipes.Provider provider, RecipeProvider recipeGenerator, HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {}
    @Override public void commitSounds(DossierSounds.Provider provider, HolderLookup.Provider registryLookup, FabricSoundsProvider.SoundExporter exporter) {}
    @Override public void commitTags(DossierTags.Provider provider) {}
    /* Name */
    @Override public String name() {
        return this.name;
    }
}