package dev.amymialee.amymialib.modules.interfaces.mixin;

import dev.amymialee.amymialib.modules.interfaces.ScrawledTagKey;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.fabricmc.fabric.api.tag.FabricTagKey;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings("unchecked")
@Mixin(TagKey.class)
public abstract class TagKeyMixin<T> implements ScrawledTagKey<T>, FabricTagKey {
    @Unique private @Nullable Scribbler scribbler;

    @Override
    public TagKey<T> amymialib$onScribble(Scribbler scribbler) {
        this.scribbler = scribbler;
        return (TagKey<T>) (Object) this;
    }

    @Override
    public @Nullable Scribbler amy$scribbler() {
        return this.scribbler;
    }

    @Override
    public TagKey<T> amy$translate(String translation) {
        Optional.ofNullable(this.amy$scribbler()).ifPresent(s -> s.translation(this.getTranslationKey(), translation));
        return (TagKey<T>) (Object) this;
    }
}