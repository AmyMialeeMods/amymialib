package dev.amymialee.amymialib.modules.scribbler.scribbles;

import dev.amymialee.amymialib.modules.dossier.DossierRegistries;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ScribbleMusicDisc extends Scribble<Item> implements DossierRegistries {
    public final ScribbleKey<JukeboxSong> songKey;
    public final Holder.Reference<SoundEvent> song;
    public final int lengthInSeconds;
    public final int comparatorOutput;

    public ScribbleMusicDisc(Scribbler scribbler, String path, Holder.Reference<SoundEvent> song, int lengthInSeconds, int comparatorOutput, @Nullable Supplier<Holder.Reference<Item>> getter) {
        super(scribbler, BuiltInRegistries.ITEM, getter);
        songKey = scribbler.key(Registries.JUKEBOX_SONG, path);
        this.song = song;
        this.lengthInSeconds = lengthInSeconds;
        this.comparatorOutput = comparatorOutput;
        scribbler.dossierData.add(this);
    }

    @Override
    public ScribbleMusicDisc translation(String name) {
        songKey.translation(name);
        return this;
    }

    @Override
    public void commitRegistries(RegistrySetBuilder registryBuilder) {
        registryBuilder.add(Registries.JUKEBOX_SONG, registerable -> registerable.register(songKey.get(), new JukeboxSong(song, Component.translatable(Util.makeDescriptionId("jukebox_song", songKey.get().identifier())), lengthInSeconds, comparatorOutput)));
    }

    /**
     * Adds this object to given tags.
     */
    @Override
    public ScribbleMusicDisc addTo(TagKey... targets) {
        return (ScribbleMusicDisc) super.addTo(targets);
    }

    /**
     * Adds this object to given tags.
     */
    @Override
    public ScribbleMusicDisc addTo(List<TagKey> targets1, TagKey ... targets2) {
        return (ScribbleMusicDisc) super.addTo(targets1, targets2);
    }
}