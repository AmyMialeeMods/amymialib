package dev.amymialee.amymialib.util.datagentasks;

import dev.amymialee.amymialib.util.AMDatagen;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public interface AMGenRecipes extends AMNameProvider {
    default void generateRecipes(AMRecipeProvider provider, RecipeProvider recipeGenerator, HolderLookup.Provider registryLookup, RecipeOutput exporter) {}

    class AMRecipeProvider extends FabricRecipeProvider {
        private final AMGenRecipes dataGen;

        public AMRecipeProvider(AMGenRecipes gen, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dataGen = gen;
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registryLookup, @NonNull RecipeOutput exporter) {
            return new RecipeProvider(registryLookup, exporter) {
                @Override
                public void buildRecipes() {
                    AMRecipeProvider.this.dataGen.generateRecipes(AMRecipeProvider.this, this, this.registries, this.output);
                    if (AMRecipeProvider.this.dataGen instanceof AMDatagen dataGen) dataGen.registryEach(AMGenRecipes.class, p -> p.generateRecipes(AMRecipeProvider.this, this, this.registries, this.output));
                }
            };
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " Recipes";
        }
    }
}