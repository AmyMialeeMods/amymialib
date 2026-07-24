package dev.amymialee.amymialib.util;

import com.ibm.icu.impl.Pair;
import dev.amymialee.amymialib.util.registry.AMBlockRegistryInstance;
import dev.amymialee.amymialib.util.registry.AMEntityTypeRegistryInstance;
import dev.amymialee.amymialib.util.registry.AMItemRegistryInstance;
import dev.amymialee.amymialib.util.registry.AMRegistryInstance;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.client.color.item.Potion;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.numbers.NumberFormatType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.OutgoingRpcMethod;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.debug.DebugSubscription;
import net.minecraft.world.attribute.AttributeType;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class AMRegistry {
    public static final Map<Class<?>, Registry<?>> DEFAULT_REGISTRIES = new HashMap<>();
    public final String namespace;
    public final Map<String, String> translations = new HashMap<>();
    public final List<AMItemRegistryInstance> registeredItems = new ArrayList<>();
    public final List<ResourceKey<CreativeModeTab>> tabs = new ArrayList<>();
    public boolean shouldRegisterBlockItems = true;
    public boolean shouldRegisterSpawnEggs = true;

    public AMRegistry(String namespace) {
        this.namespace = namespace;
    }

    public void tab(ResourceKey<CreativeModeTab> tab) {
        this.tabs.add(tab);
        registeredItems.forEach((i) -> CreativeModeTabEvents.modifyOutputEvent(tab).register(output -> output.accept(i.value)));
    }

    public void blockItems(boolean shouldRegisterBlockItems) {
        this.shouldRegisterBlockItems = shouldRegisterBlockItems;
    }

    public void spawnEggs(boolean shouldRegisterSpawnEggs) {
        this.shouldRegisterSpawnEggs = shouldRegisterSpawnEggs;
    }

    public String addTranslation(@NonNull Pair<String, String> translation) {
        return this.addTranslation(translation.first, translation.second);
    }

    public String addTranslation(String key, @NonNull String translation) {
        this.translations.put(key, translation);
        return key;
    }

    public void buildTranslations(BiConsumer<String, String> consumer) {
        this.translations.forEach(consumer);
    }

    public @NotNull AMItemRegistryInstance initItem(String path) {
        return this.init(path, new Item.Properties());
    }

    public @NotNull AMItemRegistryInstance init(String path, Item.@NotNull Properties settings) {
        return this.init(path, settings, Item::new);
    }

    public @NotNull AMItemRegistryInstance init(String path, Item.@NotNull Properties settings, @NotNull Function<Item.Properties, Item> function) {
        return new AMItemRegistryInstance(this, ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(this.namespace, path)), settings, function);
    }

    public @NotNull AMRegistryInstance<Block> init(String path, BlockBehaviour.@NotNull Properties settings) {
        return this.init(path, settings, Block::new);
    }

    public @NotNull AMRegistryInstance<Block> init(String path, BlockBehaviour.@NotNull Properties settings, @NotNull Function<BlockBehaviour.Properties, Block> function) {
        return new AMBlockRegistryInstance(this, ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(this.namespace, path)), settings, function);
    }

    public @NotNull AMRegistryInstance<EntityType<?>> init(String path, @NotNull EntityType.Builder<?> builder) {
        return new AMEntityTypeRegistryInstance(this, ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(this.namespace, path)), builder);
    }

    @SuppressWarnings("unchecked")
    public @NotNull <T> AMRegistryInstance<T> init(String path, T thing) {
        for (var entry : DEFAULT_REGISTRIES.entrySet()) {
            if (!entry.getKey().isInstance(thing)) continue;
            var registry = (Registry<T>) entry.getValue();
            return new AMRegistryInstance<>(this, registry, ResourceKey.create(registry.key(), Identifier.fromNamespaceAndPath(this.namespace, path)), thing);
        }
        throw new IllegalStateException("Failed to register " + Identifier.fromNamespaceAndPath(this.namespace, path) + " to the " + this.namespace + " AMRegistry!");
    }

    static {
        DEFAULT_REGISTRIES.put(GameEvent.class, BuiltInRegistries.GAME_EVENT);
        DEFAULT_REGISTRIES.put(SoundEvent.class, BuiltInRegistries.SOUND_EVENT);
        DEFAULT_REGISTRIES.put(Fluid.class, BuiltInRegistries.FLUID);
        DEFAULT_REGISTRIES.put(MobEffect.class, BuiltInRegistries.MOB_EFFECT);
        DEFAULT_REGISTRIES.put(Block.class, BuiltInRegistries.BLOCK);
        DEFAULT_REGISTRIES.put(DebugSubscription.class, BuiltInRegistries.DEBUG_SUBSCRIPTION);
        DEFAULT_REGISTRIES.put(EntityType.class, BuiltInRegistries.ENTITY_TYPE);
        DEFAULT_REGISTRIES.put(Item.class, BuiltInRegistries.ITEM);
        DEFAULT_REGISTRIES.put(Potion.class, BuiltInRegistries.POTION);
        DEFAULT_REGISTRIES.put(ParticleType.class, BuiltInRegistries.PARTICLE_TYPE);
        DEFAULT_REGISTRIES.put(BlockEntityType.class, BuiltInRegistries.BLOCK_ENTITY_TYPE);
        DEFAULT_REGISTRIES.put(Identifier.class, BuiltInRegistries.CUSTOM_STAT);
        DEFAULT_REGISTRIES.put(ChunkStatus.class, BuiltInRegistries.CHUNK_STATUS);
        DEFAULT_REGISTRIES.put(RuleTestType.class, BuiltInRegistries.RULE_TEST);
        DEFAULT_REGISTRIES.put(RuleBlockEntityModifierType.class, BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER);
        DEFAULT_REGISTRIES.put(PosRuleTestType.class, BuiltInRegistries.POS_RULE_TEST);
        DEFAULT_REGISTRIES.put(MenuType.class, BuiltInRegistries.MENU);
        DEFAULT_REGISTRIES.put(RecipeType.class, BuiltInRegistries.RECIPE_TYPE);
        DEFAULT_REGISTRIES.put(RecipeSerializer.class, BuiltInRegistries.RECIPE_SERIALIZER);
        DEFAULT_REGISTRIES.put(Attribute.class, BuiltInRegistries.ATTRIBUTE);
        DEFAULT_REGISTRIES.put(PositionSourceType.class, BuiltInRegistries.POSITION_SOURCE_TYPE);
        DEFAULT_REGISTRIES.put(ArgumentTypeInfo.class, BuiltInRegistries.COMMAND_ARGUMENT_TYPE);
        DEFAULT_REGISTRIES.put(StatType.class, BuiltInRegistries.STAT_TYPE);
        DEFAULT_REGISTRIES.put(VillagerType.class, BuiltInRegistries.VILLAGER_TYPE);
        DEFAULT_REGISTRIES.put(VillagerProfession.class, BuiltInRegistries.VILLAGER_PROFESSION);
        DEFAULT_REGISTRIES.put(PoiType.class, BuiltInRegistries.POINT_OF_INTEREST_TYPE);
        DEFAULT_REGISTRIES.put(MemoryModuleType.class, BuiltInRegistries.MEMORY_MODULE_TYPE);
        DEFAULT_REGISTRIES.put(SensorType.class, BuiltInRegistries.SENSOR_TYPE);
        DEFAULT_REGISTRIES.put(Activity.class, BuiltInRegistries.ACTIVITY);
        DEFAULT_REGISTRIES.put(HeightProviderType.class, BuiltInRegistries.HEIGHT_PROVIDER_TYPE);
        DEFAULT_REGISTRIES.put(BlockPredicateType.class, BuiltInRegistries.BLOCK_PREDICATE_TYPE);
        DEFAULT_REGISTRIES.put(WorldCarver.class, BuiltInRegistries.CARVER);
        DEFAULT_REGISTRIES.put(Feature.class, BuiltInRegistries.FEATURE);
        DEFAULT_REGISTRIES.put(StructurePlacementType.class, BuiltInRegistries.STRUCTURE_PLACEMENT);
        DEFAULT_REGISTRIES.put(StructurePieceType.class, BuiltInRegistries.STRUCTURE_PIECE);
        DEFAULT_REGISTRIES.put(StructureType.class, BuiltInRegistries.STRUCTURE_TYPE);
        DEFAULT_REGISTRIES.put(PlacementModifierType.class, BuiltInRegistries.PLACEMENT_MODIFIER_TYPE);
        DEFAULT_REGISTRIES.put(BlockStateProviderType.class, BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE);
        DEFAULT_REGISTRIES.put(FoliagePlacerType.class, BuiltInRegistries.FOLIAGE_PLACER_TYPE);
        DEFAULT_REGISTRIES.put(TrunkPlacerType.class, BuiltInRegistries.TRUNK_PLACER_TYPE);
        DEFAULT_REGISTRIES.put(RootPlacerType.class, BuiltInRegistries.ROOT_PLACER_TYPE);
        DEFAULT_REGISTRIES.put(TreeDecoratorType.class, BuiltInRegistries.TREE_DECORATOR_TYPE);
        DEFAULT_REGISTRIES.put(FeatureSizeType.class, BuiltInRegistries.FEATURE_SIZE_TYPE);
        DEFAULT_REGISTRIES.put(StructurePoolElementType.class, BuiltInRegistries.STRUCTURE_POOL_ELEMENT);
        DEFAULT_REGISTRIES.put(DecoratedPotPattern.class, BuiltInRegistries.DECORATED_POT_PATTERN);
        DEFAULT_REGISTRIES.put(CreativeModeTab.class, BuiltInRegistries.CREATIVE_MODE_TAB);
        DEFAULT_REGISTRIES.put(CriterionTrigger.class, BuiltInRegistries.TRIGGER_TYPES);
        DEFAULT_REGISTRIES.put(NumberFormatType.class, BuiltInRegistries.NUMBER_FORMAT_TYPE);
        DEFAULT_REGISTRIES.put(DataComponentType.class, BuiltInRegistries.DATA_COMPONENT_TYPE);
        DEFAULT_REGISTRIES.put(GameRule.class, BuiltInRegistries.GAME_RULE);
        DEFAULT_REGISTRIES.put(DataComponentPredicate.Type.class, BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE);
        DEFAULT_REGISTRIES.put(MapDecorationType.class, BuiltInRegistries.MAP_DECORATION_TYPE);
        DEFAULT_REGISTRIES.put(ConsumeEffect.Type.class, BuiltInRegistries.CONSUME_EFFECT_TYPE);
        DEFAULT_REGISTRIES.put(RecipeDisplay.Type.class, BuiltInRegistries.RECIPE_DISPLAY);
        DEFAULT_REGISTRIES.put(SlotDisplay.Type.class, BuiltInRegistries.SLOT_DISPLAY);
        DEFAULT_REGISTRIES.put(RecipeBookCategory.class, BuiltInRegistries.RECIPE_BOOK_CATEGORY);
        DEFAULT_REGISTRIES.put(TicketType.class, BuiltInRegistries.TICKET_TYPE);
        DEFAULT_REGISTRIES.put(IncomingRpcMethod.class, BuiltInRegistries.INCOMING_RPC_METHOD);
        DEFAULT_REGISTRIES.put(OutgoingRpcMethod.class, BuiltInRegistries.OUTGOING_RPC_METHOD);
        DEFAULT_REGISTRIES.put(EnvironmentAttribute.class, BuiltInRegistries.ENVIRONMENT_ATTRIBUTE);
        DEFAULT_REGISTRIES.put(AttributeType.class, BuiltInRegistries.ATTRIBUTE_TYPE);
    }
}