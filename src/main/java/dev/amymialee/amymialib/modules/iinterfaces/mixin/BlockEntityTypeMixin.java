package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaggable;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BlockEntityType.class)
public abstract class BlockEntityTypeMixin implements AmyMialibTaggable {
    @Shadow @Deprecated public abstract Holder.Reference<BlockEntityType<?>> builtInRegistryHolder();

    @Override
    public List<ResourceKey> amymialib$keys() {
        return List.of(this.builtInRegistryHolder().key());
    }
}