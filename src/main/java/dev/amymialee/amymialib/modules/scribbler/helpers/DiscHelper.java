package dev.amymialee.amymialib.modules.scribbler.helpers;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;

public record DiscHelper(Item item, ResourceKey<JukeboxSong> key, Holder.Reference<SoundEvent> song, int lengthInSeconds, int comparatorOutput) {}