package dev.amymialee.amymialib.modules.scribbler;

import dev.amymialee.amymialib.modules.dossier.data.DossierTranslations;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;

public record DiscHelper(Scribbler scribbler, Item discItem, ResourceKey<JukeboxSong> songKey, Holder.Reference<SoundEvent> song, int lengthInSeconds, int comparatorOutput) {
    public DiscHelper amy$translate(String translation) {
        this.scribbler.dossierData.add((DossierTranslations) (_, _, builder) -> builder.add(Util.makeDescriptionId(songKey.registry().toShortLanguageKey(), songKey.identifier()), translation));
        return this;
    }
}