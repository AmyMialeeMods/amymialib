package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AMGenFluidTags extends AMNameProvider {
    default void generateFluidTags(AMFluidTagsProvider provider, HolderLookup.Provider arg) {}

    static FabricDataGenerator.Pack.@NonNull RegistryDependentFactory<?> listProvider(String name, List<Object> list) {
        return (dataOutput, future) -> new AMFluidTagsProvider(new AMGenFluidTags() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void generateFluidTags(AMFluidTagsProvider provider, HolderLookup.Provider arg) {
                for (var object : list) if (object instanceof AMGenFluidTags task) task.generateFluidTags(provider, arg);
            }
        }, dataOutput, future);
    }

    class AMFluidTagsProvider extends FabricTagsProvider.FluidTagsProvider {
        private final AMGenFluidTags dataGen;

        public AMFluidTagsProvider(AMGenFluidTags gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
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
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}