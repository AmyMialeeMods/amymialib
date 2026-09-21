package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierDynamic extends DossierLanyard {
    void commitDynamic(Provider provider, HolderLookup.Provider wrapperLookup, FabricDynamicRegistryProvider.Entries entries);

    @ApiStatus.Internal
    class Provider extends FabricDynamicRegistryProvider {
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
        protected void configure(HolderLookup.Provider wrapperLookup, Entries entries) {
            this.dossier.registryEach(DossierDynamic.class, p -> p.commitDynamic(this, wrapperLookup, entries));
        }

        public <T> void add(Entries entries, ResourceKey<Registry<T>> key) {
            entries.addAll((HolderLookup.RegistryLookup<T>) entries.getLookup(key));
        }

        @Override
        public String getName() {
            return this.dossier.name() + " Dynamic";
        }
    }
}