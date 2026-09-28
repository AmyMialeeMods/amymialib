package dev.amymialee.amymialib.modules.interfaces;

import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.interfaces.registerable.ScrawledRegisterable;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public interface ScrawledTagKey<T> {
    TagKey<T> amymialib$onScribble(Scribbler scribbler);

    default @Nullable Scribbler amy$scribbler() { throw new UnsupportedOperationException("Implemented via mixin."); }

    default TagKey<T> amy$translate(String translation) {
        return amy$translate((TagKey<T>) this, translation);
    }

    default TagKey<T> amy$addTo(TagKey<T> target) {
        return amy$addTo((TagKey<T>) this, target);
    }

    default TagKey<T> amy$with(Consumer<TagAppender<T>> consumer) {
        return amy$with((TagKey<T>) this, consumer);
    }

    default TagKey<T> amy$add(ResourceKey... targets) {
        return amy$add((TagKey<T>) this, targets);
    }

    default TagKey<T> amy$add(ScrawledRegisterable... targets) {
        return amy$add((TagKey<T>) this, targets);
    }

    static <T> TagKey<T> amy$translate(TagKey<T> tag, String translation) {
        Optional.ofNullable(tag.amy$scribbler()).ifPresent(s -> s.translation(tag.getTranslationKey(), translation));
        return tag;
    }

    static <T> TagKey<T> amy$addTo(TagKey<T> tag, TagKey<T> target) {
        Optional.ofNullable(tag.amy$scribbler()).ifPresent(s -> s.dossierData.add((DossierTags) (provider) -> provider.tag(target).addTag(tag)));
        return tag;
    }

    static <T> TagKey<T> amy$with(TagKey<T> tag, Consumer<TagAppender<T>> consumer) {
        Optional.ofNullable(tag.amy$scribbler()).ifPresent(s -> s.dossierData.add((DossierTags) (provider) -> consumer.accept(provider.tag(tag))));
        return tag;
    }

    static <T> TagKey<T> amy$add(TagKey<T> tag, ResourceKey... targets) {
        Optional.ofNullable(tag.amy$scribbler()).ifPresent(s -> s.dossierData.add((DossierTags) (provider) ->
                Arrays.stream(targets).forEach(k -> provider.tag(tag).add(k))));
        return tag;
    }

    static <T> TagKey<T> amy$add(TagKey<T> tag, ScrawledRegisterable... targets) {
        Optional.ofNullable(tag.amy$scribbler()).ifPresent(s -> s.dossierData.add((DossierTags) (provider) ->
                Arrays.stream(targets).map(ScrawledRegisterable::amy$key).filter(Objects::nonNull)
                        .forEach(k -> provider.tag(tag).add(k))));
        return tag;
    }
}