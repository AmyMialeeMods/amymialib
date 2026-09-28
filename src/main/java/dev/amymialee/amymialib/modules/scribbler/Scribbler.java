package dev.amymialee.amymialib.modules.scribbler;

import com.mojang.serialization.Codec;
import dev.amymialee.amymialib.modules.aspects.Aspect;
import dev.amymialee.amymialib.modules.aspects.AspectCategory;
import dev.amymialee.amymialib.modules.aspects.AspectLink;
import dev.amymialee.amymialib.modules.aspects.AspectType;
import dev.amymialee.amymialib.modules.dossier.data.DossierTranslations;
import dev.amymialee.amymialib.modules.scribbler.scribbles.*;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.numbers.NumberFormatType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.OutgoingRpcMethod;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.debug.DebugSubscription;
import net.minecraft.world.attribute.AttributeType;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Scribbler {
    private static final Map<Class<?>, Registry<?>> AUTO_REGISTRIES = new HashMap<>();
    public final String namespace;
    public final String modName;
    public final Logger logger;
    public final List<Object> dossierData = new ArrayList<>();
    public @Nullable ResourceKey<CreativeModeTab> creativeTab;

    public Scribbler(String namespace) {
        this.namespace = namespace;
        this.modName = FabricLoader.getInstance().getModContainer(namespace).orElseThrow().getMetadata().getName();
        this.logger = LoggerFactory.getLogger(this.modName + " " + FabricLoader.getInstance().getEnvironmentType());
        AutoQueue.add(this);
    }

    /**
     * Scans through a given package and will run relevant Annotated classes.
     *
     * @param packageName Package to scan
     */
    public Scribbler auto(String packageName) {
        AutoQueue.add(this, packageName);
        return this;
    }

    /**
     * Creates the primary Creative Tab. All items in the registry from here on will be added to this tab.
     * @param name Translated name for datagen
     * @param iconGenerator Supplier for the icon of the tab
     */
    public Scribbler tab(String name, Supplier<ItemStack> iconGenerator) {
        var translationKey = id(this.namespace).toLanguageKey("itemGroup");
        return this.tab(this.of(this.namespace, FabricCreativeModeTab.builder().title(Component.translatable(translationKey)).icon(iconGenerator).build()).translation(translationKey, name).key());
    }

    /**
     * Creates the primary Creative Tab. All items in the registry from here on will be added to this tab.
     * @param iconGenerator Supplier for the icon of the tab
     */
    public Scribbler tab(Supplier<ItemStack> iconGenerator) {
        var translationKey = id(this.namespace).toLanguageKey("itemGroup");
        return this.tab(this.of(this.namespace, FabricCreativeModeTab.builder().title(Component.translatable(translationKey)).icon(iconGenerator).build()).key());
    }

    /**
     * Creates the primary Creative Tab. All items in the registry from here on will be added to this tab.
     * Bifunction allows access to the builder for customisation of the tab.
     * @param name Translated name for en_us.json
     * @param access Access to the tab builder, with the translation key provided
     */
    public Scribbler tab(String name, BiFunction<CreativeModeTab.Builder, String, CreativeModeTab.Builder> access) {
        var translationKey = id(this.namespace).toLanguageKey("itemGroup");
        return this.tab(this.of(this.namespace, access.apply(FabricCreativeModeTab.builder().title(Component.translatable(translationKey)).icon(Items.DIAMOND::getDefaultInstance), translationKey).build()).translation(translationKey, name).key());
    }

    /**
     * Creates the primary Creative Tab. All items in the registry from here on will be added to this tab.
     * Bifunction allows access to the builder for customisation of the tab.
     * @param access Access to the tab builder, with the translation key provided
     */
    public Scribbler tab(BiFunction<CreativeModeTab.Builder, String, CreativeModeTab.Builder> access) {
        var translationKey = id(this.namespace).toLanguageKey("itemGroup");
        return this.tab(this.of(this.namespace, access.apply(FabricCreativeModeTab.builder().title(Component.translatable(translationKey)), translationKey).build()).key());
    }

    /**
     * Sets the primary Creative Tab. All items in the registry from here on will be added to this tab.
     * @param tab Tab to use
     */
    public Scribbler tab(ResourceKey<CreativeModeTab> tab) {
        this.creativeTab = tab;
        return this;
    }

    /**
     * Registers a tag.
     */
    public <T> ScribbleTag<T> tag(ResourceKey<Registry<T>> registry, String path) {
        return new ScribbleTag<>(this, registry, path);
    }

    /**
     * Creates a resource key.
     */
    public <T> ScribbleKey<T> key(ResourceKey<? extends Registry<T>> registryName, String path) {
        return new ScribbleKey<>(this, registryName, this.id(path));
    }

    /**
     * Creates a resource key.
     */
    public <T> ScribbleKey<T> key(ResourceKey<T> key) {
        return new ScribbleKey<>(this, key);
    }

    /**
     * Registers an entity type.
     * If the entity is a LivingEntity, you will need to register its attributes.
     */
    public <T extends Entity> ScribbleEntity<T> of(String path, EntityType.Builder<T> builder) {
        return new ScribbleEntity<>(this, (Registry<EntityType<T>>) (Object) BuiltInRegistries.ENTITY_TYPE, () -> {
            var key = ResourceKey.create(Registries.ENTITY_TYPE, id(path));
            return Registry.registerForHolder(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
        });
    }

    /**
     * Registers a simple block.
     */
    public ScribbleBlock of(String path, BlockBehaviour.Properties settings) {
        return this.of(path, Block::new, settings);
    }

    /**
     * Registers a simple block with a unique block class.
     */
    public ScribbleBlock of(String path, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties settings) {
        return new ScribbleBlock(this, () -> {
            var key = ResourceKey.create(Registries.BLOCK, this.id(path));
            return Registry.registerForHolder(BuiltInRegistries.BLOCK, key, function.apply(settings.setId(key)));
        });
    }

    /**
     * Registers a simple block with no item.
     */
    public Scribble<Block> ofItemless(String path, BlockBehaviour.Properties settings) {
        return this.ofItemless(path, Block::new, settings);
    }

    /**
     * Registers a simple block with a unique block class with no item.
     */
    public Scribble<Block> ofItemless(String path, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties settings) {
        return new Scribble<>(this, BuiltInRegistries.BLOCK, () -> {
            var key = ResourceKey.create(Registries.BLOCK, this.id(path));
            return Registry.registerForHolder(BuiltInRegistries.BLOCK, key, function.apply(settings.setId(key)));
        });
    }

    /**
     * Registers a simple block set.
     */
    public ScribbleBlockBatch batch(String path, BlockBehaviour.Properties settings) {
        return this.batch(path, Block::new, settings);
    }

    /**
     * Registers a simple block set with a unique base block class.
     * Other blocks in the set will still use basic blocks.
     */
    public ScribbleBlockBatch batch(String path, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties settings) {
        return new ScribbleBlockBatch(this, path, function, settings);
    }

    /**
     * Registers a simple item.
     */
    public Scribble<Item> of(String path, Item.Properties settings) {
        return this.of(path, Item::new, settings);
    }

    /**
     * Registers a simple item with a unique item class.
     */
    public <T extends Item> Scribble<Item> of(String path, Function<Item.Properties, T> function, Item.Properties settings) {
        return new Scribble<>(this, BuiltInRegistries.ITEM, () -> {
            var key = ResourceKey.create(Registries.ITEM, id(path));
            return Registry.registerForHolder(BuiltInRegistries.ITEM, key, function.apply(settings.setId(key)));
        });
    }

    /**
     * Registers a simple banner pattern.
     */
    public ScribbleBannerPattern bannerPattern(String path) {
        return bannerPattern(path, new Item.Properties());
    }

    /**
     * Registers a simple banner pattern.
     */
    public ScribbleBannerPattern bannerPattern(String path, Item.Properties settings) {
        var ref = new Object() { ScribbleBannerPattern ref = null; };
        var pattern = new ScribbleBannerPattern(this, path, () -> {
            var key = ResourceKey.create(Registries.ITEM, id("%s_banner_pattern".formatted(path)));
            return Registry.registerForHolder(BuiltInRegistries.ITEM, key, new Item(settings.delayedComponent(DataComponents.PROVIDES_BANNER_PATTERNS, context -> context.getOrThrow(ref.ref.patternTag.get())).stacksTo(1).setId(key)));
        });
        ref.ref = pattern;
        return pattern;
    }

    /**
     * Registers a simple music disc.
     */
    public ScribbleMusicDisc musicDisc(String path, Holder.Reference<SoundEvent> song, int lengthInSeconds, int comparatorOutput) {
        return musicDisc(path, song, lengthInSeconds, comparatorOutput, new Item.Properties());
    }

    /**
     * Registers a simple music disc.
     */
    public ScribbleMusicDisc musicDisc(String path, Holder.Reference<SoundEvent> song, int lengthInSeconds, int comparatorOutput, Item.Properties settings) {
        var ref = new Object() { ScribbleMusicDisc ref = null; };
        var disc = new ScribbleMusicDisc(this, path, song, lengthInSeconds, comparatorOutput, () -> {
            var key = ResourceKey.create(Registries.ITEM, id("music_disc_%s".formatted(path)));
            return Registry.registerForHolder(BuiltInRegistries.ITEM, key, new Item(settings.stacksTo(1).jukeboxPlayable(ref.ref.songKey.get()).setId(key)));
        });
        ref.ref = disc;
        return disc;
    }

    /**
     * Registers a variable range sound event.
     */
    public Scribble<SoundEvent> of(String path) {
        return new Scribble<>(this, BuiltInRegistries.SOUND_EVENT, () -> {
            var key = ResourceKey.create(Registries.SOUND_EVENT, id(path));
            return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, key, SoundEvent.createVariableRangeEvent(id(path)));
        });
    }

    /**
     * Registers a fixed range sound event.
     */
    public Scribble<SoundEvent> of(String path, float range) {
        return new Scribble<>(this, BuiltInRegistries.SOUND_EVENT, () -> {
            var key = ResourceKey.create(Registries.SOUND_EVENT, id(path));
            return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, key, SoundEvent.createFixedRangeEvent(id(path), range));
        });
    }

    /**
     * Registers an object to the relevant registry if found within {@link AUTO_REGISTRIES}.<br>
     * If no fitting registry is found, it will throw an exception.
     */
    public @SuppressWarnings("unchecked") <T> Scribble<T> of(String path, T thing) {
        for (var entry : AUTO_REGISTRIES.entrySet()) {
            if (!entry.getKey().isInstance(thing)) continue;
            var registry = (Registry<T>) entry.getValue();
            return new Scribble<>(this, registry, () -> {
                var key = ResourceKey.create(registry.key(), id(path));
                return Registry.registerForHolder(registry, key, thing);
            });
        }
        throw new IllegalStateException("Failed to register %s with the %s scribbler - no AUTO_REGISTRIES matched %s.".formatted(path, this.modName, thing.getClass().getName()));
    }

    /**
     * Prepares a translation to en_us.json and returns the key.
     */
    public String translation(String key, String translation) {
        this.dossierData.add((DossierTranslations) (_, _, builder) -> builder.add(key, translation));
        return key;
    }

    public AspectLink.AspectLinkBuilder link(String path, String link) {
        return AspectLink.of(id(path), link);
    }

    public AspectCategory.AspectCategoryBuilder category(String path) {
        return AspectCategory.of(id(path));
    }

    public <T> Aspect.AspectBuilder<T> config(String path, AspectType<T> type) {
        return Aspect.config(id(path), type);
    }

    public <T> Aspect.AspectBuilder<T> client(String path, AspectType<T> type) {
        return Aspect.client(id(path), type);
    }

    public Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(this.namespace, path);
    }

    private static <T> void addAutoRegistry(Class<? super T> clazz, Registry<T> registry) {
        if (AUTO_REGISTRIES.containsKey(clazz)) {
            AUTO_REGISTRIES.put(clazz, null);
        } else {
            AUTO_REGISTRIES.put(clazz, registry);
        }
    }

    static {
        addAutoRegistry(GameEvent.class, BuiltInRegistries.GAME_EVENT);
        addAutoRegistry(SoundEvent.class, BuiltInRegistries.SOUND_EVENT);
        addAutoRegistry(Fluid.class, BuiltInRegistries.FLUID);
        addAutoRegistry(MobEffect.class, BuiltInRegistries.MOB_EFFECT);
        addAutoRegistry(DebugSubscription.class, BuiltInRegistries.DEBUG_SUBSCRIPTION);
        addAutoRegistry(Potion.class, BuiltInRegistries.POTION);
        addAutoRegistry(ParticleType.class, BuiltInRegistries.PARTICLE_TYPE);
        addAutoRegistry(BlockEntityType.class, BuiltInRegistries.BLOCK_ENTITY_TYPE);
        addAutoRegistry(Identifier.class, BuiltInRegistries.CUSTOM_STAT);
        addAutoRegistry(ChunkStatus.class, BuiltInRegistries.CHUNK_STATUS);
        addAutoRegistry(RuleTestType.class, BuiltInRegistries.RULE_TEST);
        addAutoRegistry(RuleBlockEntityModifierType.class, BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER);
        addAutoRegistry(PosRuleTestType.class, BuiltInRegistries.POS_RULE_TEST);
        addAutoRegistry(MenuType.class, BuiltInRegistries.MENU);
        addAutoRegistry(RecipeType.class, BuiltInRegistries.RECIPE_TYPE);
        addAutoRegistry(RecipeSerializer.class, BuiltInRegistries.RECIPE_SERIALIZER);
        addAutoRegistry(Attribute.class, BuiltInRegistries.ATTRIBUTE);
        addAutoRegistry(PositionSourceType.class, BuiltInRegistries.POSITION_SOURCE_TYPE);
        addAutoRegistry(ArgumentTypeInfo.class, BuiltInRegistries.COMMAND_ARGUMENT_TYPE);
        addAutoRegistry(StatType.class, BuiltInRegistries.STAT_TYPE);
        addAutoRegistry(VillagerType.class, BuiltInRegistries.VILLAGER_TYPE);
        addAutoRegistry(VillagerProfession.class, BuiltInRegistries.VILLAGER_PROFESSION);
        addAutoRegistry(PoiType.class, BuiltInRegistries.POINT_OF_INTEREST_TYPE);
        addAutoRegistry(MemoryModuleType.class, BuiltInRegistries.MEMORY_MODULE_TYPE);
        addAutoRegistry(SensorType.class, BuiltInRegistries.SENSOR_TYPE);
        addAutoRegistry(Activity.class, BuiltInRegistries.ACTIVITY);
        addAutoRegistry(HeightProviderType.class, BuiltInRegistries.HEIGHT_PROVIDER_TYPE);
        addAutoRegistry(BlockPredicateType.class, BuiltInRegistries.BLOCK_PREDICATE_TYPE);
        addAutoRegistry(StructurePieceType.class, BuiltInRegistries.STRUCTURE_PIECE);
        addAutoRegistry(StructureType.class, BuiltInRegistries.STRUCTURE_TYPE);
        addAutoRegistry(FoliagePlacerType.class, BuiltInRegistries.FOLIAGE_PLACER_TYPE);
        addAutoRegistry(TrunkPlacerType.class, BuiltInRegistries.TRUNK_PLACER_TYPE);
        addAutoRegistry(RootPlacerType.class, BuiltInRegistries.ROOT_PLACER_TYPE);
        addAutoRegistry(TreeDecoratorType.class, BuiltInRegistries.TREE_DECORATOR_TYPE);
        addAutoRegistry(FeatureSizeType.class, BuiltInRegistries.FEATURE_SIZE_TYPE);
        addAutoRegistry(StructurePoolElementType.class, BuiltInRegistries.STRUCTURE_POOL_ELEMENT);
        addAutoRegistry(CreativeModeTab.class, BuiltInRegistries.CREATIVE_MODE_TAB);
        addAutoRegistry(CriterionTrigger.class, BuiltInRegistries.TRIGGER_TYPES);
        addAutoRegistry(NumberFormatType.class, BuiltInRegistries.NUMBER_FORMAT_TYPE);
        addAutoRegistry(DataComponentType.class, BuiltInRegistries.DATA_COMPONENT_TYPE);
        addAutoRegistry(GameRule.class, BuiltInRegistries.GAME_RULE);
        addAutoRegistry(Codec.class, BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE);
        addAutoRegistry(DataComponentPredicate.Type.class, BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE);
        addAutoRegistry(MapDecorationType.class, BuiltInRegistries.MAP_DECORATION_TYPE);
        addAutoRegistry(ConsumeEffect.Type.class, BuiltInRegistries.CONSUME_EFFECT_TYPE);
        addAutoRegistry(RecipeDisplay.Type.class, BuiltInRegistries.RECIPE_DISPLAY);
        addAutoRegistry(SlotDisplay.Type.class, BuiltInRegistries.SLOT_DISPLAY);
        addAutoRegistry(RecipeBookCategory.class, BuiltInRegistries.RECIPE_BOOK_CATEGORY);
        addAutoRegistry(TicketType.class, BuiltInRegistries.TICKET_TYPE);
        addAutoRegistry(IncomingRpcMethod.class, BuiltInRegistries.INCOMING_RPC_METHOD);
        addAutoRegistry(OutgoingRpcMethod.class, BuiltInRegistries.OUTGOING_RPC_METHOD);
        addAutoRegistry(EnvironmentAttribute.class, BuiltInRegistries.ENVIRONMENT_ATTRIBUTE);
        addAutoRegistry(AttributeType.class, BuiltInRegistries.ATTRIBUTE_TYPE);
        addAutoRegistry(ContextKeySet.class, BuiltInRegistries.CONTEXT_KEY_SET);
        addAutoRegistry(Consumer.class, BuiltInRegistries.TEST_FUNCTION);
    }
}