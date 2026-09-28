package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricEntityLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierEntityLoot extends DossierLanyard {
    void commitEntityLoot(Provider provider);

    @Override default String name() { return "Entity Loot"; }

    final class Provider extends FabricEntityLootSubProvider {
        private final Dossier dossier;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dossier = dossier;
        }

        @Override
        public void generate() {
            this.dossier.registryEach(p -> p.commitEntityLoot(this), DossierEntityLoot.class);
        }

        @Override
        public String getName() {
            return this.dossier.name() + " " + super.getName();
        }
    }
}