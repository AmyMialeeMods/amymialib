package dev.amymialee.amymialib.modules.interfaces.registerable;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;

public interface ScrawledSoundEvent extends ScrawledRegisterable<SoundEvent> {
    @Override
    void amymialib$onRegister(Holder.Reference<SoundEvent> result);

    @Override
    default Holder.Reference<SoundEvent> amy$holder() {
        return ScrawledRegisterable.super.amy$holder();
    }

    @Override
    default ResourceKey<SoundEvent> amy$key() {
        return ScrawledRegisterable.super.amy$key();
    }

    @Override
    default SoundEvent amy$translate(String translation) {
        return ScrawledRegisterable.super.amy$translate(translation);
    }

    @Override
    default SoundEvent amy$translate(String key, String translation) {
        return ScrawledRegisterable.super.amy$translate(key, translation);
    }

    @Override
    default SoundEvent amy$dyeTranslate(String key, String translation) {
        return ScrawledRegisterable.super.amy$dyeTranslate(key, translation);
    }

    @Override
    default SoundEvent amy$addTo(TagKey... targets) {
        return ScrawledRegisterable.super.amy$addTo(targets);
    }
}