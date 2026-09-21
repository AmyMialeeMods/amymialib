package dev.amymialee.amymialib.modules.scribbler.scribbles;

import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ScribbleEntity<T extends Entity> extends Scribble<EntityType<T>> {
    private @Nullable Supplier<AttributeSupplier.Builder> attributes;
    private @Nullable Supplier<Item.Properties> itemProperties;
    private @Nullable Scribble<SpawnEggItem> spawnEgg;

    @ApiStatus.Internal
    public ScribbleEntity(Scribbler scribbler, Supplier<Holder.Reference<EntityType<T>>> getter) {
        super(scribbler, getter);
    }

    @Override @ApiStatus.Internal @SuppressWarnings({"unchecked", "DataFlowIssue"})
    public void run() {
        super.run();
        if (this.attributes != null) {
            if (LivingEntity.class.isAssignableFrom(this.get().getBaseClass())) {
                FabricDefaultAttributeRegistry.register((EntityType<? extends LivingEntity>) this.get(), this.attributes.get());
            } else {
                this.scribbler.logger.error("Attributes are not required for non-living entities, tried add them to {}.", this.key());
            }
        }
        if (this.itemProperties != null) {
            this.spawnEgg = new Scribble<>(this.scribbler, () -> {
                var entityKey = this.key();
                var itemKey = ResourceKey.create(Registries.ITEM, this.scribbler.id("%s_%s".formatted(entityKey.identifier().getPath(), "spawn_egg")));
                var item = Registry.registerForHolder(BuiltInRegistries.ITEM, itemKey, new SpawnEggItem(this.itemProperties.get().setId(itemKey).spawnEgg(this.get())));
                if (this.scribbler.creativeTab != null) CreativeModeTabEvents.modifyOutputEvent(this.scribbler.creativeTab).register(output -> output.accept(item.value()));
                return item;
            });
        }
    }

    @Override
    public ScribbleEntity<T> translation(String translation) {
        super.translation(translation);
        if (this.spawnEgg != null) this.spawnEgg.translation("%s %s".formatted(translation, "Spawn Egg"));
        return this;
    }

    public @Nullable Scribble<SpawnEggItem> spawnEgg() {
        return this.spawnEgg;
    }

    public ScribbleEntity<T> withEgg() {
        return this.withEgg(Item.Properties::new);
    }

    public ScribbleEntity<T> withEgg(Supplier<Item.Properties> properties) {
        if (this.itemProperties != null) throw new IllegalStateException("Item properties already set");
        this.itemProperties = properties;
        return this;
    }

    public ScribbleEntity<T> withAttributes(Supplier<AttributeSupplier.Builder> attributes) {
        this.attributes = attributes;
        return this;
    }
}