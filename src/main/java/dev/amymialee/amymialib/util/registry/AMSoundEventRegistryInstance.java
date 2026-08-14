package dev.amymialee.amymialib.util.registry;

import dev.amymialee.amymialib.util.AMRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Util;

public class AMSoundEventRegistryInstance extends AMRegistryInstance<SoundEvent> {
    public AMSoundEventRegistryInstance(AMRegistry amRegistry, ResourceKey<SoundEvent> key, SoundEvent value) {
        super(amRegistry, BuiltInRegistries.SOUND_EVENT, key, value);
    }

    @Override
    public AMSoundEventRegistryInstance translation(String translation) {
        super.translation(Util.makeDescriptionId("subtitles", key.identifier()), translation);
        return this;
    }
}