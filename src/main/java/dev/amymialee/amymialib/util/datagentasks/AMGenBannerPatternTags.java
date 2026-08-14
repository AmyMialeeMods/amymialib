package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AMGenBannerPatternTags extends AMNameProvider {
    default void generateBannerPatternTags(AMBannerPatternTagsProvider provider, HolderLookup.Provider arg) {}

    static FabricDataGenerator.Pack.@NonNull RegistryDependentFactory<?> listProvider(String name, List<Object> list) {
        return (dataOutput, future) -> new AMBannerPatternTagsProvider(new AMGenBannerPatternTags() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void generateBannerPatternTags(AMBannerPatternTagsProvider provider, HolderLookup.Provider arg) {
                for (var object : list) if (object instanceof AMGenBannerPatternTags task) task.generateBannerPatternTags(provider, arg);
            }
        }, dataOutput, future);
    }

    class AMBannerPatternTagsProvider extends FabricTagsProvider<BannerPattern> {
        private final AMGenBannerPatternTags dataGen;

        public AMBannerPatternTagsProvider(AMGenBannerPatternTags gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
            super(output, Registries.BANNER_PATTERN, registryLookupFuture);
            this.dataGen = gen;
        }

        @Override
        protected void addTags(HolderLookup.@NonNull Provider arg) {
            this.dataGen.generateBannerPatternTags(this, arg);
        }

        @Override
        public @NonNull TagAppender<BannerPattern> builder(@NonNull TagKey<BannerPattern> tag) {
            return super.builder(tag);
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}