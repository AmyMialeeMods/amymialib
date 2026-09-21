package dev.amymialee.amymialib.modules.scribbler.scribbles;

import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Registry;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

public class ScribbleTag<T> {
    private final Scribbler scribbler;
    private final TagKey<T> tag;

    @ApiStatus.Internal
    public ScribbleTag(Scribbler scribbler, ResourceKey<Registry<T>> registry, String path) {
        this.scribbler = scribbler;
        this.tag = TagKey.create(registry, scribbler.id(path));
        this.scribbler.dossierData.add(this);
    }

    /**
     * Adds a translation to generate with datagen.
     * Automatically creates the translation key.
     */
    public ScribbleTag<T> translation(String translation) {
        this.scribbler.translation(Util.makeDescriptionId("tag.%s".formatted(tag.registry().identifier().getPath()), tag.location()), translation);
        return this;
    }

    /**
     * Adds a translation to generate with datagen.
     */
    public ScribbleTag<T> translation(String key, String translation) {
        this.scribbler.translation(key, translation);
        return this;
    }

    /**
     * Adds this tag to another target tag.
     */
    public ScribbleTag<T> addTo(TagKey<T> target) {
        this.scribbler.dossierData.add((DossierTags) (provider) -> provider.builder(target).addTag(this.tag));
        return this;
    }

    /**
     * Provides a tag appender for the current tag.
     */
    public ScribbleTag<T> with(Consumer<TagAppender<T>> consumer) {
        this.scribbler.dossierData.add((DossierTags) (provider) -> consumer.accept(provider.builder(this.tag)));
        return this;
    }
}