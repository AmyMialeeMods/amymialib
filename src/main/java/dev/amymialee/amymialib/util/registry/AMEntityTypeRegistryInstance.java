package dev.amymialee.amymialib.util.registry;

import dev.amymialee.amymialib.util.AMRegistry;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AMEntityTypeRegistryInstance extends AMRegistryInstance<EntityType<?>> {
    protected AMItemRegistryInstance item = null;

    public AMEntityTypeRegistryInstance(AMRegistry amRegistry, ResourceKey<EntityType<?>> key, @NotNull EntityType.Builder<?> builder) {
        super(amRegistry, BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
        if (amRegistry.shouldRegisterSpawnEggs) this.spawnEgg();
        attributes(Guardian.createAttributes());
    }

    @SuppressWarnings({"unchecked", "DataFlowIssue"})
    public AMEntityTypeRegistryInstance attributes(AttributeSupplier.Builder attributes) {
        try {
            FabricDefaultAttributeRegistry.register((EntityType<? extends LivingEntity>) value, attributes);
        } catch (IllegalArgumentException _) {
            throw new IllegalStateException("Tried to register entity attributes on a non LivingEntity %s with the %s AMRegistry!".formatted(key, this.amRegistry.namespace));
        }
        return this;
    }

    public AMEntityTypeRegistryInstance spawnEgg() {
        if (item == null) item = amRegistry.init(key.identifier().getPath(), new Item.Properties().spawnEgg(value), SpawnEggItem::new);
        return this;
    }

    public AMEntityTypeRegistryInstance creativeTab(ResourceKey<CreativeModeTab> tab) {
        if (item != null) item.creativeTab(tab);
        return this;
    }

    @SafeVarargs
    public final AMEntityTypeRegistryInstance creativeTab(ResourceKey<CreativeModeTab> tab, Consumer<FabricCreativeModeTabOutput>... consumers) {
        if (item != null) item.creativeTab(tab, consumers);
        return this;
    }

    @Override
    public void postRegister() {
        if (item != null) item.register();
    }
}