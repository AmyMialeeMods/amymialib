package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibItemConvertible;
import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaggable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin implements AmyMialibItemConvertible, AmyMialibTaggable {
    @Shadow @Deprecated public abstract Holder.Reference<EntityType<?>> builtInRegistryHolder();

    @Override
    public Item amy$asItem() {
        return AmyMialibItemConvertible.BY_ENTITY.get((EntityType<?>) (Object) this);
    }

    @Override
    public List<ResourceKey> amy$keys() {
        var list = new ArrayList<ResourceKey>(List.of(this.builtInRegistryHolder().key()));
        if (this.amy$asItem() != Items.AIR && this.amy$asItem() != null) list.add(this.amy$asItem().builtInRegistryHolder().key());
        return list;
    }
}