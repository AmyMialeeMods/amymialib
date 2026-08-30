package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface AMGenAdvancements extends AMNameProvider {
    default void generateAdvancements(AMAdvancementProvider provider, HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {}

    class AMAdvancementProvider extends FabricAdvancementProvider {
        private final AMGenAdvancements dataGen;

        public AMAdvancementProvider(AMGenAdvancements gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
            super(output, registryLookup);
            this.dataGen = gen;
        }

        @Override
        public void generateAdvancement(HolderLookup.@NonNull Provider registryLookup, @NonNull Consumer<AdvancementHolder> consumer) {
            this.dataGen.generateAdvancements(this, registryLookup, consumer);
            if (this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenAdvancements.class, p -> p.generateAdvancements(this, registryLookup, consumer));
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}