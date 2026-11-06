package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaggable;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(ResourceKey.class)
public class ResourceKeyMixin<T> implements AmyMialibTaggable {
    @Override
    public List<ResourceKey> amy$keys() {
        var self = (ResourceKey) (Object) this;
        return List.of(self);
    }
}