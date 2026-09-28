package dev.amymialee.amymialib.modules.interfaces.mixin;

import dev.amymialee.amymialib.modules.interfaces.ScrawledSilencer;
import org.reflections.util.ConfigurationBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ConfigurationBuilder.class)
public class ConfigurationBuilderMixin implements ScrawledSilencer<ConfigurationBuilder> {
    @Unique private boolean silenced = false;

    @Override
    public boolean amy$isSilenced() {
        return silenced;
    }

    @Override
    public ConfigurationBuilder amy$silence() {
        return this.amy$setSilenced(true);
    }

    @Override
    public ConfigurationBuilder amy$setSilenced(boolean silenced) {
        this.silenced = silenced;
        return (ConfigurationBuilder) (Object) this;
    }
}