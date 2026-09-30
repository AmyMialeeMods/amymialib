package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibItemConvertible;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockBehaviour$BlockStateBaseMixin implements AmyMialibItemConvertible {
    @Shadow public abstract Block getBlock();

    @Override
    public Item amy$asItem() {
        return this.getBlock().asItem();
    }
}