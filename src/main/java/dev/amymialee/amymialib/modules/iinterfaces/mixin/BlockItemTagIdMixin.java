package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaglike;
import net.minecraft.tags.BlockItemTagId;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BlockItemTagId.class)
public class BlockItemTagIdMixin implements AmyMialibTaglike {
    @Shadow @Final private TagKey<Block> block;
    @Shadow @Final private TagKey<Item> item;

    @Override
    public List<TagKey> amy$tags() {
        return List.of(this.block, this.item);
    }
}