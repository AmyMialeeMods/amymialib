package dev.amymialee.amymialib.modules.scribbler.scribbles;

import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.Supplier;

public class ScribbleBlock extends Scribble<Block> implements EarlyScribble {
    private Item.Properties itemProperties = new Item.Properties().useBlockDescriptionPrefix();
    private Scribble<Item> item;

    @ApiStatus.Internal
    public ScribbleBlock(Scribbler scribbler, Supplier<Holder.Reference<Block>> getter) {
        super(scribbler, BuiltInRegistries.BLOCK, getter);
    }

    @ApiStatus.Internal @SuppressWarnings({"DataFlowIssue", "OptionalGetWithoutIsPresent"})
    public static ScribbleBlock fakeBlock(Scribbler scribbler, ResourceKey<Block> key) {
        var scribble = new ScribbleBlock(scribbler, null);
        scribble.holder = BuiltInRegistries.BLOCK.get(key.identifier()).get();
        return scribble;
    }

    public Scribble<Item> item() {
        if (this.item == null) this.before();
        return this.item;
    }

    public ScribbleBlock withItem(Item.Properties properties) {
        this.itemProperties = properties.useBlockDescriptionPrefix();
        return this;
    }

    @Override
    public ScribbleBlock translation(String translation) {
        return (ScribbleBlock) super.translation(translation);
    }

    @Override
    public ScribbleBlock translation(String key, String translation) {
        return (ScribbleBlock) super.translation(key, translation);
    }

    @Override
    public ScribbleBlock dyeTranslation(String key, String translation) {
        return (ScribbleBlock) super.dyeTranslation(key, translation);
    }

    @Override
    public final ScribbleBlock addTo(TagKey... targets) {
        return (ScribbleBlock) super.addTo(targets);
    }

    @Override
    public final ScribbleBlock addTo(List<TagKey> targets1, TagKey ... targets2) {
        return (ScribbleBlock) super.addTo(targets1, targets2);
    }

    public final ScribbleBlock addItemTo(TagKey... targets) {
        this.item().addTo(targets);
        return this;
    }

    public final ScribbleBlock addItemTo(List<TagKey> targets1, TagKey ... targets2) {
        this.item().addTo(targets1, targets2);
        return this;
    }

    public final ScribbleBlock addTo(BlockItemTagId... targets) {
        for (var t : targets) {
            this.addTo(t.block());
            this.item().addTo(t.item());
        }
        return this;
    }

    public final ScribbleBlock addTo(List<BlockItemTagId> targets1, BlockItemTagId ... targets2) {
        for (var t : targets1) {
            this.addTo(t.block());
            this.item().addTo(t.item());
        }
        for (var t : targets2) {
            this.addTo(t.block());
            this.item().addTo(t.item());
        }
        return this;
    }

    @ApiStatus.Internal @Override
    public void before() {
        if (this.itemProperties == null || this.item != null) return;
        var item = Item.BY_BLOCK.get(this.get());
        if (item == null) {
            this.item = this.scribbler.of(this.key().identifier().getPath(), p -> new BlockItem(this.get(), p), this.itemProperties);
        } else {
            this.item = Scribble.fake(scribbler, BuiltInRegistries.ITEM.get(BuiltInRegistries.ITEM.getKey(item)).get().key());
        }
    }

    public BlockItemId blockItemId() {
        return new BlockItemId(this.key(), this.item().key());
    }
}