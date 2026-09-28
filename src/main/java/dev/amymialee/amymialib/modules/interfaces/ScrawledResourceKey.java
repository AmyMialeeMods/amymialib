package dev.amymialee.amymialib.modules.interfaces;

import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("unchecked")
public interface ScrawledResourceKey<T> {
    ResourceKey<T> amymialib$onScribble(Scribbler scribbler);

    default @Nullable Scribbler amy$scribbler() { throw new UnsupportedOperationException("Implemented via mixin."); }

    default ResourceKey<T> amy$translate(String translation) { throw new UnsupportedOperationException("Implemented via mixin."); }

    default ResourceKey<T> amy$addTo(TagKey... targets) {
        var self = (ResourceKey<T>) this;
        Optional.ofNullable(this.amy$scribbler()).ifPresent(s ->
                s.dossierData.add((DossierTags) (provider) -> {
                    for (var target : targets) provider.tag(target).add(self);
                })
        );
        return self;
    }
}