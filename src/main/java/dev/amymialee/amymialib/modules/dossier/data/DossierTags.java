package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaggable;
import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaglike;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface DossierTags extends DossierLanyard {
    void commitTags(Provider provider);

    @Override default String name() { return "Tags"; }

    @SuppressWarnings("unchecked")
    final class Provider implements DataProvider {
        private final Dossier dossier;
        public final FabricPackOutput output;
        public final CompletableFuture<HolderLookup.Provider> registriesFuture;
        private final Map<ResourceKey, DossierTagProvider> tagProviders = new HashMap<>();

        public void tag(AmyMialibTaggable object, AmyMialibTaglike... tags) {
            var success = this.tag(Arrays.asList(tags), List.of(object));
            if (!success) dossier.scribbler.logger.error("Failed to tag object {} with tags {}", object, Arrays.toString(tags));
        }

        public void tag(AmyMialibTaglike tag, AmyMialibTaggable... objects) {
            var success = this.tag(List.of(tag), Arrays.asList(objects));
            if (!success) dossier.scribbler.logger.error("Failed to tag objects {} with tag {}", Arrays.toString(objects), tag);
        }

        public boolean tag(List<AmyMialibTaglike> taglikes, List<AmyMialibTaggable> objects) {
            var targetTags = taglikes.stream().map(AmyMialibTaggable::amymialib$tags).flatMap(List::stream).toList();
            var keys = objects.stream().map(AmyMialibTaggable::amymialib$keys).flatMap(List::stream).toList();
            var tags = objects.stream().map(AmyMialibTaggable::amymialib$tags).flatMap(List::stream).toList();
            var anyAdded = false;
            for (var targetTag : targetTags) {
                var targetRegistry = targetTag.registry();
                for (var key : keys) if (targetRegistry == key.registryKey()) {
                    tag(targetTag).add(key);
                    anyAdded = true;
                }
                for (var tag : tags) if (targetRegistry == tag.registry()) {
                    tag(targetTag).addTag(tag);
                    anyAdded = true;
                }
            }
            return anyAdded;
        }

        public TagAppender tag(TagKey tag) {
            return this.tagProviders.computeIfAbsent(tag.registry(), DossierTagProvider::new).builder(tag);
        }

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            this.dossier = dossier;
            this.output = output;
            this.registriesFuture = registriesFuture;
        }

        @Override
        public @NonNull CompletableFuture<?> run(@NonNull CachedOutput cache) {
            return this.registriesFuture.thenApply(registries -> {
                this.dossier.registryEach(p -> p.commitTags(this), DossierTags.class);
                for (var entry : this.tagProviders.entrySet()) entry.getValue().run(cache).join();
                this.dossier.registryEach(p -> p.commitTags(this), DossierTags.class);
                return registries;
            });
        }

        @Override
        public @NonNull String getName() {
            return this.dossier.name() + " Tags";
        }

        public class DossierTagProvider<T> extends FabricTagsProvider<T> {
            public DossierTagProvider(ResourceKey<? extends Registry<T>> registryKey) {
                super(Provider.this.output, registryKey, Provider.this.registriesFuture);
            }

            @Override
            protected void addTags(HolderLookup.@NonNull Provider registries) {}

            @Override
            protected @NonNull CompletableFuture<HolderLookup.Provider> createContentsProvider() {
                return Provider.this.registriesFuture.thenApply(registries -> registries);
            }

            @Override
            public @NonNull TagAppender<T> builder(@NonNull TagKey<T> tag) {
                return super.builder(tag);
            }
        }
    }
}