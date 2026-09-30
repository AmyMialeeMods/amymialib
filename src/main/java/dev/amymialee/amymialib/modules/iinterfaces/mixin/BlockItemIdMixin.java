package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaggable;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BlockItemId.class)
public class BlockItemIdMixin implements AmyMialibTaggable {
    @Shadow @Final private ResourceKey<Block> block;
    @Shadow @Final private ResourceKey<Item> item;

    @Override
    public List<ResourceKey> amymialib$keys() {
        return List.of(this.block, this.item);
    }
}