package dev.amymialee.amymialib.modules.interfaces.registerable;

import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Holder;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public interface ScrawledBlock extends ScrawledRegisterable<Block> {
    default BlockItemId amy$blockItemId() {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }

    default Block amy$withItem() {
        return this.amy$withItem(new Item.Properties());
    }

    default Block amy$withItem(Item.Properties properties) {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }

    default Block amy$addItemTo(TagKey... targets) {
        var self = (Block) this;
        self.asItem().amy$addTo(targets);
        return self;
    }

    default Block amy$addTo(BlockItemTagId... targets) {
        var self = (Block) this;
        for (var t : targets) {
            this.amy$addTo(t.block());
            self.asItem().amy$addTo(t.item());
        }
        return self;
    }

    @Override
    void amymialib$onScribble(Scribbler scribbler);

    @Override
    void amymialib$onRegister(Holder.Reference<Block> result);

    @Override
    default Scribbler amy$scribbler() {
        return ScrawledRegisterable.super.amy$scribbler();
    }

    @Override
    default Holder.Reference<Block> amy$holder() {
        return ScrawledRegisterable.super.amy$holder();
    }

    @Override
    default ResourceKey<Block> amy$key() {
        return ScrawledRegisterable.super.amy$key();
    }

    @Override
    default Block amy$translate(String translation) {
        return ScrawledRegisterable.super.amy$translate(translation);
    }

    @Override
    default Block amy$translate(String key, String translation) {
        return ScrawledRegisterable.super.amy$translate(key, translation);
    }

    @Override
    default Block amy$dyeTranslate(String key, String translation) {
        return ScrawledRegisterable.super.amy$dyeTranslate(key, translation);
    }

    @Override
    default Block amy$addTo(TagKey... targets) {
        return ScrawledRegisterable.super.amy$addTo(targets);
    }
}