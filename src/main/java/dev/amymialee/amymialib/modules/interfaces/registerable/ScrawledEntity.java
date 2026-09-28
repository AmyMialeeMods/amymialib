package dev.amymialee.amymialib.modules.interfaces.registerable;

import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public interface ScrawledEntity<T extends Entity> extends ScrawledRegisterable<EntityType<T>> {
    default EntityType<T> amy$withEgg(Item.Properties properties) {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }

    default Item amy$egg() {
        throw new UnsupportedOperationException("Implemented via mixin.");
    }

    default EntityType<T> amy$addItemTo(TagKey... targets) {
        this.amy$egg().amy$addTo(targets);
        return (EntityType<T>) this;
    }

    @SuppressWarnings("DataFlowIssue")
    default EntityType<T> amy$attributes(Supplier<AttributeSupplier.Builder> attributes) {
        var self = (EntityType<T>) this;
        if (LivingEntity.class.isAssignableFrom(self.getBaseClass())) {
            FabricDefaultAttributeRegistry.register((EntityType<? extends LivingEntity>) self, attributes.get());
        }
        return self;
    }

    @Override
    void amymialib$onScribble(Scribbler scribbler);

    @Override
    void amymialib$onRegister(Holder.Reference<EntityType<T>> result);

    @Override
    default Scribbler amy$scribbler() {
        return ScrawledRegisterable.super.amy$scribbler();
    }

    @Override
    default Holder.Reference<EntityType<T>> amy$holder() {
        return ScrawledRegisterable.super.amy$holder();
    }

    @Override
    default ResourceKey<EntityType<T>> amy$key() {
        return ScrawledRegisterable.super.amy$key();
    }

    @Override
    default EntityType<T> amy$translate(String translation) {
        return ScrawledRegisterable.super.amy$translate(translation);
    }

    @Override
    default EntityType<T> amy$translate(String key, String translation) {
        return ScrawledRegisterable.super.amy$translate(key, translation);
    }

    @Override
    default EntityType<T> amy$dyeTranslate(String key, String translation) {
        return ScrawledRegisterable.super.amy$dyeTranslate(key, translation);
    }

    @Override
    default EntityType<T> amy$addTo(TagKey... targets) {
        return ScrawledRegisterable.super.amy$addTo(targets);
    }
}