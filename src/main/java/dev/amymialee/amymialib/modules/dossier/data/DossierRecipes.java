package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierRecipes extends DossierLanyard {
    void commitRecipes(Provider provider, RecipeProvider recipeProvider, HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements);

    @Override default String name() { return "Recipes"; }

    final class Provider extends FabricRecipeProvider {
        private final Dossier dossier;
        public final FabricPackOutput output;
        public final CompletableFuture<HolderLookup.Provider> registriesFuture;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dossier = dossier;
            this.output = output;
            this.registriesFuture = registriesFuture;
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
            return new RecipeProvider(recipes, advancements) {
                @Override
                public void buildRecipes() {
                    Provider.this.dossier.registryEach(DossierRecipes.class, p -> p.commitRecipes(Provider.this, this, registries, recipes, advancements));
                }
            };
        }

        @Override
        public String getName() {
            return this.dossier.name() + " " + super.getName();
        }
    }
}