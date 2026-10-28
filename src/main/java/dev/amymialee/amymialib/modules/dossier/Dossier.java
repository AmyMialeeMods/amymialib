package dev.amymialee.amymialib.modules.dossier;

import dev.amymialee.amymialib.modules.dossier.data.*;
import dev.amymialee.amymialib.modules.scribbler.AutoQueue;
import dev.amymialee.amymialib.modules.scribbler.AutoScanner;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import dev.amymialee.amymialib.modules.scribbler.auto.DossierData;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

/**
 * Fabric Data Generator with many fabric data providers pre-included for easy overriding.
 */
public class Dossier implements DataGeneratorEntrypoint, DossierLanyard {
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
        /* Extras */
        if (this.should(DossierExtras.class)) for (var task : this.scribbler.dossierData) if (task instanceof DossierExtras extra) extra.commitProviders(pack);
        if (this.should(DossierDynamic.class)) pack.addProvider((dataOutput, future) -> new DossierDynamic.Provider(this, dataOutput, future));
        /* Data */
        if (this.should(DossierAdvancements.class, DossierAdvancements.DossierCtxAdvancements.class)) pack.addProvider((dataOutput, future) -> new DossierAdvancements.Provider(this, dataOutput, future));
        if (this.should(DossierBlockLoot.class)) pack.addProvider((dataOutput, future) -> new DossierBlockLoot.Provider(this, dataOutput, future));
        if (this.should(DossierEntityLoot.class)) pack.addProvider((dataOutput, future) -> new DossierEntityLoot.Provider(this, dataOutput, future));
        if (this.should(DossierLoot.class)) pack.addProvider((dataOutput, future) -> new DossierLoot.Provider(this, dataOutput, future));
        if (this.should(DossierRecipes.class)) pack.addProvider((dataOutput, future) -> new DossierRecipes.Provider(this, dataOutput, future));
        if (this.should(DossierModels.DossierBlockModels.class) || this.should(DossierModels.DossierItemModels.class)) pack.addProvider((dataOutput, _) -> new DossierModels.Provider(this, dataOutput));
        if (this.should(DossierSounds.class)) pack.addProvider((dataOutput, future) -> new DossierSounds.Provider(this, dataOutput, future));
        if (this.should(DossierTags.class)) pack.addProvider((dataOutput, future) -> new DossierTags.Provider(this, dataOutput, future));
        /* Basically every mod needs translations */
        pack.addProvider((dataOutput, future) -> new DossierTranslations.Provider(this, dataOutput, future));
    }

    @Override public final void buildRegistry(@NonNull RegistrySetBuilder registryBuilder) {
        this.registryEach(p -> p.commitRegistries(registryBuilder), DossierRegistries.class);
    }

    /**
     * Run by each of the Dossier providers, allowing them to iterate on all the sources of data.
     */
    @ApiStatus.Internal @SafeVarargs
    public final @SuppressWarnings("unchecked") <T> void registryEach(Consumer<T> consumer, Class<? extends T> ... variants) {
        for (var c : variants) {
            this.scribbler.dossierData.forEach(t -> { if (c.isInstance(t)) consumer.accept((T) t); });
            AutoQueue.forAny(this.scribbler, p -> AutoScanner.findFunctionalInterfaces(scribbler.logger, DossierData.class, p, c).forEach(consumer));
        }
    }

    /**
     * Checks if any of the given Providers should run, by checking if they have anything to do if they did.
     */
    private boolean should(Class<?> ... clazz) {
        for (var c : clazz) {
            for (var o : this.scribbler.dossierData) if (c.isInstance(o)) return true;
            if (AutoQueue.findAny(this.scribbler, p -> AutoScanner.checkForFunctionalInterfaces(DossierData.class, p, c))) return true;
        }
        return false;
    }

    /* Name */
    @Override public String name() { return this.name; }
}