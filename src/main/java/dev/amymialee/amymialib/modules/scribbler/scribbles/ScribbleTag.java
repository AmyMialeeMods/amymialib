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
        this.scribbler.dossierData.add((DossierTags) (provider) -> provider.tag(target).addTag(this.tag));
        return this;
    }

    /**
     * Provides a tag appender for the current tag.
     */
    public ScribbleTag<T> with(Consumer<TagAppender<T>> consumer) {
        this.scribbler.dossierData.add((DossierTags) (provider) -> consumer.accept(provider.tag(this.tag)));
        return this;
    }

    /**
     * Adds an object to this tag.
     */
    @SafeVarargs
    public final ScribbleTag<T> add(ResourceKey<T>... targets) {
        this.scribbler.dossierData.add((DossierTags) (provider) -> {
            for (var target : targets) provider.tag(this.get()).add(target);
        });
        return this;
    }

    /**
     * Adds an object to this tag.
     */
    @SafeVarargs
    public final ScribbleTag<T> add(ScribbleKey<T>... targets) {
        this.scribbler.dossierData.add((DossierTags) (provider) -> {
            for (var target : targets) provider.tag(this.get()).add(target.get());
        });
        return this;
    }

    /**
     * Adds an object to this tag.
     */
    @SafeVarargs
    public final ScribbleTag<T> add(Scribble<T>... targets) {
        this.scribbler.dossierData.add((DossierTags) (provider) -> {
            for (var target : targets) provider.tag(this.get()).add(target.key());
        });
        return this;
    }

    public TagKey<T> get() {
        return this.tag;
    }
}