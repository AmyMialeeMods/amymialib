package dev.amymialee.amymialib.modules.interfaces.mixin;

import dev.amymialee.amymialib.modules.interfaces.ScrawledResourceKey;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("unchecked")
@Mixin(ResourceKey.class)
public abstract class ResourceKeyMixin<T> implements ScrawledResourceKey {
    @Shadow public abstract Identifier registry();
    @Shadow public abstract Identifier identifier();

    @Unique private @Nullable Scribbler scribbler;

    @Override
    public ResourceKey<T> amymialib$onScribble(Scribbler scribbler) {
        this.scribbler = scribbler;
        return (ResourceKey<T>) (Object) this;
    }

    @Override
    public @Nullable Scribbler amy$scribbler() {
        return this.scribbler;
    }

    @Override
    public ResourceKey<T> amy$translate(String translation) {
        Optional.ofNullable(this.amy$scribbler()).ifPresent(s -> s.translation(Util.makeDescriptionId(this.registry().toShortLanguageKey(), this.identifier()), translation));
        return (ResourceKey<T>) (Object) this;
    }
}