package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTrackable;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public abstract class LevelMixin implements AmyMialibTrackable {}