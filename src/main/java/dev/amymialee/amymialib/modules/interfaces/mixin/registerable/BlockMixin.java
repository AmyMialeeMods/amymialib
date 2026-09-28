package dev.amymialee.amymialib.modules.interfaces.mixin.registerable;

import dev.amymialee.amymialib.modules.interfaces.registerable.ScrawledBlock;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Holder;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("unchecked")
@Mixin(Block.class)
public abstract class BlockMixin implements ScrawledBlock, ItemLike {
    @Unique private @Nullable Scribbler scribbler;
    @Unique private @Nullable Holder.Reference<Block> holder;

    @Override
    public Block amy$withItem(Item.Properties properties) {
        var self = (Block) (Object) this;
        Optional.ofNullable(this.amy$scribbler()).ifPresent(s -> s.of(self.amy$key().identifier().getPath(), p -> new BlockItem(self, p), properties.useBlockDescriptionPrefix()));
        return self;
    }

    @Override
    public BlockItemId amy$blockItemId() {
        var self = (Block) (Object) this;
        return new BlockItemId(this.amy$key(), this.asItem().amy$key());
    }

    @Override
    public void amymialib$onScribble(Scribbler scribbler) {
        this.scribbler = scribbler;
    }

    @Override
    public void amymialib$onRegister(Holder.Reference<Block> holder) {
        this.holder = holder;
    }

    @Override
    public @Nullable Scribbler amy$scribbler() {
        return this.scribbler;
    }

    @Override
    public @Nullable Holder.Reference<Block> amy$holder() {
        return this.holder;
    }

    @Override
    public Block amy$addItemTo(TagKey... targets) {
        return ScrawledBlock.super.amy$addItemTo(targets);
    }

    @Override
    public Block amy$addTo(BlockItemTagId... targets) {
        return ScrawledBlock.super.amy$addTo(targets);
    }

    @Override
    public ResourceKey<Block> amy$key() {
        return ScrawledBlock.super.amy$key();
    }

    @Override
    public Block amy$translate(String translation) {
        return ScrawledBlock.super.amy$translate(translation);
    }

    @Override
    public Block amy$translate(String key, String translation) {
        return ScrawledBlock.super.amy$translate(key, translation);
    }

    @Override
    public Block amy$dyeTranslate(String key, String translation) {
        return ScrawledBlock.super.amy$dyeTranslate(key, translation);
    }

    @Override
    public Block amy$addTo(TagKey... targets) {
        return ScrawledBlock.super.amy$addTo(targets);
    }
}