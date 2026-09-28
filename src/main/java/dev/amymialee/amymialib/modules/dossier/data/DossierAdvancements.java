package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@FunctionalInterface
public interface DossierAdvancements extends DossierLanyard {
    void commitAdvancements(Provider provider, HolderLookup.Provider lookup, Consumer<AdvancementHolder> consumer);

    @FunctionalInterface
    interface DossierCtxAdvancements extends DossierAdvancements {
        @Override
        default void commitAdvancements(Provider provider, HolderLookup.Provider lookup, Consumer<AdvancementHolder> consumer) {
            commitAdvancements(provider);
        }

        void commitAdvancements(Provider ctx);
    }

    @Override default String name() { return "Advancements"; }

    final class Provider extends FabricAdvancementProvider {
        private final Dossier dossier;
        private HolderLookup.Provider lookup;
        private Consumer<AdvancementHolder> consumer;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dossier = dossier;
        }

        @Override
        public void generateAdvancement(HolderLookup.Provider lookup, Consumer<AdvancementHolder> consumer) {
            this.lookup = lookup;
            this.consumer = consumer;
            this.dossier.registryEach(d -> d.commitAdvancements(this, lookup, consumer), DossierAdvancements.class);
            this.dossier.registryEach(d -> d.commitAdvancements(this), DossierCtxAdvancements.class);
        }

        public AdvancementHolder create(AdvancementHolder holder) {
            this.consumer.accept(holder);
            return holder;
        }

        public HolderLookup.Provider lookup() {
            return lookup;
        }

        public Consumer<AdvancementHolder> consumer() {
            return consumer;
        }

        @Override
        public String getName() {
            return this.dossier.name() + " " + super.getName();
        }
    }
}