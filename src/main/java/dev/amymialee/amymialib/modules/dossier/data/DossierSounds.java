package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierSounds extends DossierLanyard {
    void commitSounds(Provider provider, HolderLookup.Provider lookup, FabricSoundsProvider.SoundExporter exporter);

    @Override default String name() { return "Sounds"; }

    final class Provider extends FabricSoundsProvider {
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
        protected void configure(HolderLookup.Provider lookup, SoundExporter exporter) {
            this.dossier.registryEach(DossierSounds.class, p -> p.commitSounds(this, lookup, exporter));
        }

        @Override
        public String getName() {
            return this.dossier.name() + " Sounds";
        }
    }
}