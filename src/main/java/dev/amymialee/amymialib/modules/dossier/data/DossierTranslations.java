package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierTranslations extends DossierLanyard {
    void commitTranslations(Provider provider, HolderLookup.Provider lookup, FabricLanguageProvider.TranslationBuilder builder);

    @Override default String name() { return "Translations"; }

    final class Provider extends FabricLanguageProvider {
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
        public void generateTranslations(HolderLookup.Provider lookup, TranslationBuilder builder) {
            this.dossier.registryEach(DossierTranslations.class, p -> p.commitTranslations(this, lookup, builder));
        }

        @Override
        public String getName() {
            return this.dossier.name() + " " + super.getName();
        }
    }
}