package dev.amymialee.amymialib.mixin.iinterfaces;

import dev.amymialee.amymialib.util.iinterfaces.AMComponent;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Component.class)
public interface ComponentMixin extends AMComponent {}