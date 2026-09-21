package dev.amymialee.amymialib.modules.scribbler.scribbles;

import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

public class ScribbleBlock<T extends Block> extends Scribble<T> {
    private final HashMap<Variant, ScribbleBlock<? extends Block>> blocks = new HashMap<>();
    private @Nullable Supplier<Item.Properties> itemProperties;
    private @Nullable Scribble<BlockItem> item;

    @ApiStatus.Internal
    public ScribbleBlock(Scribbler scribbler, Supplier<Holder.Reference<T>> getter) {
        super(scribbler, getter);
        this.blocks.put(Variant.BLOCK, this);
    }

    @Override @ApiStatus.Internal
    public void run() {
        super.run();
        if (this.itemProperties == null) return;
        this.item = new Scribble<>(this.scribbler, () -> {
            var itemKey = ResourceKey.create(Registries.ITEM, this.key().identifier());
            var item = Registry.registerForHolder(BuiltInRegistries.ITEM, itemKey, new BlockItem(this.get(), this.itemProperties.get().setId(itemKey)));
            if (this.scribbler.creativeTab != null) CreativeModeTabEvents.modifyOutputEvent(this.scribbler.creativeTab).register(output -> output.accept(item.value()));
            return item;
        });
    }

    @Override
    public ScribbleBlock<T> translation(String translation) {
        super.translation(translation);
        for (var entry : this.blocks.entrySet()) entry.getValue().translation("%s %s".formatted(translation, entry.getKey().name));
        return this;
    }

    public @Nullable Scribble<BlockItem> item() {
        return this.item;
    }

    public ScribbleBlock<T> withItem() {
        return this.withItem(() -> new Item.Properties().useBlockDescriptionPrefix());
    }

    public ScribbleBlock<T> withItem(Supplier<Item.Properties> properties) {
        if (this.itemProperties != null) throw new IllegalStateException("Item properties already set");
        this.itemProperties = () -> properties.get().useBlockDescriptionPrefix();
        return this;
    }

    public @Nullable Block stairs() {
        return this.get(Variant.STAIRS);
    }

    public @Nullable Block slab() {
        return this.get(Variant.SLAB);
    }

    public @Nullable Block carpet() {
        return this.get(Variant.CARPET);
    }

    public @Nullable Block wall() {
        return this.get(Variant.WALL);
    }

    public @Nullable Block pane() {
        return this.get(Variant.PANE);
    }

    public @Nullable Block fence() {
        return this.get(Variant.FENCE);
    }

    public @Nullable Block fenceGate() {
        return this.get(Variant.FENCE_GATE);
    }

    public @Nullable Block pressurePlate() {
        return this.get(Variant.PRESSURE_PLATE);
    }

    public @Nullable Block button() {
        return this.get(Variant.BUTTON);
    }

    public ScribbleBlock<T> addStairs() {
        return this.add(Variant.STAIRS, p -> new StairBlock(this.get().defaultBlockState(), p), p -> p, null);
    }

    public ScribbleBlock<T> addSlab() {
        return this.add(Variant.SLAB, SlabBlock::new, p -> p, null);
    }

    public ScribbleBlock<T> addCarpet() {
        return this.add(Variant.CARPET, CarpetBlock::new, p -> p.strength(0.1F).pushReaction(PushReaction.POPPED), null);
    }

    public ScribbleBlock<T> addCarpet(String name) {
        return this.add(Variant.CARPET, CarpetBlock::new, p -> p.strength(0.1F).pushReaction(PushReaction.POPPED), name);
    }

    public ScribbleBlock<T> addWall() {
        return this.add(Variant.WALL, WallBlock::new, BlockBehaviour.Properties::forceSolidOn, null);
    }

    public ScribbleBlock<T> addPane() {
        return this.add(Variant.PANE, IronBarsBlock::new, BlockBehaviour.Properties::noOcclusion, null);
    }

    public ScribbleBlock<T> addPane(String name) {
        return this.add(Variant.PANE, IronBarsBlock::new, BlockBehaviour.Properties::noOcclusion, name);
    }

