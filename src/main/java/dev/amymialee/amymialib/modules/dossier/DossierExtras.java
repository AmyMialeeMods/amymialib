package dev.amymialee.amymialib.modules.dossier;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

@FunctionalInterface
public interface DossierExtras extends DossierLanyard {
    void commitProviders(FabricDataGenerator.Pack pack);

    @Override default String name() { return "Extras"; }
}