package dev.amymialee.amymialib.util.registry;

import dev.amymialee.amymialib.util.AMRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class AMBlockRegistryInstance extends AMRegistryInstance<Block> {
    protected AMItemRegistryInstance item = null;
    protected boolean noItem = false;

    public AMBlockRegistryInstance(AMRegistry amRegistry, ResourceKey<Block> key, Block.@NotNull Properties settings, @NotNull Function<Block.Properties, Block> function) {
        this(amRegistry, key, function.apply(settings.setId(key)));
    }

    public AMBlockRegistryInstance(AMRegistry amRegistry, ResourceKey<Block> key, Block value) {
        super(amRegistry, BuiltInRegistries.BLOCK, key, value);
    }

    public AMBlockRegistryInstance noItem() {
        this.noItem = true;
        return this;
    }

    @Override
    public void postRegister() {
        if (!this.noItem) getItem().register();
    }

    public AMItemRegistryInstance getItem() {
        if (item == null) {
            if (this.noItem) throw new IllegalStateException("Attempted to create item for entity %s when told not to!".formatted(this.key));
            item = amRegistry.init(key.identifier().getPath(), p -> new BlockItem(value, p), new Item.Properties().useBlockDescriptionPrefix());
        }
        return item;
    }

    public AMBlockRegistryInstance setItem(Item.Properties properties) {
        if (item != null) throw new IllegalStateException("Attempted to create item for block %s when one exists!".formatted(this.key));
        item = amRegistry.init(key.identifier().getPath(), p -> new BlockItem(value, p), properties.useBlockDescriptionPrefix());
        return this;
    }
}