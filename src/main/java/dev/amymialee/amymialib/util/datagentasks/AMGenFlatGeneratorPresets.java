package dev.amymialee.amymialib.util.datagentasks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface AMGenFlatGeneratorPresets extends AMNameProvider {
    default void generateFlatLevelGeneratorPresets(AMFlatLevelGeneratorPresetProvider provider, Consumer<AMFlatLevelGeneratorPresetProvider.FlatLevelGeneratorPresetData> consumer) {}

    class AMFlatLevelGeneratorPresetProvider implements DataProvider {
        private final AMGenFlatGeneratorPresets dataGen;
        private final PackOutput.PathProvider pathResolver;
        private final CompletableFuture<HolderLookup.Provider> registriesFuture;

        public AMFlatLevelGeneratorPresetProvider(AMGenFlatGeneratorPresets gen, @NonNull FabricPackOutput packOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            this.dataGen = gen;
            this.registriesFuture = registriesFuture;
            this.pathResolver = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "worldgen/flat_level_generator_preset");
        }

        public void generate(Consumer<FlatLevelGeneratorPresetData> consumer) {
            this.dataGen.generateFlatLevelGeneratorPresets(this, consumer);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenFlatGeneratorPresets.class, p -> p.generateFlatLevelGeneratorPresets(this, consumer));
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
            return this.dataGen.getName() + " Flat Level Generator Presets";
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
}