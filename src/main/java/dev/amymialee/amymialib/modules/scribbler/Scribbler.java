package dev.amymialee.amymialib.modules.scribbler;

import com.mojang.serialization.Codec;
import dev.amymialee.amymialib.modules.aspects.Aspect;
import dev.amymialee.amymialib.modules.aspects.AspectCategory;
import dev.amymialee.amymialib.modules.aspects.AspectLink;
import dev.amymialee.amymialib.modules.aspects.AspectType;
import dev.amymialee.amymialib.modules.dossier.DossierRegistries;
import dev.amymialee.amymialib.modules.dossier.data.DossierDynamic;
import dev.amymialee.amymialib.modules.dossier.data.DossierRecipes;
import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.dossier.data.DossierTranslations;
import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTaggable;
import dev.amymialee.amymialib.modules.scribbler.auto.Decipher;
import dev.amymialee.amymialib.modules.scribbler.helpers.BannerHelper;
import dev.amymialee.amymialib.modules.scribbler.helpers.BlockBatch;
import dev.amymialee.amymialib.modules.scribbler.helpers.DiscHelper;
import dev.amymialee.amymialib.modules.scribbler.helpers.MaterialHelper;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicate;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormatType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.jsonrpc.IncomingRpcMethod;
import net.minecraft.server.jsonrpc.OutgoingRpcMethod;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.util.debug.DebugSubscription;
import net.minecraft.world.attribute.AttributeType;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BannerPattern;
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
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Scribbler {
    private static final Map<Class<?>, Registry<?>> AUTO_REGISTRIES = new HashMap<>();
    public static Set<String> COMPLETED_KEYS = new HashSet<>();
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
     * @param packageName Package to scan
     */
    public Scribbler auto(String packageName) {
        AutoQueue.add(this, packageName);
        return this;
    }

    /**
     * Sets the primary Creative Tab. All items in the registry from here on will be added to this tab.
     */
    public Scribbler tab(ResourceKey<CreativeModeTab> tab) {
        this.creativeTab = tab;
        return this;
    }

    /**
     * Creates the primary Creative Tab. All items in the registry from here on will be added to this tab.
     * @param icon Supplier for the icon of the tab
     */
    public Scribbler tab(Supplier<ItemStack> icon) {
        return this.tab(Registry.registerForHolder(BuiltInRegistries.CREATIVE_MODE_TAB, id(this.namespace), FabricCreativeModeTab.builder().title(Component.translatable(id(this.namespace).toLanguageKey())).icon(icon).build()).key());
    }

    /**
     * Creates the primary Creative Tab. All items in the registry from here on will be added to this tab.
     * @param access Access to the tab builder
     */
    public Scribbler tab(Function<CreativeModeTab.Builder, CreativeModeTab.Builder> access) {
        return this.tab(Registry.registerForHolder(BuiltInRegistries.CREATIVE_MODE_TAB, id(this.namespace), access.apply(FabricCreativeModeTab.builder().title(Component.translatable(id(this.namespace).toLanguageKey("itemGroup")))).build()).key());
    }

    /**
     * Registers a tag.
     */
    public <T> TagKey<T> tag(ResourceKey<Registry<T>> reg, String path) {
        return TagKey.create(reg, id(path));
    }

    /**
     * Creates a resource key.
     */
    public <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> reg, String path) {
        return ResourceKey.create(reg, id(path));
    }

    /**
     * Registers a variable range sound event.
     */
    public Holder.Reference<SoundEvent> of(String path) {
        var id = id(path);
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, ResourceKey.create(Registries.SOUND_EVENT, id), SoundEvent.createVariableRangeEvent(id));
    }

    /**
     * Registers a fixed range sound event.
     */
    public Holder.Reference<SoundEvent> of(String path, float range) {
        var id = id(path);
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, ResourceKey.create(Registries.SOUND_EVENT, id), SoundEvent.createFixedRangeEvent(id, range));
    }

    /**
     * Registers a simple block.
     */
    public Block of(String path, BlockBehaviour.Properties properties) {
        return this.of(path, Block::new, properties);
    }

    /**
     * Registers a simple block.
     */
    public Block of(String path, BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        return this.of(path, Block::new, properties, itemProperties);
    }

    /**
     * Registers a block.
     */
    public Block of(String path, Function<BlockBehaviour.Properties, Block> func, BlockBehaviour.Properties properties) {
        return of(path, func, properties, new Item.Properties());
    }

    /**
     * Registers a block.
     */
    public Block of(String path, Function<BlockBehaviour.Properties, Block> func, BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        var key = key(Registries.BLOCK, path);
        var block = Registry.register(BuiltInRegistries.BLOCK, key, func.apply(properties.setId(key)));
        if (itemProperties != null) this.of(path, p -> new BlockItem(block, p), itemProperties.useBlockDescriptionPrefix());
        return block;
    }

    /**
     * Registers a simple item.
     */
    public Item of(String path, Item.Properties properties) {
        return this.of(path, Item::new, properties);
    }

    /**
     * Registers an item.
     */
    public Item of(String path, Function<Item.Properties, Item> func, Item.Properties properties) {
        var key = key(Registries.ITEM, path);
        var item = Registry.register(BuiltInRegistries.ITEM, key, func.apply(properties.setId(key)));
        if (creativeTab != null) CreativeModeTabEvents.modifyOutputEvent(creativeTab).register(output -> output.accept(item));
        return item;
    }

    /**
     * Registers an entity type.
     */
    public <T extends Entity> EntityType<T> of(String path, EntityType.Builder<T> builder) {
        var key = ResourceKey.create(Registries.ENTITY_TYPE, id(path));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
    }

    /**
     * Registers an entity type with attributes and spawn egg.
     */
    public <T extends Entity> EntityType<T> of(String path, EntityType.Builder<T> builder, AttributeSupplier.Builder attributes) {
        return of(path, builder, attributes, new Item.Properties());
    }

    /**
     * Registers an entity type with attributes and spawn egg.
     */
    public @SuppressWarnings("unchecked") <T extends Entity> EntityType<T> of(String path, EntityType.Builder<T> builder, AttributeSupplier.Builder attributes, Item.Properties properties) {
        var key = ResourceKey.create(Registries.ENTITY_TYPE, id(path));
        var entity = Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
        try {
            FabricDefaultAttributeRegistry.register((EntityType<? extends LivingEntity>) entity, attributes);
        } catch (ClassCastException _) {
            logger.error("Tried to register entity attributes to a non-living entity {}", key);
        }
        of("%s_%s".formatted(key.identifier().getPath(), "spawn_egg"), SpawnEggItem::new, properties.spawnEgg(entity));
        return entity;
    }

    /**
     * Registers a simple banner pattern.
     */
    public BannerHelper bannerPattern(String path, Item.Properties properties) {
        var patternTag = tag(Registries.BANNER_PATTERN, "pattern_item/%s".formatted(path));
        var patternKey = key(Registries.BANNER_PATTERN, path);
        var itemPath = "%s_banner_pattern".formatted(path);
        this.dossierData.add((DossierDynamic) (provider, lookup, entries) -> entries.add((HolderLookup.RegistryLookup<BannerPattern>) entries.getLookup(patternKey.registryKey()), patternKey));
        this.dossierData.add((DossierRegistries) (registryBuilder) -> registryBuilder.add(Registries.BANNER_PATTERN, registerable -> registerable.register(patternKey, new BannerPattern(patternKey.identifier(), "block.%s.banner.%s".formatted(patternKey.identifier().getNamespace(), patternKey.identifier().getPath())))));
        this.dossierData.add((DossierTags) (provider) -> {
            provider.tag(patternTag, patternKey);
            provider.tag(ItemTags.LOOM_PATTERNS, ResourceKey.create(Registries.ITEM, id(itemPath)));
        });
        return new BannerHelper(of(itemPath, properties.delayedComponent(DataComponents.PROVIDES_BANNER_PATTERNS, context -> context.getOrThrow(patternTag)).stacksTo(1)), patternTag, patternKey);
    }

    /**
     * Registers a simple music disc.
     */
    public DiscHelper musicDisc(String path, int lengthInSeconds, int comparatorOutput, Item.Properties settings) {
        var songKey = key(Registries.JUKEBOX_SONG, path);
        var song = of(path);
        var itemPath = "music_disc_%s".formatted(path);
        var key = ResourceKey.create(Registries.ITEM, id(itemPath));
        this.dossierData.add((DossierDynamic) (provider, lookup, entries) -> entries.add((HolderLookup.RegistryLookup<JukeboxSong>) entries.getLookup(songKey.registryKey()), songKey));
        this.dossierData.add((DossierRegistries) (registryBuilder) -> registryBuilder.add(Registries.JUKEBOX_SONG, registerable -> registerable.register(songKey, new JukeboxSong(song, Component.translatable(Util.makeDescriptionId("jukebox_song", songKey.identifier())), lengthInSeconds, comparatorOutput))));
        return new DiscHelper(of(itemPath, settings.stacksTo(1).jukeboxPlayable(songKey)), songKey, song, lengthInSeconds, comparatorOutput);
    }

    /**
     * Registers a trim material.
     */
    public ResourceKey<TrimMaterial> trimMaterial(String path, Consumer<MutableComponent> description) {
        var key = key(Registries.TRIM_MATERIAL, path);
        var translation = Component.translatable(Util.makeDescriptionId(key.registry().getPath(), key.identifier()));
        description.accept(translation);
        this.dossierData.add((DossierDynamic) (provider, lookup, entries) -> entries.add((HolderLookup.RegistryLookup<TrimMaterial>) entries.getLookup(key.registryKey()), key));
        this.dossierData.add((DossierRegistries) (registryBuilder) -> registryBuilder.add(Registries.TRIM_MATERIAL, registerable -> registerable.register(key, new TrimMaterial(id("trim/" + path), translation))));
        return key;
    }

    /**
     * Registers a trim pattern.
     */
    public Item trimPattern(String path, Item.Properties properties, Consumer<MutableComponent> description, boolean decal) {
        var itemKey = ResourceKey.create(Registries.ITEM, id(path + "_armor_trim"));
        var item = of(path + "_armor_trim", SmithingTemplateItem::createArmorTrimTemplate, properties);
        var key = key(Registries.TRIM_PATTERN, path);
        var translation = Component.translatable(Util.makeDescriptionId(key.registry().getPath(), key.identifier()));
        description.accept(translation);
        this.dossierData.add((DossierDynamic) (provider, lookup, entries) -> entries.add((HolderLookup.RegistryLookup<TrimPattern>) entries.getLookup(key.registryKey()), key));
        this.dossierData.add((DossierRegistries) (registryBuilder) -> registryBuilder.add(Registries.TRIM_PATTERN, registerable -> registerable.register(key, new TrimPattern(key.identifier(), translation, decal))));
        this.dossierData.add((DossierRecipes.Inverse) (provider, data) -> provider.trimSmithing(item, key, ResourceKey.create(Registries.RECIPE, itemKey.identifier().withSuffix("_smithing_trimming"))));
        this.translate(key, WordUtils.capitalize(path.replace('_', ' ')));
        return item;
    }

    /**
     * Registers a tool/armour material.
     */
    public MaterialHelper material(String path, TagKey<Block> incorrectBlocksForDrops, int toolDurability, float speed, float damageBonus, int enchantValue,
            int armorDurabilityMultiplier, int bootDefence, int legDefence, int chestDefence, int helmDefence, int bodyDefence, Holder<SoundEvent> equipSound, float toughness, float knockbackResistance) {
        var materials = tag(Registries.ITEM, "%s_materials".formatted(path));
        var tool = new ToolMaterial(incorrectBlocksForDrops, toolDurability, speed, damageBonus, enchantValue, materials);
        var armor = new ArmorMaterial(armorDurabilityMultiplier, ArmorMaterials.makeDefense(bootDefence, legDefence, chestDefence, helmDefence, bodyDefence), enchantValue, equipSound, toughness, knockbackResistance, materials, key(EquipmentAssets.ROOT_ID, path));
        return new MaterialHelper(materials, tool, armor);
    }

    /**
     * Creates a simple block set.
     */
    public BlockBatch batch(String path, Block block) {
        return new BlockBatch(this, block);
    }

    /**
     * Creates a simple block set.
     */
    public BlockBatch batch(String path, BlockBehaviour.Properties properties) {
        return new BlockBatch(this, of(path, properties));
    }

    /**
     * Creates a block set.
     */
    public BlockBatch batch(String path, Function<BlockBehaviour.Properties, Block> function, BlockBehaviour.Properties properties) {
        return new BlockBatch(this, of(path, function, properties));
    }

    /**
     * Registers an object to the relevant registry if found within {@link AUTO_REGISTRIES}.<br>
     * If no fitting registry is found, it will throw an exception.
     */
    public @SuppressWarnings("unchecked") <T> T of(String path, T thing) {
        for (var entry : AUTO_REGISTRIES.entrySet()) {
            if (!entry.getKey().isInstance(thing)) continue;
            var registry = (Registry<T>) entry.getValue();
            return Registry.register(registry, ResourceKey.create(registry.key(), id(path)), thing);
        }
        throw new IllegalStateException("Failed to register %s with the %s scribbler - no AUTO_REGISTRIES matched %s.".formatted(path, this.modName, thing.getClass().getName()));
    }

    /**
     * Plain registration.
     */
    public <T> T of(String path, Registry<T> registry, T thing) {
        return Registry.register(registry, ResourceKey.create(registry.key(), id(path)), thing);
    }

    /**
     * Prepares a translation to en_us.json and returns the key.
     */
    public String translate(String key, String translation) {
        if (COMPLETED_KEYS.contains(key)) {
            this.logger.warn("Key {} has already been translated.", key);
            return key;
        }
        COMPLETED_KEYS.add(key);
        this.dossierData.add((DossierTranslations) (_, _, builder) -> builder.add(key, translation));
        return key;
    }

    /**
     * Prepares translations to en_us.json for a given object.
     */
    public void translate(Field f, Decipher cipher) throws IllegalAccessException {
        Optional.ofNullable(cipher.dyeTranslation()).ifPresent(d -> {
            if (d.length == 0) return;
            if (d.length != 2) {
                this.logger.error("Unable to translate field {}.{}, as the dye translation array is not of length 2.", f.getDeclaringClass().getName(), f.getName());
                throw new RuntimeException("Unable to translate field %s.%s, as the dye translation array is not of length 2.", null, true, false) {};
            }
            for (var dye : DyeColor.values()) this.translate(d[0].formatted(dye.getName()), d[1].formatted(WordUtils.capitalize(dye.getName().replace('_', ' '))));
        });
        var field = f.get(null);
        var translation = cipher.value();
        if (field instanceof Scribbler scribbler) {
            this.translate(id(this.namespace).toLanguageKey(), translation);
        } else if (field instanceof EntityType<?> entityType) {
            this.dossierData.add((DossierTranslations) (_, _, builder) -> builder.add(entityType, translation));
            Optional.ofNullable(entityType.amy$asItem()).ifPresent(e -> this.dossierData.add((DossierTranslations) (_, _, builder) -> builder.add(e, "%s %s".formatted(translation, "Spawn Egg"))));
        } else if (field instanceof Holder.Reference<?> holder) {
            if (holder.value() instanceof SoundEvent sound) {
                this.translate("subtitles." + sound.location().toShortLanguageKey().replace('/', '.'), translation);
                return;
            }
            this.translate(Util.makeDescriptionId(holder.key().registry().toShortLanguageKey(), holder.key().identifier()), translation);
            if (holder.value() instanceof EntityType<?> entityType) Optional.ofNullable(entityType.amy$asItem()).ifPresent(e -> this.dossierData.add((DossierTranslations) (_, _, builder) -> builder.add(e, "%s %s".formatted(translation, "Spawn Egg"))));
        } else if (field instanceof DiscHelper(var item, var key, var song, var lengthInSeconds, var comparatorOutput)) {
            this.dossierData.add((DossierTranslations) (_, _, builder) -> builder.add(Util.makeDescriptionId(key.registry().toShortLanguageKey(), key.identifier()), translation));
            var itemKey = item.builtInRegistryHolder().key();
            this.translate(Util.makeDescriptionId(itemKey.registry().toShortLanguageKey(), itemKey.identifier()), "Music Disc");
        } else if (field instanceof BannerHelper(var item, var tag, var key)) {
            this.translate(Util.makeDescriptionId("tag.%s".formatted(tag.registry().identifier().getPath()), tag.location()), "%s Banner Patterns".formatted(translation));
            for (var dye : DyeColor.values()) this.translate("block.%s.banner.%s.%s".formatted(key.identifier().getNamespace(), key.identifier().getPath(), dye.getName()), "%s %s".formatted(WordUtils.capitalize(dye.getName().replace('_', ' ')), translation));
            var itemKey = item.builtInRegistryHolder().key();
            this.translate(Util.makeDescriptionId(itemKey.registry().toShortLanguageKey(), itemKey.identifier()), "%s Banner Pattern".formatted(translation));
        } else if (field instanceof BlockBatch batch) {
            batch.forEach((v, b) -> {
                var key = batch.get(v).builtInRegistryHolder().key();
                this.translate(Util.makeDescriptionId(key.registry().toShortLanguageKey(), key.identifier()), v == BlockBatch.Variant.BLOCK ? translation : "%s %s".formatted(translation, WordUtils.capitalizeFully(v.name.replace('_', ' '))));
            });
        } else if (field instanceof SoundEvent sound) {
            this.translate("subtitles." + sound.location().toShortLanguageKey().replace('/', '.'), translation);
        } else if (field instanceof SoundType type) {
            this.translate("subtitles." + type.getBreakSound().location().toShortLanguageKey().replace('/', '.'), "Block broken");
            this.translate("subtitles." + type.getStepSound().location().toShortLanguageKey().replace('/', '.'), "Footsteps");
            this.translate("subtitles." + type.getPlaceSound().location().toShortLanguageKey().replace('/', '.'), "Block placed");
            this.translate("subtitles." + type.getHitSound().location().toShortLanguageKey().replace('/', '.'), "Block breaking");
            this.translate("subtitles." + type.getFallSound().location().toShortLanguageKey().replace('/', '.'), "Something falls on a block");
        } else if (field instanceof AmyMialibTaggable taggable) {
            for (var tag : taggable.amymialib$tags()) this.translate(tag.getTranslationKey(), translation);
            for (var key : taggable.amymialib$keys()) this.translate(Util.makeDescriptionId(key.registry().toShortLanguageKey(), key.identifier()), translation);
        }
    }

    /**
     * Prepares translations to en_us.json for a given key.
     */
    public void translate(ResourceKey key, String translation) {
        this.translate(Util.makeDescriptionId(key.registry().toShortLanguageKey(), key.identifier()), translation);
    }

    /**
     * Prepares translations to en_us.json for a given tag.
     */
    public void translate(TagKey tag, String translation) {
        this.translate(tag.getTranslationKey(), translation);
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

    /**
     * Creates an identifier in the namespace of this scribbler.
     */
    public Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(this.namespace, path);
    }

    /**
     * Used to initialise the list of Auto Registries.
     * This ensures that all the classes match the registries, between updates.
     */
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