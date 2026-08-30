package dev.amymialee.amymialib.util;

import dev.amymialee.amymialib.util.datagentasks.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.function.Consumer;

/**
 * Fabric Data Generator with many fabric data providers pre-included for easy overriding.
 */
public abstract class AMDatagen implements DataGeneratorEntrypoint,
        AMGenAdvancements, AMGenBlockLootTables, AMGenDamageTypes, AMGenDynamic, AMGenEntityLootTables, AMGenFlatGeneratorPresets, AMGenLootTables, AMGenModels, AMGenRecipes, AMGenSounds, AMGenTranslations,
        AMGenBannerPatternTags, AMGenBlockEntityTypeTags, AMGenBlockTags, AMGenEntityTypeTags, AMGenFlatGeneratorPresetTags, AMGenFluidTags, AMGenItemTags, AMGenDamageTypeTags,
        AMGenExtra, AMNameProvider {
    public final AMRegistry heldRegistry;
    private String name;

    public AMDatagen() {
        this(null);
    }

    /**
     * Allows for inserting an AMRegistry into the datagen, which will allow it to run its own datagen providers after this datagen finishes.
     */
    public AMDatagen(AMRegistry heldRegistry) {
        this.heldRegistry = heldRegistry;
    }

    @Override
    public void onInitializeDataGenerator(@NotNull FabricDataGenerator generator) {
        this.name = generator.getModContainer().getMetadata().getName();
        var pack = generator.createPack();
        var set = new HashSet<String>();
        for (var method : this.getClass().getDeclaredMethods()) set.add(method.getName());
        if (set.contains("generateBlockStateModels") || set.contains("generateItemModels")) pack.addProvider((dataOutput, _) -> new AMModelProvider(this, dataOutput));
        if (set.contains("generateSounds")) pack.addProvider((dataOutput, future) -> new AMSoundsProvider(this, dataOutput, future));
        if (set.contains("generateAdvancements")) pack.addProvider((dataOutput, future) -> new AMGenAdvancements.AMAdvancementProvider(this, dataOutput, future));
        if (set.contains("generateBlockLootTables")) pack.addProvider((dataOutput, future) -> new AMBlockLootSubProvider(this, dataOutput, future));
        if (set.contains("generateEntityLootTables")) pack.addProvider((dataOutput, future) -> new AMEntityLootTableProvider(this, dataOutput, future));
        if (set.contains("generateTranslations")) pack.addProvider((dataOutput, future) -> new AMLanguageProvider(this, dataOutput, future));
        if (set.contains("generateLootTables")) pack.addProvider((dataOutput, future) -> new AMLootTableProvider(this, dataOutput, future));
        if (set.contains("generateRecipes")) pack.addProvider((dataOutput, future) -> new AMRecipeProvider(this, dataOutput, future));
        if (set.contains("generateDamageTypes")) pack.addProvider((dataOutput, future) -> new AMDamageTypeProvider(this, dataOutput, future));
        if (set.contains("generateFlatLevelGeneratorPresets")) pack.addProvider((dataOutput, future) -> new AMFlatLevelGeneratorPresetProvider(this, dataOutput, future));
        if (set.contains("generateDynamic")) pack.addProvider((dataOutput, future) -> new AMDynamicProvider(this, dataOutput, future));
        /* Tag Providers */
        if (set.contains("generateBlockTags")) pack.addProvider((dataOutput, future) -> new AMBlockTagsProvider(this, dataOutput, future));
        if (set.contains("generateBlockEntityTypeTags")) pack.addProvider((dataOutput, future) -> new AMBlockEntityTypeTagsProvider(this, dataOutput, future));
        if (set.contains("generateItemTags")) pack.addProvider((dataOutput, future) -> new AMItemTagsProvider(this, dataOutput, future));
        if (set.contains("generateFluidTags")) pack.addProvider((dataOutput, future) -> new AMFluidTagsProvider(this, dataOutput, future));
        if (set.contains("generateEntityTypeTags")) pack.addProvider((dataOutput, future) -> new AMGenEntityTypeTags.AMEntityTypeTagsProvider(this, dataOutput, future));
        if (set.contains("generateDamageTypeTags")) pack.addProvider((dataOutput, future) -> new AMDamageTypeTagsProvider(this, dataOutput, future));
        if (set.contains("generateBannerPatternTags")) pack.addProvider((dataOutput, future) -> new AMBannerPatternTagsProvider(this, dataOutput, future));
        if (set.contains("generateFlatLevelGeneratorPresetTags")) pack.addProvider((dataOutput, future) -> new AMFlatLevelGeneratorPresetTagsProvider(this, dataOutput, future));
        /* Extras */
        if (set.contains("addExtraDataProviders")) this.addExtraDataProviders(pack);
        if (this.heldRegistry != null) for (var task : this.heldRegistry.datagenTasks) if (task instanceof AMGenExtra extra) extra.addExtraDataProviders(pack);
    }

    public <T> void registryEach(Class<T> clazz, Consumer<T> consumer) {
        if (this.heldRegistry != null) this.heldRegistry.datagenTasks.forEach(t -> {
            if (clazz.isInstance(t)) consumer.accept((T) t);
        });
    }

    public String getName() {
        return this.name;
    }
}