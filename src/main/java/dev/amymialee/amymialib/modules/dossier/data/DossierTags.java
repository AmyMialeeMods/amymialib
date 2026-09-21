package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierTags extends DossierLanyard {
    void commitTags(Provider provider);

    @Override default String name() { return "Tags"; }

    final class Provider implements DataProvider {
        private final Dossier dossier;
        public final FabricPackOutput output;
        public final CompletableFuture<HolderLookup.Provider> registriesFuture;
        private @SuppressWarnings("rawtypes") final Map<ResourceKey, DossierTagProvider> tagProviders = new HashMap<>();

        public @SuppressWarnings("unchecked") <T> TagAppender<T> builder(TagKey<T> tag) {
            return this.tagProviders.computeIfAbsent(tag.registry(), DossierTagProvider::new).builder(tag);
        }

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            this.dossier = dossier;
            this.output = output;
            this.registriesFuture = registriesFuture;
        }

        @Override
        public CompletableFuture<?> run(CachedOutput cache) {
            return this.registriesFuture.thenApply(registries -> {
                this.dossier.registryEach(DossierTags.class, p -> p.commitTags(this));
                for (var entry : this.tagProviders.entrySet()) entry.getValue().run(cache).join();
                this.dossier.registryEach(DossierTags.class, p -> p.commitTags(this));
                return registries;
            });
        }

        @Override
        public String getName() {
            return this.dossier.name() + " Tags";
        }

        public class DossierTagProvider<T> extends FabricTagsProvider<T> {
            public DossierTagProvider(ResourceKey<? extends Registry<T>> registryKey) {
                super(Provider.this.output, registryKey, Provider.this.registriesFuture);
            }

            @Override
            protected void addTags(HolderLookup.Provider registries) {}

            @Override
            protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
                return Provider.this.registriesFuture.thenApply(registries -> registries);
            }

            @Override
            public TagAppender<T> builder(TagKey<T> tag) {
                return super.builder(tag);
            }
        }
    }
}