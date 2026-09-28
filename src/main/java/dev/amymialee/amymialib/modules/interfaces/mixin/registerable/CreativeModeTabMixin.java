package dev.amymialee.amymialib.modules.interfaces.mixin.registerable;

import dev.amymialee.amymialib.modules.interfaces.registerable.ScrawledRegisterable;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;

@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin implements ScrawledRegisterable<CreativeModeTab> {
    @Unique private @Nullable Scribbler scribbler;
    @Unique private @Nullable Holder.Reference<CreativeModeTab> holder;

    @Override
    public void amymialib$onScribble(Scribbler scribbler) {
        this.scribbler = scribbler;
    }

    @Override
    public void amymialib$onRegister(Holder.Reference<CreativeModeTab> holder) {
        this.holder = holder;
    }

    @Override
    public @Nullable Scribbler amy$scribbler() {
        return this.scribbler;
    }

    @Override
    public @Nullable Holder.Reference<CreativeModeTab> amy$holder() {
        return this.holder;
    }

    @Override
    public ResourceKey<CreativeModeTab> amy$key() {
        return ScrawledRegisterable.super.amy$key();
    }

    @Override
    public CreativeModeTab amy$translate(String translation) {
        return ScrawledRegisterable.super.amy$translate(translation);
    }

    @Override
    public CreativeModeTab amy$translate(String key, String translation) {
        return ScrawledRegisterable.super.amy$translate(key, translation);
    }

    @Override
    public CreativeModeTab amy$dyeTranslate(String key, String translation) {
        return ScrawledRegisterable.super.amy$dyeTranslate(key, translation);
    }

    @Override
    public CreativeModeTab amy$addTo(TagKey... targets) {
        return ScrawledRegisterable.super.amy$addTo(targets);
    }
}