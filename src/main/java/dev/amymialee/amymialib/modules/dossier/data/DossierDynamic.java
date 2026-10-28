package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierDynamic extends DossierLanyard {
    void commitDynamic(Provider provider, FabricDynamicRegistryProvider.Entries entries);

    @Override default String name() { return "Dynamic"; }

    final class Provider extends FabricDynamicRegistryProvider {
        private static final HashSet<ResourceKey<?>> registered = new HashSet<>();
        private final Dossier dossier;
        private final FabricPackOutput output;
        private HolderLookup.Provider lookup;
        private Entries entries;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output, registriesFuture);
            this.dossier = dossier;
            this.output = output;
        }

        @Override
        protected void configure(HolderLookup.@NonNull Provider lookup, @NonNull Entries entries) {
            this.lookup = lookup;
            this.entries = entries;
            this.dossier.registryEach(p -> p.commitDynamic(this, entries), DossierDynamic.class);
        }

        public <T> void add(ResourceKey<Registry<T>> key) {
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

        public HolderLookup.Provider lookup() {
            return lookup;
        }

        public Entries entries() {
            return entries;
        }

        @Override
        public @NonNull String getName() {
            return this.dossier.name() + " Dynamic";
        }
    }
}