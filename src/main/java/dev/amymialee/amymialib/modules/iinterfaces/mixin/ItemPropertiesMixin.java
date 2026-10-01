package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibItemConvertible;
import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibItemProperties;
import net.minecraft.core.component.DataComponentInitializers;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Properties.class)
public abstract class ItemPropertiesMixin implements AmyMialibItemProperties {
    @Shadow public abstract <T> Item.Properties component(DataComponentType<T> type, T value);
    @Shadow private DataComponentInitializers.Initializer<Item> componentInitializer;

    @Unique private EntityType<?> entityType;
    @Unique private ResourceKey<Item> id;

    @Inject(method = "spawnEgg", at = @At("TAIL"))
    private void amymialib$remember(EntityType<?> type, CallbackInfoReturnable<Item.Properties> cir) {
        this.entityType = type;
        this.amymialib$memories();
    }

    @Inject(method = "setId", at = @At("TAIL"))
    private void amymialib$remember(ResourceKey<Item> id, CallbackInfoReturnable<Item.Properties> cir) {
        this.id = id;
        this.amymialib$memories();
    }

    @Unique
    private void amymialib$memories() {
        if (this.id != null && this.entityType != null) AmyMialibItemConvertible.ENTITY_BY_EGG.put(this.id, this.entityType);
    }
}