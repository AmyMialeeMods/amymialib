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

    @Override default String name() { return "Loot"; }

    final class Provider extends SimpleFabricLootTableSubProvider {
        private final Dossier dossier;
        public final FabricPackOutput output;
        public final CompletableFuture<HolderLookup.Provider> registriesFuture;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture, LootTable.DEFAULT_PARAM_SET);
            this.dossier = dossier;
            this.output = output;
            this.registriesFuture = registriesFuture;
        }

        @Override
        public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
            this.dossier.registryEach(DossierLoot.class, p -> p.commitLoot(this, consumer));
        }

        @Override
        public String getName() {
            return this.dossier.name() + " " + super.getName();
        }

        @Override
        public void run() {}
    }
}
