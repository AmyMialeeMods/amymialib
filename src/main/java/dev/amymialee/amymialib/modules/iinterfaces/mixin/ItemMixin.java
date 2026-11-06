package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaggable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin implements AmyMialibTaggable {
    @Shadow @Deprecated public abstract Holder.Reference<Item> builtInRegistryHolder();

    @Override
    public List<ResourceKey> amy$keys() {
        return List.of(this.builtInRegistryHolder().key());
    }
}