package dev.amymialee.amymialib.modules.interfaces.mixin;

import dev.amymialee.amymialib.modules.interfaces.ScrawledComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Component.class)
public interface ComponentMixin extends ScrawledComponent {}