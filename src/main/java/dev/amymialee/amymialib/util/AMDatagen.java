package dev.amymialee.amymialib.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.*;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.BlockItemTagAppender;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Fabric Data Generator with many fabric data providers pre-included for easy overriding.
 */
public abstract class AMDatagen implements DataGeneratorEntrypoint {
    private String name;

    @Override
    public void onInitializeDataGenerator(@NotNull FabricDataGenerator generator) {
        this.name = generator.getModContainer().getMetadata().getName();
        var pack = generator.createPack();
        var set = new HashSet<String>();
        for (var method : this.getClass().getDeclaredMethods()) set.add(method.getName());
        if (set.contains("generateBlockStateModels") || set.contains("generateItemModels")) pack.addProvider((dataOutput, _) -> new MModelProvider(this, dataOutput));
        if (set.contains("generateSounds")) pack.addProvider((dataOutput, future) -> new AMAdvancementProvider(this, dataOutput, future));
        if (set.contains("generateAdvancements")) pack.addProvider((dataOutput, future) -> new AMAdvancementProvider(this, dataOutput, future));
        if (set.contains("generateBlockLootTables")) pack.addProvider((dataOutput, future) -> new AMBlockLootSubProvider(this, dataOutput, future));
        if (set.contains("generateEntityLootTables")) pack.addProvider((dataOutput, future) -> new AMEntityLootTableProvider(this, dataOutput, future));
        if (set.contains("generateTranslations")) pack.addProvider((dataOutput, future) -> new AMLanguageProvider(this, dataOutput, future));
        if (set.contains("generateLootTables")) pack.addProvider((dataOutput, future) -> new AMLootTableProvider(this, dataOutput, future));
        if (set.contains("generateRecipes")) pack.addProvider((dataOutput, future) -> new AMRecipeProvider(this, dataOutput, future));
        if (set.contains("generateDamageTypes")) pack.addProvider((dataOutput, future) -> new AMDamageTypeProvider(this, dataOutput, future));
        if (set.contains("generateFlatLevelGeneratorPresets")) pack.addProvider((dataOutput, future) -> new AMFlatLevelGeneratorPresetProvider(this, dataOutput, future));
        /* Tag Providers */
        if (set.contains("generateBlockTags")) pack.addProvider((dataOutput, future) -> new AMBlockTagsProvider(this, dataOutput, future));
        if (set.contains("generateBlockEntityTypeTags")) pack.addProvider((dataOutput, future) -> new AMBlockEntityTypeTagsProvider(this, dataOutput, future));
        if (set.contains("generateItemTags")) pack.addProvider((dataOutput, future) -> new AMItemTagsProvider(this, dataOutput, future));
        if (set.contains("generateFluidTags")) pack.addProvider((dataOutput, future) -> new AMFluidTagsProvider(this, dataOutput, future));
        if (set.contains("generateEntityTypeTags")) pack.addProvider((dataOutput, future) -> new AMEntityTypeTagsProvider(this, dataOutput, future));
        if (set.contains("generateDamageTypeTags")) pack.addProvider((dataOutput, future) -> new AMDamageTypeTagsProvider(this, dataOutput, future));
        if (set.contains("generateFlatLevelGeneratorPresetTags")) pack.addProvider((dataOutput, future) -> new AMFlatLevelGeneratorPresetTagsProvider(this, dataOutput, future));
        /* Extras */
        if (set.contains("addExtraDataProviders")) this.addExtraDataProviders(pack);
    }

    protected void generateBlockStateModels(MModelProvider provider, BlockModelGenerators generator) {}

    protected void generateItemModels(MModelProvider provider, ItemModelGenerators generator) {}

    protected void generateSounds(AMSoundsProvider provider, HolderLookup.Provider registryLookup, FabricSoundsProvider.SoundExporter exporter) {}

