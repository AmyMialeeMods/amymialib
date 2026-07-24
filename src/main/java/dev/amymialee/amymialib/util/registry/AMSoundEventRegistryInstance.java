package dev.amymialee.amymialib.util.registry;

import com.ibm.icu.impl.Pair;
import dev.amymialee.amymialib.util.AMRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class AMSoundEventRegistryInstance extends AMRegistryInstance<SoundEvent> {
    public AMSoundEventRegistryInstance(AMRegistry amRegistry, ResourceKey<SoundEvent> key, SoundEvent value) {
        super(amRegistry, BuiltInRegistries.SOUND_EVENT, key, value);
    }

    @Override
    public AMRegistryInstance<SoundEvent> translation(String translation) {
        this.translation = Pair.of(Util.makeDescriptionId("subtitles", key.identifier()), translation);
        return this;
    }
}