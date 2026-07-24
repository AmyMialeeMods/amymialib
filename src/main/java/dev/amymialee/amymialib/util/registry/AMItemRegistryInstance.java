package dev.amymialee.amymialib.util.registry;

import dev.amymialee.amymialib.util.AMRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class AMItemRegistryInstance extends AMRegistryInstance<Item> {
    protected Map<ResourceKey<CreativeModeTab>, List<Consumer<FabricCreativeModeTabOutput>>> creativeTabs = new HashMap<>();

    public AMItemRegistryInstance(AMRegistry amRegistry, ResourceKey<Item> key, Item.@NotNull Properties settings, @NotNull Function<Item.Properties, Item> function) {
        this(amRegistry, key, function.apply(settings.setId(key)));
    }

    public AMItemRegistryInstance(AMRegistry amRegistry, ResourceKey<Item> key, Item value) {
        super(amRegistry, BuiltInRegistries.ITEM, key, value);
        amRegistry.tabs.forEach(this::creativeTab);
    }

    public AMItemRegistryInstance creativeTab(ResourceKey<CreativeModeTab> tab) {
        creativeTabs.computeIfAbsent(tab, (_) -> new ArrayList<>()).add(((t) -> t.accept(value)));
        return this;
    }

    @SafeVarargs
    public final AMItemRegistryInstance creativeTab(ResourceKey<CreativeModeTab> tab, Consumer<FabricCreativeModeTabOutput>... consumers) {
        Collections.addAll(creativeTabs.computeIfAbsent(tab, (_) -> new ArrayList<>()), consumers);
        return this;
    }

    @Override
    public void postRegister() {
        creativeTabs.forEach((tab, list) -> CreativeModeTabEvents.modifyOutputEvent(tab).register(output -> list.forEach(c -> c.accept(output))));
        this.amRegistry.registeredItems.add(this);
        if (value instanceof BlockItem blockItem) blockItem.registerBlocks(Item.BY_BLOCK, value);
    }
}