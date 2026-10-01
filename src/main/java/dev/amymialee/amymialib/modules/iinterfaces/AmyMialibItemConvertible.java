package dev.amymialee.amymialib.modules.iinterfaces;

import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleInit;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

public interface AmyMialibItemConvertible {
    Map<ResourceKey<Item>, EntityType<?>> ENTITY_BY_EGG = new HashMap<>();
    Map<EntityType<?>, Item> BY_ENTITY = new HashMap<>();

    default Item amy$asItem() { throw new UnsupportedOperationException("Implemented via mixin."); }

    @ScribbleInit
    static void register() {
        RegistryEntryAddedCallback.allEntries(BuiltInRegistries.ITEM, h -> {
            var key = h.key();
            if (ENTITY_BY_EGG.containsKey(key)) BY_ENTITY.put(ENTITY_BY_EGG.get(key), h.value());
        });
    }
}