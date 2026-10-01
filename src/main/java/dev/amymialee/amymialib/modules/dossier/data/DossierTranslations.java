package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import dev.amymialee.amymialib.modules.scribbler.AutoQueue;
import dev.amymialee.amymialib.modules.scribbler.AutoScanner;
import dev.amymialee.amymialib.modules.scribbler.auto.Decipher;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.AccessFlag;
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
        public void generateTranslations(HolderLookup.@NonNull Provider lookup, @NonNull TranslationBuilder builder) {
            AutoQueue.forEach(dossier.scribbler, this::runTranslate);
            this.lookup = lookup;
            this.builder = builder;
            this.dossier.registryEach(p -> p.commitTranslations(this, lookup, builder), DossierTranslations.class);
            this.dossier.registryEach(p -> p.commitTranslations(this), DossierCtxTranslations.class);
            this.dossier.registryEach(p -> p.commitTranslations(builder), DossierBuilderTranslations.class);
        }

        private void runTranslate(String packageName) {
            AutoScanner.findAnnotations(packageName).getFieldsAnnotatedWith(Decipher.class).forEach(f -> {
                try {
                    for (var decipher : f.getAnnotationsByType(Decipher.class)) dossier.scribbler.translate(f, decipher);
                } catch (Throwable t) {
                    var reason = "";
                    if (!f.accessFlags().contains(AccessFlag.STATIC)) {
                        reason = "Unable to translate field %s.%s, as it needs to be static.".formatted(f.getDeclaringClass().getName(), f.getName());
                    } else if (t instanceof RuntimeException runtimeException) {
                        throw runtimeException;
                    } else {
                        dossier.scribbler.logger.error("Unable to translate field {}.{}, due to an unknown reason.", f.getDeclaringClass().getName(), f.getName());
                        throw new RuntimeException(t);
                    }
                    dossier.scribbler.logger.error(reason);
                    throw new RuntimeException(reason, null, true, false) {};
                }
            });
        }

        public HolderLookup.Provider lookup() {
            return lookup;
        }

        public TranslationBuilder builder() {
            return builder;
        }

        @Override
        public @NonNull String getName() {
            return this.dossier.name() + " " + super.getName();
        }
    }
}