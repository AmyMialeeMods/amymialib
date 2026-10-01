package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierSounds extends DossierLanyard {
    void commitSounds(Provider provider, HolderLookup.Provider lookup, FabricSoundsProvider.SoundExporter exporter);

    @FunctionalInterface
    interface DossierCtxSounds extends DossierSounds {
        @Override
        default void commitSounds(Provider provider, HolderLookup.Provider lookup, FabricSoundsProvider.SoundExporter exporter) {
            commitSounds(provider);
        }

        void commitSounds(Provider ctx);
    }

    @Override default String name() { return "Sounds"; }

    final class Provider extends FabricSoundsProvider {
        private final Dossier dossier;
        private HolderLookup.Provider lookup;
        private SoundExporter exporter;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dossier = dossier;
        }

        @Override
        protected void configure(HolderLookup.@NonNull Provider lookup, @NonNull SoundExporter exporter) {
            this.lookup = lookup;
            this.exporter = exporter;
            this.dossier.registryEach(p -> p.commitSounds(this, lookup, exporter), DossierSounds.class);
        }

        public HolderLookup.Provider lookup() {
            return lookup;
        }

        public SoundExporter exporter() {
            return exporter;
        }

        @Override
        public @NonNull String getName() {
            return this.dossier.name() + " Sounds";
        }
    }
}