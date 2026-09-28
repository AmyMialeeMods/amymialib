package dev.amymialee.amymialib.modules.interfaces.registerable;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;

public interface ScrawledCreativeTab extends ScrawledRegisterable<CreativeModeTab> {
    @Override
    void amymialib$onRegister(Holder.Reference<CreativeModeTab> result);

    @Override
    default Holder.Reference<CreativeModeTab> amy$holder() {
        return ScrawledRegisterable.super.amy$holder();
    }

    @Override
    default ResourceKey<CreativeModeTab> amy$key() {
        return ScrawledRegisterable.super.amy$key();
    }

    @Override
    default CreativeModeTab amy$translate(String translation) {
        return ScrawledRegisterable.super.amy$translate(translation);
    }

    @Override
    default CreativeModeTab amy$translate(String key, String translation) {
        return ScrawledRegisterable.super.amy$translate(key, translation);
    }

    @Override
    default CreativeModeTab amy$dyeTranslate(String key, String translation) {
        return ScrawledRegisterable.super.amy$dyeTranslate(key, translation);
    }

    @Override
    default CreativeModeTab amy$addTo(TagKey... targets) {
        return ScrawledRegisterable.super.amy$addTo(targets);
    }
}