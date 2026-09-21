package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

@FunctionalInterface
public interface DossierExtras extends DossierLanyard {
    void commitProviders(FabricDataGenerator.Pack pack);
}