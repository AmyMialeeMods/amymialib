package dev.amymialee.amymialib.modules.interfaces;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;

public interface ScrawledItemProperties {
    default Item.Properties amy$unit(DataComponentType<Unit> type) {
        return ((Item.Properties) this).component(type, Unit.INSTANCE);
    }
}