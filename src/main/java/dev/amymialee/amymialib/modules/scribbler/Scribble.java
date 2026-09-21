package dev.amymialee.amymialib.modules.scribbler;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.modules.dossier.data.DossierTranslations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Scribble<T> {
    protected final Scribbler registry;
    private final Supplier<Holder.Reference<T>> getter;
    private @Nullable Holder.Reference<T> holder;

    @ApiStatus.Internal
    public Scribble(Scribbler registry, Supplier<Holder.Reference<T>> getter) {
        this.registry = registry;
        this.getter = getter;
        AmyMialib.SCRIBBLES.add(this);
    }

    /**
     * Adds a translation to generate with datagen.
     * Automatically creates the translation key.
     */
    public Scribble<T> translation(String translation) {
        this.registry.dossierData.add((DossierTranslations) (_, _, builder) -> {
            var key = Scribble.this.key();
            builder.add(Util.makeDescriptionId(key.registry().toLanguageKey(), key.identifier()), translation);
        });
        return this;
    }

    /**
     * Adds a translation to generate with datagen.
     */
    public Scribble<T> translation(String key, String translation) {
        this.registry.translation(key, translation);
        return this;
    }

    /**
     * Creates a translation with every dye colour included as options.
     * @param key Translation key, including %s for dye colour
     * @param translation Translation, including %s for dye colour
     */
    public Scribble<T> dyeTranslation(String key, String translation) {
        for (var dye : DyeColor.values()) this.translation(key.formatted(dye.getName()), translation.formatted(WordUtils.capitalize(dye.getName().replace('_', ' '))));
        return this;
    }

    @ApiStatus.Internal
    public void run() {
        this.holder = this.getter.get();
    }

    public Holder.Reference<T> holder() {
        if (this.holder == null) this.run();
        return this.holder;
    }

    public ResourceKey<T> key() {
        return this.holder().key();
    }

    public T get() {
        return this.holder().value();
    }
}