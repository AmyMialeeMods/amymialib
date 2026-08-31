package dev.amymialee.amymialib.util;

import dev.amymialee.amymialib.util.datagentasks.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

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
        if (should(set, "generateBlockStateModels", AMGenModels.class) || should(set, "generateItemModels", AMGenModels.class)) pack.addProvider((dataOutput, _) -> new AMModelProvider(this, dataOutput));
        if (should(set, "generateSounds", AMGenSounds.class)) pack.addProvider((dataOutput, future) -> new AMSoundsProvider(this, dataOutput, future));
        if (should(set, "generateAdvancements", AMGenAdvancements.class)) pack.addProvider((dataOutput, future) -> new AMGenAdvancements.AMAdvancementProvider(this, dataOutput, future));
        if (should(set, "generateBlockLootTables", AMGenBlockLootTables.class)) pack.addProvider((dataOutput, future) -> new AMBlockLootSubProvider(this, dataOutput, future));
        if (should(set, "generateEntityLootTables", AMGenEntityLootTables.class)) pack.addProvider((dataOutput, future) -> new AMEntityLootTableProvider(this, dataOutput, future));
        if (should(set, "generateTranslations", AMGenTranslations.class)) pack.addProvider((dataOutput, future) -> new AMLanguageProvider(this, dataOutput, future));
        if (should(set, "generateLootTables", AMGenLootTables.class)) pack.addProvider((dataOutput, future) -> new AMLootTableProvider(this, dataOutput, future));
        if (should(set, "generateRecipes", AMGenRecipes.class)) pack.addProvider((dataOutput, future) -> new AMRecipeProvider(this, dataOutput, future));
        if (should(set, "generateDamageTypes", AMGenDamageTypes.class)) pack.addProvider((dataOutput, future) -> new AMDamageTypeProvider(this, dataOutput, future));
        if (should(set, "generateFlatGeneratorPresets", AMGenFlatGeneratorPresets.class)) pack.addProvider((dataOutput, future) -> new AMFlatLevelGeneratorPresetProvider(this, dataOutput, future));
        if (should(set, "generateDynamic", AMGenDynamic.class)) pack.addProvider((dataOutput, future) -> new AMDynamicProvider(this, dataOutput, future));
        /* Tag Providers */
        if (should(set, "generateBlockTags", AMGenBlockTags.class)) pack.addProvider((dataOutput, future) -> new AMBlockTagsProvider(this, dataOutput, future));
        if (should(set, "generateBlockEntityTypeTags", AMGenBlockEntityTypeTags.class)) pack.addProvider((dataOutput, future) -> new AMBlockEntityTypeTagsProvider(this, dataOutput, future));
        if (should(set, "generateItemTags", AMGenItemTags.class)) pack.addProvider((dataOutput, future) -> new AMItemTagsProvider(this, dataOutput, future));
        if (should(set, "generateFluidTags", AMGenFluidTags.class)) pack.addProvider((dataOutput, future) -> new AMFluidTagsProvider(this, dataOutput, future));
        if (should(set, "generateEntityTypeTags", AMGenEntityTypeTags.class)) pack.addProvider((dataOutput, future) -> new AMGenEntityTypeTags.AMEntityTypeTagsProvider(this, dataOutput, future));
        if (should(set, "generateDamageTypeTags", AMGenDamageTypeTags.class)) pack.addProvider((dataOutput, future) -> new AMDamageTypeTagsProvider(this, dataOutput, future));
        if (should(set, "generateBannerPatternTags", AMGenBannerPatternTags.class)) pack.addProvider((dataOutput, future) -> new AMBannerPatternTagsProvider(this, dataOutput, future));
        if (should(set, "generateFlatGeneratorPresetTags", AMGenFlatGeneratorPresetTags.class)) pack.addProvider((dataOutput, future) -> new AMFlatLevelGeneratorPresetTagsProvider(this, dataOutput, future));
        /* Extras */
        if (should(set, "addExtraDataProviders", AMGenExtra.class)) this.addExtraDataProviders(pack);
        if (this.heldRegistry != null) for (var task : this.heldRegistry.datagenTasks) if (task instanceof AMGenExtra extra) extra.addExtraDataProviders(pack);
    }

    public boolean should(@NonNull HashSet<String> set, String method, Class<?> clazz) {
        return set.contains(method) || (this.heldRegistry != null && this.heldRegistry.datagenTasks.stream().anyMatch(clazz::isInstance));
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