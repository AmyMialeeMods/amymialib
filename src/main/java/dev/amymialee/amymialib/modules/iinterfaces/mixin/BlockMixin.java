package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibBlock;
import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaggable;
import net.minecraft.core.Holder;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(Block.class)
public abstract class BlockMixin implements AmyMialibBlock, AmyMialibTaggable {
    @Shadow @Deprecated public abstract Holder.Reference<Block> builtInRegistryHolder();
    @Shadow public abstract Item asItem();

    @Override
    public BlockItemId amy$blockItemId() {
        return new BlockItemId(this.builtInRegistryHolder().key(), this.asItem().builtInRegistryHolder().key());
    }

    @Override
    public List<ResourceKey> amymialib$keys() {
        var list = new ArrayList<ResourceKey>(List.of(this.builtInRegistryHolder().key()));
        if (this.asItem() != Items.AIR) list.add(this.asItem().builtInRegistryHolder().key());
        return list;
    }
}