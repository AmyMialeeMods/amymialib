package dev.amymialee.amymialib.util.registry;

import dev.amymialee.amymialib.util.AMRegistry;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public class AMBlockRegistryInstance extends AMRegistryInstance<Block> {
    protected AMItemRegistryInstance item = null;

    public AMBlockRegistryInstance(AMRegistry amRegistry, ResourceKey<Block> key, Block.@NotNull Properties settings, @NotNull Function<Block.Properties, Block> function) {
        this(amRegistry, key, function.apply(settings.setId(key)));
    }

    public AMBlockRegistryInstance(AMRegistry amRegistry, ResourceKey<Block> key, Block value) {
        super(amRegistry, BuiltInRegistries.BLOCK, key, value);
        if (amRegistry.shouldRegisterBlockItems) this.blockItem();
    }

    public AMBlockRegistryInstance blockItem() {
        if (item == null) item = amRegistry.init(key.identifier().getPath(), new Item.Properties(), (p) -> new BlockItem(value, p));
        return this;
    }

    public AMBlockRegistryInstance creativeTab(ResourceKey<CreativeModeTab> tab) {
        if (item != null) item.creativeTab(tab);
        return this;
    }

    @SafeVarargs
    public final AMBlockRegistryInstance creativeTab(ResourceKey<CreativeModeTab> tab, Consumer<FabricCreativeModeTabOutput>... consumers) {
        if (item != null) item.creativeTab(tab, consumers);
        return this;
    }

    @Override
    public void postRegister() {
        if (item != null) item.register();
    }
}