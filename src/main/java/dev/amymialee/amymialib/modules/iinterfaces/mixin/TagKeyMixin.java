package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaglike;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(TagKey.class)
public class TagKeyMixin implements AmyMialibTaglike {
    @Override
    public List<TagKey> amymialib$tags() {
        var self = (TagKey) (Object) this;
        return List.of(self);
    }
}