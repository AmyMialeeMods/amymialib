package dev.amymialee.amymialib.modules.iinterfaces.mixin.notinjected;

import dev.amymialee.amymialib.modules.iinterfaces.AMSilencer;
import org.reflections.util.ConfigurationBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ConfigurationBuilder.class)
public class ConfigurationBuilderMixin implements AMSilencer<ConfigurationBuilder> {
    @Unique private boolean silenced = false;

    @Override
    public boolean am$isSilenced() {
        return silenced;
    }

    @Override
    public ConfigurationBuilder am$silence() {
        return this.am$setSilenced(true);
    }

    @Override
    public ConfigurationBuilder am$setSilenced(boolean silenced) {
        this.silenced = silenced;
        return (ConfigurationBuilder) (Object) this;
    }
}