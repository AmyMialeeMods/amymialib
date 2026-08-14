package dev.amymialee.amymialib.util.registry;

import dev.amymialee.amymialib.util.AMRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class AMItemRegistryInstance extends AMRegistryInstance<Item> {
    public AMItemRegistryInstance(AMRegistry amRegistry, ResourceKey<Item> key, Item.@NotNull Properties settings, @NotNull Function<Item.Properties, Item> function) {
        this(amRegistry, key, function.apply(settings.setId(key)));
    }

    public AMItemRegistryInstance(AMRegistry amRegistry, ResourceKey<Item> key, Item value) {
        super(amRegistry, BuiltInRegistries.ITEM, key, value);
    }

    public @SuppressWarnings("deprecation") AMItemRegistryInstance bannerTranslation(ResourceKey<BannerPattern> pattern) {
        for (var dye : DyeColor.values()) this.translation("block.%s.banner.%s.%s".formatted(pattern.identifier().getNamespace(), pattern.identifier().getPath(), dye.getName()), WordUtils.capitalizeFully("%s %s".formatted(dye.getName().replace('_', ' '), pattern.identifier().getPath())));
        return this;
    }

    @Override
    public void postRegister() {
        if (amRegistry.creativeTab != null) CreativeModeTabEvents.modifyOutputEvent(amRegistry.creativeTab).register(output -> output.accept(value));
        if (value instanceof BlockItem blockItem) blockItem.registerBlocks(Item.BY_BLOCK, value);
    }
}