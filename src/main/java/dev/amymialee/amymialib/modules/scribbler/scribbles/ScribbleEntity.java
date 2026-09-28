package dev.amymialee.amymialib.modules.scribbler.scribbles;

import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ScribbleEntity<T extends Entity> extends Scribble<EntityType<T>> implements EarlyScribble, LateScribble {
    private @Nullable Supplier<AttributeSupplier.Builder> attributes;
    private @Nullable Supplier<Item.Properties> itemProperties = Item.Properties::new;
    private @Nullable Scribble<Item> spawnEgg;
    private @Nullable String translation;

    @ApiStatus.Internal
    public ScribbleEntity(Scribbler scribbler, Registry<EntityType<T>> registry, Supplier<Holder.Reference<EntityType<T>>> getter) {
        super(scribbler, registry, getter);
    }

    public @Nullable Scribble<Item> egg() {
        return this.spawnEgg;
    }

    public ScribbleEntity<T> noEgg() {
        this.itemProperties = null;
        return this;
    }

    public ScribbleEntity<T> withEgg(Supplier<Item.Properties> properties) {
        this.itemProperties = properties;
        return this;
    }

    public ScribbleEntity<T> attributes(Supplier<AttributeSupplier.Builder> attributes) {
        this.attributes = attributes;
        return this;
    }

    @Override
    public ScribbleEntity<T> translation(String translation) {
        this.translation = translation;
        return (ScribbleEntity<T>) super.translation(translation);
    }

    @Override
    public ScribbleEntity<T> translation(String key, String translation) {
        return (ScribbleEntity<T>) super.translation(key, translation);
    }

    @Override
    public ScribbleEntity<T> dyeTranslation(String key, String translation) {
        return (ScribbleEntity<T>) super.dyeTranslation(key, translation);
    }

    @Override
    public final ScribbleEntity<T> addTo(TagKey... targets) {
        return (ScribbleEntity<T>) super.addTo(targets);
    }

    @Override
    public final ScribbleEntity<T> addTo(List<TagKey> targets1, TagKey ... targets2) {
        return (ScribbleEntity<T>) super.addTo(targets1, targets2);
    }

    @Override
    public void before() {
        if (this.itemProperties == null) return;
        this.spawnEgg = this.scribbler.of("%s_%s".formatted(this.key().identifier().getPath(), "spawn_egg"), SpawnEggItem::new, this.itemProperties.get().spawnEgg(this.get()));
        if (this.translation != null) this.spawnEgg.translation("%s %s".formatted(translation, "Spawn Egg"));
    }

    @Override
    public void after() {
        if (LivingEntity.class.isAssignableFrom(this.get().getBaseClass())) {
            if (this.attributes == null) {
                this.scribbler.logger.warn("Attributes weren't scribbled for entity {}, make sure they are done elsewhere.", this.key());
            } else {
                FabricDefaultAttributeRegistry.register((EntityType<? extends LivingEntity>) this.get(), this.attributes.get());
            }
        } else if (this.attributes != null) {
            this.scribbler.logger.error("Attributes are not required for non-living entities, tried add them to {}.", this.key());
        }
    }
}