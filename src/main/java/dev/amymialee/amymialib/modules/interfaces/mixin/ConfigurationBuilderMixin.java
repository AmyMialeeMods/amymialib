package dev.amymialee.amymialib.modules.interfaces.mixin;

import dev.amymialee.amymialib.modules.interfaces.AmyMialibSilencer;
import org.reflections.util.ConfigurationBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ConfigurationBuilder.class)
public class ConfigurationBuilderMixin implements AmyMialibSilencer<ConfigurationBuilder> {
    @Unique private boolean silenced = false;

    @Override
    public boolean amymialib$isSilenced() {
        return silenced;
    }

    @Override
    public ConfigurationBuilder amymialib$silence() {
        return this.amymialib$setSilenced(true);
    }

    @Override
    public ConfigurationBuilder amymialib$setSilenced(boolean silenced) {
        this.silenced = silenced;
        return (ConfigurationBuilder) (Object) this;
    }
}