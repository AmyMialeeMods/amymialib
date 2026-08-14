package dev.amymialee.amymialib.util.registry;

import dev.amymialee.amymialib.util.AMRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.NotNull;

public class AMEntityTypeRegistryInstance<T extends Entity> extends AMRegistryInstance<EntityType<T>> {
    protected AMItemRegistryInstance item = null;
    protected boolean noItem = false;

    public @SuppressWarnings("unchecked") AMEntityTypeRegistryInstance(AMRegistry amRegistry, ResourceKey<EntityType<T>> key, @NotNull EntityType.Builder<T> builder) {
        super(amRegistry, (Registry<EntityType<T>>) (Object) BuiltInRegistries.ENTITY_TYPE, key, builder.build((ResourceKey<EntityType<?>>) (Object) key));
    }

    public AMEntityTypeRegistryInstance<T> noItem() {
        this.noItem = true;
        return this;
    }

    public @SuppressWarnings({"unchecked", "DataFlowIssue"}) AMEntityTypeRegistryInstance<T> attributes(AttributeSupplier.Builder attributes) {
        try {
            FabricDefaultAttributeRegistry.register((EntityType<? extends LivingEntity>) value, attributes);
        } catch (IllegalArgumentException _) {
            throw new IllegalStateException("Tried to register entity attributes on a non LivingEntity %s with the %s AMRegistry!".formatted(key, this.amRegistry.namespace));
        }
        return this;
    }

    @Override
    public AMRegistryInstance<EntityType<T>> translation(String translation) {
        if (!noItem) this.getItem().translation(translation + " Spawn Egg");
        return super.translation(translation);
    }

    @Override
    public void postRegister() {
        if (this.noItem) return;
        this.getItem().register();
        this.amRegistry.spawnEggs.put(this.value, this.item.value);
    }

    public AMItemRegistryInstance getItem() {
        if (item == null) {
            if (this.noItem) throw new IllegalStateException("Attempted to create item for entity %s when told not to!".formatted(this.key));
            item = amRegistry.init(key.identifier().getPath() + "_spawn_egg", SpawnEggItem::new, new Item.Properties().spawnEgg(value));
        }
        return item;
    }
}