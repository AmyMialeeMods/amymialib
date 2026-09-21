package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierDynamic extends DossierLanyard {
    void commitDynamic(Provider provider, HolderLookup.Provider lookup, FabricDynamicRegistryProvider.Entries entries);

    @Override default String name() { return "Dynamic"; }

    final class Provider extends FabricDynamicRegistryProvider {
        private static final HashSet<ResourceKey<?>> registered = new HashSet<>();
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
        protected void configure(HolderLookup.Provider lookup, Entries entries) {
            this.dossier.registryEach(DossierDynamic.class, p -> p.commitDynamic(this, lookup, entries));
        }

        public <T> void add(Entries entries, ResourceKey<Registry<T>> key) {
            var registry = (HolderLookup.RegistryLookup<T>) entries.getLookup(key);
            registry.listElementIds()
                    .filter(resourceKey -> resourceKey.identifier().getNamespace().equals(output.getModId()))
                    .filter(resourceKey -> !registered.contains(resourceKey))
                    .forEach(k -> {
                        entries.add(registry, k);
                        registered.add(k);
                    }
            );
        }

        @Override
        public String getName() {
            return this.dossier.name() + " Dynamic";
        }
    }
}