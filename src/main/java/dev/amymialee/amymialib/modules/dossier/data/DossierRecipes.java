package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import dev.amymialee.amymialib.modules.scribbler.helpers.BlockBatch;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierRecipes extends DossierLanyard {
    void commitRecipes(Provider data, RecipeProvider provider);

    @Override default String name() { return "Recipes"; }

    final class Provider extends FabricRecipeProvider {
        private final Dossier dossier;
        private RecipeProvider recipeProvider;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dossier = dossier;
        }

        @Override
        protected @NonNull RecipeProvider createRecipeProvider(HolderLookup.@NonNull Provider registries, @NonNull BootstrapContext<Recipe<?>> recipes, @NonNull BootstrapContext<Advancement> advancements) {
            recipeProvider = new RecipeProvider(recipes, advancements) {
                @Override
                public void buildRecipes() {
                    Provider.this.dossier.registryEach(p -> p.commitRecipes(Provider.this, this), DossierRecipes.class);
                }
            };
            return recipeProvider;
        }

        public void generateBatch(BlockBatch batch, boolean stonecutting) {
            var base = Objects.requireNonNull(batch.getItem());
            Optional.ofNullable(batch.get(BlockBatch.Variant.STAIRS)).ifPresent(i -> recipeProvider.stairBuilder(i, Ingredient.of(base)).unlockedBy(RecipeProvider.getHasName(base), recipeProvider.has(base)).save(recipeProvider.output));
            Optional.ofNullable(batch.get(BlockBatch.Variant.SLAB)).ifPresent(i -> recipeProvider.slabBuilder(RecipeCategory.BUILDING_BLOCKS, i, Ingredient.of(base)).unlockedBy(RecipeProvider.getHasName(base), recipeProvider.has(base)).save(recipeProvider.output));
            Optional.ofNullable(batch.get(BlockBatch.Variant.CARPET)).ifPresent(i -> recipeProvider.carpetBuilder(RecipeCategory.DECORATIONS, i, Ingredient.of(base)).unlockedBy(RecipeProvider.getHasName(base), recipeProvider.has(base)).save(recipeProvider.output));
            Optional.ofNullable(batch.get(BlockBatch.Variant.WALL)).ifPresent(i -> recipeProvider.wallBuilder(RecipeCategory.DECORATIONS, i, Ingredient.of(base)).unlockedBy(RecipeProvider.getHasName(base), recipeProvider.has(base)).save(recipeProvider.output));
            Optional.ofNullable(batch.get(BlockBatch.Variant.PANE)).ifPresent(i -> recipeProvider.shaped(RecipeCategory.DECORATIONS, i, 16).define('#', base).pattern("###").pattern("###").unlockedBy(RecipeProvider.getHasName(base), recipeProvider.has(base)).save(recipeProvider.output));
            Optional.ofNullable(batch.get(BlockBatch.Variant.FENCE)).ifPresent(i -> recipeProvider.fenceBuilder(i, Ingredient.of(base)).unlockedBy(RecipeProvider.getHasName(base), recipeProvider.has(base)).save(recipeProvider.output));
            Optional.ofNullable(batch.get(BlockBatch.Variant.FENCE_GATE)).ifPresent(i -> recipeProvider.fenceGateBuilder(i, Ingredient.of(base)).unlockedBy(RecipeProvider.getHasName(base), recipeProvider.has(base)).save(recipeProvider.output));
            Optional.ofNullable(batch.get(BlockBatch.Variant.PRESSURE_PLATE)).ifPresent(i -> recipeProvider.pressurePlateBuilder(RecipeCategory.REDSTONE, i, Ingredient.of(base)).unlockedBy(RecipeProvider.getHasName(base), recipeProvider.has(base)).save(recipeProvider.output));
            Optional.ofNullable(batch.get(BlockBatch.Variant.BUTTON)).ifPresent(i -> recipeProvider.buttonBuilder(i, Ingredient.of(base)).unlockedBy(RecipeProvider.getHasName(base), recipeProvider.has(base)).save(recipeProvider.output));
            if (stonecutting) {
                Optional.ofNullable(batch.get(BlockBatch.Variant.STAIRS)).ifPresent(i -> recipeProvider.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, i, base, 1));
                Optional.ofNullable(batch.get(BlockBatch.Variant.SLAB)).ifPresent(i -> recipeProvider.stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, i, base, 2));
                Optional.ofNullable(batch.get(BlockBatch.Variant.WALL)).ifPresent(i -> recipeProvider.stonecutterResultFromBase(RecipeCategory.DECORATIONS, i, base, 1));
            }
        }

        @Override
        public @NonNull String getName() {
            return this.dossier.name() + " " + super.getName();
        }
    }
}