package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@FunctionalInterface
public interface DossierLoot extends DossierLanyard {
    void commitLoot(Provider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer);

    @FunctionalInterface
    interface DossierCtxLoot extends DossierLoot {
        @Override
        default void commitLoot(Provider provider, BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            commitLoot(provider);
        }

        void commitLoot(Provider ctx);
    }

    @Override default String name() { return "Loot"; }

    final class Provider extends SimpleFabricLootTableSubProvider {
        private final Dossier dossier;
        private BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture, LootTable.DEFAULT_PARAM_SET);
            this.dossier = dossier;
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            this.consumer = consumer;
            this.dossier.registryEach(p -> p.commitLoot(this, consumer), DossierLoot.class);
            this.dossier.registryEach(p -> p.commitLoot(this), DossierCtxLoot.class);
        }

        public BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer() {
            return consumer;
        }

        @Override
        public String getName() {
            return this.dossier.name() + " " + super.getName();
        }

        @Override
        public void run() {}
    }
}