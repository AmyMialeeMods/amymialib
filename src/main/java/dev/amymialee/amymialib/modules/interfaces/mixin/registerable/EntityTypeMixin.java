package dev.amymialee.amymialib.modules.interfaces.mixin.registerable;

import dev.amymialee.amymialib.modules.interfaces.registerable.ScrawledEntity;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("unchecked")
@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T extends Entity> implements ScrawledEntity<T> {
    @Unique private @Nullable Scribbler scribbler;
    @Unique private @Nullable Holder.Reference<EntityType<T>> holder;
    @Unique private Item egg;

    @Override
    public Item amy$egg() {
        return this.egg;
    }

    @Override
    public EntityType<T> amy$withEgg(Item.Properties properties) {
        var self = (EntityType<T>) (Object) this;
        Optional.ofNullable(this.amy$scribbler()).ifPresent(s -> this.egg = s.of("%s_%s".formatted(this.amy$key().identifier().getPath(), "spawn_egg"), SpawnEggItem::new, properties.spawnEgg(self)));
        return self;
    }

    @Override
    public EntityType<T> amy$translate(String translation) {
        var self = (EntityType<T>) (Object) this;
        if (egg != null) egg.amy$translate("%s %s".formatted(translation, "Spawn Egg"));
        ScrawledEntity.super.amy$translate(translation);
        return self;
    }

    @Override
    public void amymialib$onScribble(Scribbler scribbler) {
        this.scribbler = scribbler;
    }

    @Override
    public void amymialib$onRegister(Holder.Reference<EntityType<T>> holder) {
        this.holder = holder;
    }

    @Override
    public @Nullable Scribbler amy$scribbler() {
        return this.scribbler;
    }

    @Override
    public @Nullable Holder.Reference<EntityType<T>> amy$holder() {
        return this.holder;
    }

    @Override
    public EntityType<T> amy$addItemTo(TagKey... targets) {
        return ScrawledEntity.super.amy$addItemTo(targets);
    }

    @Override
    public EntityType<T> amy$attributes(Supplier<AttributeSupplier.Builder> attributes) {
        return ScrawledEntity.super.amy$attributes(attributes);
    }

    @Override
    public ResourceKey<EntityType<T>> amy$key() {
        return ScrawledEntity.super.amy$key();
    }

    @Override
    public EntityType<T> amy$translate(String key, String translation) {
        return ScrawledEntity.super.amy$translate(key, translation);
    }

    @Override
    public EntityType<T> amy$dyeTranslate(String key, String translation) {
        return ScrawledEntity.super.amy$dyeTranslate(key, translation);
    }

    @Override
    public EntityType<T> amy$addTo(TagKey... targets) {
        return ScrawledEntity.super.amy$addTo(targets);
    }
}