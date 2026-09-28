package dev.amymialee.amymialib.modules.interfaces.registerable;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public interface ScrawledItem extends ScrawledRegisterable<Item> {
    @Override
    void amymialib$onRegister(Holder.Reference<Item> result);

    @Override
    default Holder.Reference<Item> amy$holder() {
        return ScrawledRegisterable.super.amy$holder();
    }

    @Override
    default ResourceKey<Item> amy$key() {
        return ScrawledRegisterable.super.amy$key();
    }

    @Override
    default Item amy$translate(String translation) {
        return ScrawledRegisterable.super.amy$translate(translation);
    }

    @Override
    default Item amy$translate(String key, String translation) {
        return ScrawledRegisterable.super.amy$translate(key, translation);
    }

    @Override
    default Item amy$dyeTranslate(String key, String translation) {
        return ScrawledRegisterable.super.amy$dyeTranslate(key, translation);
    }

    @Override
    default Item amy$addTo(TagKey... targets) {
        return ScrawledRegisterable.super.amy$addTo(targets);
    }
}