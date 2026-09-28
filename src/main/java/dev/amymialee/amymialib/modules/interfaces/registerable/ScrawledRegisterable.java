package dev.amymialee.amymialib.modules.interfaces.registerable;

import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.dossier.data.DossierTranslations;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import org.apache.commons.text.WordUtils;

import java.util.Optional;

@SuppressWarnings("unchecked")
public interface ScrawledRegisterable<T> {
    void amymialib$onScribble(Scribbler scribbler);

    default Scribbler amy$scribbler() { throw new UnsupportedOperationException("Implemented via mixin."); }

    void amymialib$onRegister(Holder.Reference<T> result);

    default Holder.Reference<T> amy$holder() { throw new UnsupportedOperationException("Implemented via mixin."); }

    default ResourceKey<T> amy$key() { return this.amy$holder().key(); }

    default T amy$translate(String translation) {
        Optional.ofNullable(this.amy$holder()).ifPresent(h -> {
            var key = h.key();
            this.amy$translate(Util.makeDescriptionId(key.registry().toShortLanguageKey(), key.identifier()), translation);
        });
        return (T) this;
    }

    default T amy$translate(String key, String translation) {
        Optional.ofNullable(this.amy$scribbler()).ifPresent(s -> s.dossierData.add((DossierTranslations) (_, _, builder) -> builder.add(key, translation)));
        return (T) this;
    }

    default T amy$dyeTranslate(String key, String translation) {
        for (var dye : DyeColor.values()) this.amy$translate(key.formatted(dye.getName()), translation.formatted(WordUtils.capitalize(dye.getName().replace('_', ' '))));
        return (T) this;
    }

    default T amy$addTo(TagKey... targets) {
        Optional.ofNullable(this.amy$scribbler()).ifPresent(s -> Optional.ofNullable(this.amy$holder()).ifPresent(h ->
            s.dossierData.add((DossierTags) (provider) -> {
                for (var target : targets) provider.tag(target).add(this.amy$key());
            })
        ));
        return (T) this;
    }
}