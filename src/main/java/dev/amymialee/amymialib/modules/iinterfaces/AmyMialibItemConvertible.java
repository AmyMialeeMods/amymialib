package dev.amymialee.amymialib.modules.iinterfaces;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public interface AmyMialibItemConvertible {
    Map<EntityType<?>, Item> BY_ENTITY = new HashMap<>();

    default Item amy$asItem() { throw new UnsupportedOperationException("Implemented via mixin."); }
}