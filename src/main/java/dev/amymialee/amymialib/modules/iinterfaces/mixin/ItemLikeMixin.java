package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibItemConvertible;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemLike.class)
public interface ItemLikeMixin extends AmyMialibItemConvertible {
    @Shadow Item asItem();

    @Override
    default Item amy$asItem() {
        return this.asItem();
    }
}