    public ScribbleBlock<T> addFence() {
        return this.add(Variant.FENCE, FenceBlock::new, p -> p, null);
    }

    public ScribbleBlock<T> addFenceGate(WoodType type) {
        return this.add(Variant.FENCE_GATE, p -> new FenceGateBlock(type, p), BlockBehaviour.Properties::forceSolidOn, null);
    }

    public ScribbleBlock<T> addPressurePlate(BlockSetType type) {
        return this.add(Variant.PRESSURE_PLATE, p -> new PressurePlateBlock(type, p), p -> p.forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.POPPED), null);
    }

    public ScribbleBlock<T> addButton(BlockSetType type, int ticksToStayPressed) {
        return this.add(Variant.BUTTON, p -> new ButtonBlock(type, ticksToStayPressed, p), p -> p.noCollision().strength(0.5F).pushReaction(PushReaction.POPPED), null);
    }

    public ScribbleBlock<T> stonelike() {
        this.addStairs();
        this.addSlab();
        this.addWall();
        return this;
    }

    public ScribbleBlock<T> stonelike(BlockSetType type, int ticksToStayPressed) {
        this.addPlateAndButton(type, ticksToStayPressed);
        return this.stonelike();
    }

    public ScribbleBlock<T> woodLike(WoodType wood, BlockSetType type, int ticksToStayPressed) {
        this.addStairs();
        this.addSlab();
        this.addFence();
        this.addFenceGate(wood);
        this.addPlateAndButton(type, ticksToStayPressed);
        return this;
    }

    public ScribbleBlock<T> addPlateAndButton(BlockSetType type, int ticksToStayPressed) {
        this.addPressurePlate(type);
        this.addButton(type, ticksToStayPressed);
        return this;
    }

    @ApiStatus.Internal
    private boolean has(Variant variant) {
        return this.blocks.containsKey(variant);
    }

    @ApiStatus.Internal
    private @Nullable Block get(Variant variant) {
        if (!this.has(variant)) return null;
        return this.blocks.get(variant).get();
    }

    @ApiStatus.Internal
    private ScribbleBlock<T> set(Variant variant, ScribbleBlock<Block> block) {
        this.blocks.put(variant, block);
        return this;
    }

    @ApiStatus.Internal
    private ScribbleBlock<T> add(Variant var, Function<BlockBehaviour.Properties, Block> function, Function<BlockBehaviour.Properties, BlockBehaviour.Properties> settings, @Nullable String nameOverride) {
        if (this.has(var)) {
            this.scribbler.logger.warn("Variant {} already exists in ScribbleBlock {}", var.name(), this.key().identifier());
            return this;
        }
        return this.set(var, new ScribbleBlock<>(this.scribbler, () -> {
            var blockKey = this.key();
            var id = this.scribbler.id("%s_%s".formatted(blockKey.identifier().getPath(), nameOverride != null ? nameOverride : var.name().toLowerCase(Locale.ROOT)));
            var key = ResourceKey.create(Registries.BLOCK, id);
            var block = function.apply(settings.apply(BlockBehaviour.Properties.ofFullCopy(this.get()).setId(key)));
            var holder = Registry.registerForHolder(BuiltInRegistries.BLOCK, key, block);
            if (this.itemProperties != null) {
                var itemKey = ResourceKey.create(Registries.ITEM, id);
                var item = Registry.register(BuiltInRegistries.ITEM, itemKey, new BlockItem(block, this.itemProperties.get().setId(itemKey)));
                if (this.scribbler.creativeTab != null) CreativeModeTabEvents.modifyOutputEvent(this.scribbler.creativeTab).register(output -> output.accept(item));
            }
            return holder;
        }));
    }

    /// All standard block variants that use the same base texture
    private enum Variant {
        BLOCK,
        STAIRS,
        SLAB,
        CARPET,
        WALL,
        PANE,
        FENCE,
        FENCE_GATE,
        PRESSURE_PLATE,
        BUTTON;

        public final String name;

        Variant() {
            this.name = this.name().charAt(0) + this.name().substring(1).toLowerCase(Locale.ROOT);
        }
    }
}