    protected void generateAdvancements(AMAdvancementProvider provider, HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {}

    protected void generateBlockLootTables(AMBlockLootSubProvider provider) {}

    protected void generateEntityLootTables(AMEntityLootTableProvider provider) {}

    protected void generateLootTables(AMLootTableProvider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {}

    protected void generateTranslations(AMLanguageProvider provider, HolderLookup.Provider registryLookup, FabricLanguageProvider.TranslationBuilder builder) {}

    protected void generateRecipes(AMRecipeProvider provider, RecipeProvider recipeGenerator, HolderLookup.Provider registryLookup, RecipeOutput exporter) {}

    protected void generateDamageTypes(AMDamageTypeProvider provider, HolderLookup.Provider registries, FabricDynamicRegistryProvider.@NotNull Entries entries) {}

    protected void generateFlatLevelGeneratorPresets(AMFlatLevelGeneratorPresetProvider provider, Consumer<AMFlatLevelGeneratorPresetProvider.FlatLevelGeneratorPresetData> consumer) {}

    protected void generateBlockTags(AMBlockTagsProvider provider, HolderLookup.Provider arg) {}

    protected void generateBlockEntityTypeTags(AMBlockEntityTypeTagsProvider provider, HolderLookup.Provider arg) {}

    protected void generateItemTags(AMItemTagsProvider provider, HolderLookup.Provider arg) {}

    protected void generateFluidTags(AMFluidTagsProvider provider, HolderLookup.Provider arg) {}

    protected void generateEntityTypeTags(AMEntityTypeTagsProvider provider, HolderLookup.Provider arg) {}

    protected void generateDamageTypeTags(AMDamageTypeTagsProvider provider, HolderLookup.Provider arg) {}

    protected void generateFlatLevelGeneratorPresetTags(AMFlatLevelGeneratorPresetTagsProvider provider, HolderLookup.Provider arg) {}

    protected void addExtraDataProviders(FabricDataGenerator.Pack pack) {}

    public static class MModelProvider extends FabricModelProvider {
        private final AMDatagen dataGen;

        public MModelProvider(AMDatagen gen, FabricPackOutput output) {
            super(output);
            this.dataGen = gen;
        }

        @Override
        public void generateBlockStateModels(@NonNull BlockModelGenerators blockModelGenerators) {
            this.dataGen.generateBlockStateModels(this, blockModelGenerators);
        }

        @Override
        public void generateItemModels(@NonNull ItemModelGenerators itemModelGenerators) {
            this.dataGen.generateItemModels(this, itemModelGenerators);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    public static class AMSoundsProvider extends FabricSoundsProvider {
        private final AMDatagen dataGen;

        public AMSoundsProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(output, registryLookup);
            this.dataGen = gen;
        }

        @Override
        protected void configure(HolderLookup.@NonNull Provider registryLookup, @NonNull SoundExporter exporter) {
            this.dataGen.generateSounds(this, registryLookup, exporter);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " Sounds";
        }
    }

    public static class AMAdvancementProvider extends FabricAdvancementProvider {
        private final AMDatagen dataGen;

        public AMAdvancementProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(output, registryLookup);
            this.dataGen = gen;
        }

        @Override
        public void generateAdvancement(HolderLookup.@NonNull Provider registryLookup, @NonNull Consumer<AdvancementHolder> consumer) {
            this.dataGen.generateAdvancements(this, registryLookup, consumer);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    public static class AMBlockLootSubProvider extends FabricBlockLootSubProvider {
        private final AMDatagen dataGen;

        public AMBlockLootSubProvider(AMDatagen gen, FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(packOutput, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        public void generate() {
            this.dataGen.generateBlockLootTables(this);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    public static class AMEntityLootTableProvider extends FabricEntityLootSubProvider {
        private final AMDatagen dataGen;

        public AMEntityLootTableProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        public void generate() {
            this.dataGen.generateEntityLootTables(this);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    public static class AMLootTableProvider extends SimpleFabricLootTableSubProvider {
        private final AMDatagen dataGen;

        public AMLootTableProvider(AMDatagen dataGen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture, LootTable.DEFAULT_PARAM_SET);
            this.dataGen = dataGen;
        }

        @Override
        public void generate(@NonNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> lootTableBiConsumer) {
            this.dataGen.generateLootTables(this, lootTableBiConsumer);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    public static class AMLanguageProvider extends FabricLanguageProvider {
        private final AMDatagen dataGen;

        public AMLanguageProvider(AMDatagen gen, FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(packOutput, registryLookup);
            this.dataGen = gen;
        }

        @Override
        public void generateTranslations(HolderLookup.@NonNull Provider registryLookup, @NonNull TranslationBuilder translationBuilder) {
            this.dataGen.generateTranslations(this, registryLookup, translationBuilder);
        }

        public void addSubtitleKey(@NonNull TranslationBuilder translationBuilder, @NotNull SoundEvent event, String translation) {
            var soundEvent = Minecraft.getInstance().getSoundManager().getSoundEvent(event.location());
            if (soundEvent == null || soundEvent.getSubtitle() == null) return;
            if (!(soundEvent.getSubtitle().getContents() instanceof TranslatableContents translatable)) return;
            translationBuilder.add(translatable.getKey(), translation);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    public static class AMRecipeProvider extends FabricRecipeProvider {
        private final AMDatagen dataGen;

        public AMRecipeProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registryLookup, @NonNull RecipeOutput exporter) {
            return new RecipeProvider(registryLookup, exporter) {
                @Override
                public void buildRecipes() {
                    AMRecipeProvider.this.dataGen.generateRecipes(AMRecipeProvider.this, this, this.registries, this.output);
                }
            };
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " Recipes";
        }
    }

    public static class AMDamageTypeProvider extends FabricDynamicRegistryProvider {
        private final AMDatagen dataGen;

        public AMDamageTypeProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        protected void configure(HolderLookup.@NonNull Provider registries, @NotNull Entries entries) {
            this.dataGen.generateDamageTypes(this, registries, entries);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " Tags for Damage Types";
        }
    }

    public static class AMFlatLevelGeneratorPresetProvider implements DataProvider {
        private final AMDatagen dataGen;
        private final PackOutput.PathProvider pathResolver;
        private final CompletableFuture<HolderLookup.Provider> registriesFuture;

        public AMFlatLevelGeneratorPresetProvider(AMDatagen gen, @NonNull FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            this.dataGen = gen;
            this.registriesFuture = registriesFuture;
            this.pathResolver = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "worldgen/flat_level_generator_preset");
        }

        public void generate(Consumer<FlatLevelGeneratorPresetData> consumer) {
            this.dataGen.generateFlatLevelGeneratorPresets(this, consumer);
        }

        @Override
        public @NonNull CompletableFuture<?> run(@NonNull CachedOutput writer) {
            return this.registriesFuture.thenCompose(_ -> {
                Set<Identifier> set = new HashSet<>();
                List<CompletableFuture<?>> list = new ArrayList<>();
                Consumer<FlatLevelGeneratorPresetData> consumer = flatPreset -> {
                    if (!set.add(flatPreset.name.identifier())) {
                        throw new IllegalStateException("Duplicate Superflat Preset " + flatPreset.name.identifier());
                    } else {
                        var path = this.pathResolver.json(flatPreset.name.identifier());
                        list.add(DataProvider.saveStable(writer, flatPreset.toJson(), path));
                    }
                };
                this.generate(consumer);
                return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
            });
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " Flat Level Generator Presets";
        }

        public static class FlatLevelGeneratorPresetData {
            protected final ResourceKey<FlatLevelGeneratorPreset> name;
            protected final ItemLike icon;
            protected final ResourceKey<Biome> biome;
            protected final Set<ResourceKey<StructureSet>> structureSetKeys;
            protected final boolean hasFeatures;
            protected final boolean hasLakes;
            protected final FlatLayerInfo[] layers;

            public FlatLevelGeneratorPresetData(
                    ResourceKey<FlatLevelGeneratorPreset> registryKey,
                    ItemLike icon,
                    ResourceKey<Biome> biome,
                    @NotNull Set<ResourceKey<StructureSet>> structureSetKeys,
                    boolean hasFeatures,
                    boolean hasLakes,
                    FlatLayerInfo... layers) {
                this.name = registryKey;
                this.icon = icon;
                this.biome = biome;
                this.structureSetKeys = structureSetKeys;
                this.hasFeatures = hasFeatures;
                this.hasLakes = hasLakes;
                this.layers = layers;
            }

            public JsonElement toJson() {
                var base = new JsonObject();
                base.addProperty("display", this.icon.asItem().toString());
                var settings = new JsonObject();
                settings.addProperty("biome", this.biome.identifier().toString());
                settings.addProperty("features", this.hasFeatures);
                settings.addProperty("lakes", this.hasLakes);
                var layersJson = new JsonArray();
                for(var i = this.layers.length - 1; i >= 0; i--) {
                    var layerInstance = this.layers[i];
                    var layerJson = new JsonObject();
                    layerJson.addProperty("block", BuiltInRegistries.BLOCK.getKey(layerInstance.getBlockState().getBlock()).toString());
                    layerJson.addProperty("height", layerInstance.getHeight());
                    layersJson.add(layerJson);
                }
                settings.add("layers", layersJson);
                var structuresJson = new JsonArray();
                for (var structureInstance : this.structureSetKeys) {
                    structuresJson.add(structureInstance.identifier().toString());
                }
                settings.add("structure_overrides", structuresJson);
                base.add("settings", settings);
                return base;
            }
        }
    }

    protected static class AMBlockTagsProvider extends FabricTagsProvider.BlockTagsProvider {
        private final AMDatagen dataGen;

        public AMBlockTagsProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateBlockTags(this, arg);
        }

        @Override
        public @NonNull BlockItemTagAppender<Block> builder(@NonNull TagKey<Block> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    protected static class AMBlockEntityTypeTagsProvider extends FabricTagsProvider.BlockEntityTypeTagsProvider {
        private final AMDatagen dataGen;

        public AMBlockEntityTypeTagsProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateBlockEntityTypeTags(this, arg);
        }

        @Override
        protected @NonNull TagAppender<BlockEntityType<?>> builder(@NonNull TagKey<BlockEntityType<?>> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    protected static class AMItemTagsProvider extends FabricTagsProvider.ItemTagsProvider {
        private final AMDatagen dataGen;

        public AMItemTagsProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateItemTags(this, arg);
        }

        @Override
        public @NonNull BlockItemTagAppender<Item> builder(@NonNull TagKey<Item> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    protected static class AMFluidTagsProvider extends FabricTagsProvider.FluidTagsProvider {
        private final AMDatagen dataGen;

        public AMFluidTagsProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateFluidTags(this, arg);
        }

        @Override
        public @NonNull TagAppender<Fluid> builder(@NonNull TagKey<Fluid> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    protected static class AMEntityTypeTagsProvider extends FabricTagsProvider.EntityTypeTagsProvider {
        private final AMDatagen dataGen;

        public AMEntityTypeTagsProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateEntityTypeTags(this, arg);
        }

        @Override
        public @NonNull TagAppender<EntityType<?>> builder(@NonNull TagKey<EntityType<?>> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    protected static class AMDamageTypeTagsProvider extends FabricTagsProvider<DamageType> {
        private final AMDatagen dataGen;

        public AMDamageTypeTagsProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, Registries.DAMAGE_TYPE, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateDamageTypeTags(this, arg);
        }

        @Override
        public @NonNull TagAppender<DamageType> builder(@NonNull TagKey<DamageType> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }

    protected static class AMFlatLevelGeneratorPresetTagsProvider extends FabricTagsProvider<FlatLevelGeneratorPreset> {
        private final AMDatagen dataGen;

        public AMFlatLevelGeneratorPresetTagsProvider(AMDatagen gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, Registries.FLAT_LEVEL_GENERATOR_PRESET, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateFlatLevelGeneratorPresetTags(this, arg);
        }

        @Override
        public @NonNull TagAppender<FlatLevelGeneratorPreset> builder(@NonNull TagKey<FlatLevelGeneratorPreset> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.name + " " + super.getName();
        }
    }
}