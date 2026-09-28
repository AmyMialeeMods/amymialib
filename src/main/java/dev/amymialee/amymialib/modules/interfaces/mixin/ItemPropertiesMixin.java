package dev.amymialee.amymialib.modules.interfaces.mixin;

import dev.amymialee.amymialib.modules.interfaces.ScrawledItemProperties;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.Properties.class)
public class ItemPropertiesMixin implements ScrawledItemProperties {}