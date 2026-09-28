package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AMTypedInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.TypedInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TypedInstance.class)
public interface TypedInstanceMixin<T> extends AMTypedInstance<T> {
    @Shadow Holder<T> typeHolder();

    @Override
    default boolean am$is(TypedInstance<T> type) {
        return this.typeHolder() == type.typeHolder();
    }
}