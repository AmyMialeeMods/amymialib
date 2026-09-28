package dev.amymialee.amymialib.modules.interfaces.mixin.registerable;

import dev.amymialee.amymialib.modules.interfaces.registerable.ScrawledRegisterable;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(SoundEvent.class)
public abstract class SoundEventMixin implements ScrawledRegisterable<SoundEvent> {
    @Unique private @Nullable Scribbler scribbler;
    @Unique private @Nullable Holder.Reference<SoundEvent> holder;

    @Override
    public void amymialib$onScribble(Scribbler scribbler) {
        this.scribbler = scribbler;
    }

    @Override
    public void amymialib$onRegister(Holder.Reference<SoundEvent> holder) {
        this.holder = holder;
    }

    @Override
    public @Nullable Scribbler amy$scribbler() {
        return this.scribbler;
    }

    @Override
    public @Nullable Holder.Reference<SoundEvent> amy$holder() {
        return this.holder;
    }

    @Override
    public ResourceKey<SoundEvent> amy$key() {
        return ScrawledRegisterable.super.amy$key();
    }

    @Override
    public SoundEvent amy$translate(String translation) {
        return ScrawledRegisterable.super.amy$translate(translation);
    }

    @Override
    public SoundEvent amy$translate(String key, String translation) {
        return ScrawledRegisterable.super.amy$translate(key, translation);
    }

    @Override
    public SoundEvent amy$dyeTranslate(String key, String translation) {
        return ScrawledRegisterable.super.amy$dyeTranslate(key, translation);
    }

    @Override
    public SoundEvent amy$addTo(TagKey... targets) {
        return ScrawledRegisterable.super.amy$addTo(targets);
    }
}