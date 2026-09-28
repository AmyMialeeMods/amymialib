package dev.amymialee.amymialib.modules.scribbler.scribbles;

import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.dossier.data.DossierTranslations;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public class ScribbleKey<T> {
    private final Scribbler scribbler;
    private final ResourceKey<T> key;

    @ApiStatus.Internal
    public ScribbleKey(Scribbler scribbler, ResourceKey<? extends Registry<T>> registryName, Identifier location) {
        this.scribbler = scribbler;
        this.key = ResourceKey.create(registryName, location);
    }

    @ApiStatus.Internal
    public ScribbleKey(Scribbler scribbler, ResourceKey<T> key) {
        this.scribbler = scribbler;
        this.key = key;
    }

    /**
     * Adds a translation to generate with datagen.
     * Automatically creates the translation key.
     */
    public ScribbleKey<T> translation(String translation) {
        this.scribbler.dossierData.add((DossierTranslations) (_, _, builder) -> builder.add(Util.makeDescriptionId(key.registry().toShortLanguageKey(), key.identifier()), translation));
        return this;
    }

    /**
     * Adds a translation to generate with datagen.
     */
    public ScribbleKey<T> translation(String key, String translation) {
        this.scribbler.translation(key, translation);
        return this;
    }

    /**
     * Creates a translation with every dye colour included as options.
     * @param key Translation key, including %s for dye colour
     * @param translation Translation, including %s for dye colour
     */
    public ScribbleKey<T> dyeTranslation(String key, String translation) {
        for (var dye : DyeColor.values()) this.translation(key.formatted(dye.getName()), translation.formatted(WordUtils.capitalize(dye.getName().replace('_', ' '))));
        return this;
    }

    /**
     * Adds this object to given tags.
     */
    public ScribbleKey<T> addTo(TagKey... targets) {
        this.scribbler.dossierData.add((DossierTags) (provider) -> {
            for (var target : targets) provider.tag(target).addOptional(this.key);
        });
        return this;
    }

    /**
     * Adds this object to given tags.
     */
    public ScribbleKey<T> addTo(List<TagKey> targets1, TagKey ... targets2) {
        this.scribbler.dossierData.add((DossierTags) (provider) -> {
            for (var target : targets1) provider.tag(target).addOptional(this.key);
            for (var target : targets2) provider.tag(target).addOptional(this.key);
        });
        return this;
    }

    public ResourceKey<T> get() {
        return this.key;
    }
}