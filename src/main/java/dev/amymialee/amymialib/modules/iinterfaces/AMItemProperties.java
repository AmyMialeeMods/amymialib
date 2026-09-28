package dev.amymialee.amymialib.modules.iinterfaces;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;

public interface AMItemProperties {
    default Item.Properties am$unit(DataComponentType<Unit> type) {
        return ((Item.Properties) this).component(type, Unit.INSTANCE);
    }
}