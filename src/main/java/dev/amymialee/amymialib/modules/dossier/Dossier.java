package dev.amymialee.amymialib.modules.dossier;

import dev.amymialee.amymialib.modules.dossier.data.*;
import dev.amymialee.amymialib.modules.scribbler.AutoQueue;
import dev.amymialee.amymialib.modules.scribbler.AutoScanner;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import dev.amymialee.amymialib.modules.scribbler.auto.DossierData;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Fabric Data Generator with many fabric data providers pre-included for easy overriding.
 */
public class Dossier implements DataGeneratorEntrypoint, DossierExtras, DossierRegistries, DossierDynamic, DossierLanyard,
        DossierAdvancements, DossierBlockLoot, DossierEntityLoot, DossierTranslations, DossierLoot,
        DossierModels.DossierBlockModels, DossierModels.DossierItemModels, DossierRecipes, DossierSounds, DossierTags {
    public final Scribbler scribbler;
    private String name = "Dossier";

    public Dossier(String modId) {
        this(new Scribbler(modId));
    }

    public Dossier(Scribbler scribbler) {
        this.scribbler = scribbler;
        this.scribbler.dossierData.add(this);
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        this.name = generator.getModContainer().getMetadata().getName() + " " + this.name;
        var pack = generator.createPack();
        var set = new HashSet<String>();
        for (var method : this.getClass().getDeclaredMethods()) set.add(method.getName());
        /* Extras */
        if (this.should(set, "commitProviders", DossierExtras.class)) for (var task : this.scribbler.dossierData) if (task instanceof DossierExtras extra) extra.commitProviders(pack);
        if (this.should(set, "commitDynamic", DossierDynamic.class, DossierCtxDynamic.class)) pack.addProvider((dataOutput, future) -> new DossierDynamic.Provider(this, dataOutput, future));
        /* Data */
        if (this.should(set, "commitAdvancements", DossierAdvancements.class, DossierCtxAdvancements.class)) pack.addProvider((dataOutput, future) -> new DossierAdvancements.Provider(this, dataOutput, future));
        if (this.should(set, "commitBlockLoot", DossierBlockLoot.class)) pack.addProvider((dataOutput, future) -> new DossierBlockLoot.Provider(this, dataOutput, future));
        if (this.should(set, "commitEntityLoot", DossierEntityLoot.class)) pack.addProvider((dataOutput, future) -> new DossierEntityLoot.Provider(this, dataOutput, future));
        if (this.should(set, "commitTranslations", DossierTranslations.class, DossierCtxTranslations.class, DossierBuilderTranslations.class)) pack.addProvider((dataOutput, future) -> new DossierTranslations.Provider(this, dataOutput, future));
        if (this.should(set, "commitLoot", DossierLoot.class, DossierCtxLoot.class)) pack.addProvider((dataOutput, future) -> new DossierLoot.Provider(this, dataOutput, future));
        if (this.should(set, "commitRecipes", DossierRecipes.class)) pack.addProvider((dataOutput, future) -> new DossierRecipes.Provider(this, dataOutput, future));
        if (this.should(set, "commitBlockModels", DossierBlockModels.class) || this.should(set, "commitItemModels", DossierItemModels.class)) pack.addProvider((dataOutput, _) -> new DossierModels.Provider(this, dataOutput));
        if (this.should(set, "commitSounds", DossierSounds.class, DossierCtxSounds.class)) pack.addProvider((dataOutput, future) -> new DossierSounds.Provider(this, dataOutput, future));
        if (this.should(set, "commitTags", DossierTags.class)) pack.addProvider((dataOutput, future) -> new DossierTags.Provider(this, dataOutput, future));
    }

    /**
     * Run by each of the Dossier providers, allowing them to iterate on all the sources of data.
     */
    @ApiStatus.Internal @SafeVarargs
    public final @SuppressWarnings("unchecked") <T> void registryEach(Consumer<T> consumer, Class<T> ... clazz) {
        for (var c : clazz) {
            this.scribbler.dossierData.forEach(t -> { if (c.isInstance(t)) consumer.accept((T) t); });
            AutoQueue.forAny(this.scribbler, p -> AutoScanner.findFunctionalInterfaces(scribbler.logger, DossierData.class, p, c).forEach(consumer));
        }
    }

    /**
     * Checks if any of the given Providers should run, by checking if they have anything to do if they did.
     */
    private boolean should(HashSet<String> set, String method, Class<?> ... clazz) {
        for (var c : clazz) {
            if (set.contains(method)) return true;
            for (var o : this.scribbler.dossierData) if (c.isInstance(o)) return true;
            if (AutoQueue.findAny(this.scribbler, p -> AutoScanner.checkForFunctionalInterfaces(DossierData.class, p, c))) return true;
        }
        return false;
    }

    /* Registries */
    @Override public final void buildRegistry(RegistrySetBuilder registryBuilder) {
        this.registryEach(p -> p.commitRegistries(registryBuilder), DossierRegistries.class);
    }
    @Override public void commitRegistries(RegistrySetBuilder registryBuilder) {}
    /* Extras */
    @Override public void commitProviders(FabricDataGenerator.Pack pack) {}
    @Override public void commitDynamic(DossierDynamic.Provider provider, HolderLookup.Provider lookup, FabricDynamicRegistryProvider.Entries entries) {}
    /* Data */
    @Override public void commitAdvancements(DossierAdvancements.Provider provider, HolderLookup.Provider lookup, Consumer<AdvancementHolder> consumer) {}
    @Override public void commitBlockLoot(DossierBlockLoot.Provider provider) {}
    @Override public void commitEntityLoot(DossierEntityLoot.Provider provider) {}
    @Override public void commitTranslations(DossierTranslations.Provider provider, HolderLookup.Provider lookup, FabricLanguageProvider.TranslationBuilder builder) {}
    @Override public void commitLoot(DossierLoot.Provider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {}
    @Override public void commitItemModels(DossierModels.Provider provider, ItemModelGenerators generator) {}
    @Override public void commitBlockModels(DossierModels.Provider provider, BlockModelGenerators generator) {}
    @Override public void commitRecipes(DossierRecipes.Provider data, RecipeProvider provider) {}
    @Override public void commitSounds(DossierSounds.Provider provider, HolderLookup.Provider lookup, FabricSoundsProvider.SoundExporter exporter) {}
    @Override public void commitTags(DossierTags.Provider provider) {}


    /* Name */
    @Override public String name() { return this.name; }
}