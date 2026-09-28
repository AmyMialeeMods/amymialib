package dev.amymialee.amymialib.modules.scribbler.scribbles;

import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.dossier.data.DossierTranslations;
import dev.amymialee.amymialib.modules.scribbler.AutoQueue;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.TypedInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class Scribble<T> implements AutoScribbler, TypedInstance<T> {
    public final Scribbler scribbler;
    protected final Registry<T> registry;
    protected final @Nullable Supplier<Holder.Reference<T>> getter;
    protected @Nullable Holder.Reference<T> holder;

    @ApiStatus.Internal
    public Scribble(Scribbler scribbler, Registry<T> registry, @Nullable Supplier<Holder.Reference<T>> getter) {
        this.scribbler = scribbler;
        this.registry = registry;
        this.getter = getter;
        AutoQueue.addScribble(this);
    }

    @ApiStatus.Internal @SuppressWarnings({"unchecked", "DataFlowIssue", "OptionalGetWithoutIsPresent"})
    public static <T> Scribble<T> fake(Scribbler scribbler, ResourceKey<T> key) {
        var registry = (Registry<T>) BuiltInRegistries.REGISTRY.getValue(key.registry());
        var scribble = new Scribble<>(scribbler, registry, null);
        scribble.holder = registry.get(key.identifier()).get();
        return scribble;
    }

    /**
     * Adds a translation to generate with datagen.
     * Automatically creates the translation key.
     */
    public Scribble<T> translation(String translation) {
        if (this.getter != null) this.scribbler.dossierData.add((DossierTranslations) (_, _, builder) -> {
            var key = Scribble.this.key();
            builder.add(Util.makeDescriptionId(key.registry().toShortLanguageKey(), key.identifier()), translation);
        });
        return this;
    }

    /**
     * Adds a translation to generate with datagen.
     */
    public Scribble<T> translation(String key, String translation) {
        this.scribbler.translation(key, translation);
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

    /**
     * Adds this object to given tags.
     */
    public Scribble<T> addTo(TagKey... targets) {
        this.scribbler.dossierData.add((DossierTags) (provider) -> {
            for (var target : targets) provider.tag(target).add(this.key());
        });
        return this;
    }

    /**
     * Adds this object to given tags.
     */
    public Scribble<T> addTo(List<TagKey> targets1, TagKey ... targets2) {
        this.scribbler.dossierData.add((DossierTags) (provider) -> {
            for (var target : targets1) provider.tag(target).add(this.key());
            for (var target : targets2) provider.tag(target).add(this.key());
        });
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

    @Override
    public Holder<T> typeHolder() {
        return this.holder();
    }

    @Override
    public String debugName() {
        return this.key().toString();
    }
}