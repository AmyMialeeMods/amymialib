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

    @FunctionalInterface
    interface DossierCtxTranslations extends DossierTranslations {
        @Override
        default void commitTranslations(DossierTranslations.Provider provider, HolderLookup.Provider lookup, FabricLanguageProvider.TranslationBuilder builder) {
            commitTranslations(provider);
        }

        void commitTranslations(Provider ctx);
    }

    @FunctionalInterface
    interface DossierBuilderTranslations extends DossierTranslations {
        @Override
        default void commitTranslations(DossierTranslations.Provider provider, HolderLookup.Provider lookup, FabricLanguageProvider.TranslationBuilder builder) {
            commitTranslations(builder);
        }

        void commitTranslations(FabricLanguageProvider.TranslationBuilder builder);
    }

    @Override default String name() { return "Translations"; }

    final class Provider extends FabricLanguageProvider {
        private final Dossier dossier;
        private HolderLookup.Provider lookup;
        private TranslationBuilder builder;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dossier = dossier;
        }

        @Override
        public void generateTranslations(HolderLookup.Provider lookup, TranslationBuilder builder) {
            this.lookup = lookup;
            this.builder = builder;
            this.dossier.registryEach(p -> p.commitTranslations(this, lookup, builder), DossierTranslations.class);
            this.dossier.registryEach(p -> p.commitTranslations(this), DossierCtxTranslations.class);
            this.dossier.registryEach(p -> p.commitTranslations(builder), DossierBuilderTranslations.class);
        }

        public HolderLookup.Provider lookup() {
            return lookup;
        }

        public TranslationBuilder builder() {
            return builder;
        }

        @Override
        public String getName() {
            return this.dossier.name() + " " + super.getName();
        }
    }
}