package dev.amymialee.amymialib.modules.scribbler.helpers;

import dev.amymialee.amymialib.modules.scribbler.Scribbler;
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BlockBatch {
    public final Scribbler scribbler;
    private final HashMap<Variant, Block> blocks = new HashMap<>();
    private @Nullable Supplier<Item.Properties> itemProperties = () -> new Item.Properties().useBlockDescriptionPrefix();

    @ApiStatus.Internal
    public BlockBatch(Scribbler scribbler, Block block) {
        this.scribbler = scribbler;
        this.blocks.put(Variant.BLOCK, block);
    }

    public BlockBatch forEach(Consumer<Block> consumer) {
        for (var block : this.blocks.values()) consumer.accept(block);
        return this;
    }

    public BlockBatch setItem(Supplier<Item.Properties> properties) {
        if (this.itemProperties != null) throw new IllegalStateException("Item properties already set");
        this.itemProperties = () -> properties.get().useBlockDescriptionPrefix();
        return this;
    }

    public Block get() {
        return Objects.requireNonNull(this.get(Variant.BLOCK));
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

    public @Nullable Item getItem() {
        return this.getItem(Variant.BLOCK);
    }

    public @Nullable Item stairsItem() {
        return this.getItem(Variant.STAIRS);
    }

    public @Nullable Item slabItem() {
        return this.getItem(Variant.SLAB);
    }

    public @Nullable Item carpetItem() {
        return this.getItem(Variant.CARPET);
    }

    public @Nullable Item wallItem() {
        return this.getItem(Variant.WALL);
    }

    public @Nullable Item paneItem() {
        return this.getItem(Variant.PANE);
    }

    public @Nullable Item fenceItem() {
        return this.getItem(Variant.FENCE);
    }

    public @Nullable Item fenceGateItem() {
        return this.getItem(Variant.FENCE_GATE);
    }

    public @Nullable Item pressurePlateItem() {
        return this.getItem(Variant.PRESSURE_PLATE);
    }

    public @Nullable Item buttonItem() {
        return this.getItem(Variant.BUTTON);
    }

    public BlockBatch setStairs(Block existing) {
        this.blocks.put(Variant.STAIRS, existing);
        return this;
    }

    public BlockBatch setSlab(Block existing) {
        this.blocks.put(Variant.SLAB, existing);
        return this;
    }

    public BlockBatch setCarpet(Block existing) {
        this.blocks.put(Variant.CARPET, existing);
        return this;
    }

    public BlockBatch setWall(Block existing) {
        this.blocks.put(Variant.WALL, existing);
        return this;
    }

    public BlockBatch setPane(Block existing) {
        this.blocks.put(Variant.PANE, existing);
        return this;
    }

    public BlockBatch setFence(Block existing) {
        this.blocks.put(Variant.FENCE, existing);
        return this;
    }

    public BlockBatch setFenceGate(Block existing) {
        this.blocks.put(Variant.FENCE_GATE, existing);
        return this;
    }

    public BlockBatch setPressurePlate(Block existing) {
        this.blocks.put(Variant.PRESSURE_PLATE, existing);
        return this;
    }

    public BlockBatch setButton(Block existing) {
        this.blocks.put(Variant.BUTTON, existing);
        return this;
    }

    public BlockBatch addStairs() {
        return this.add(Variant.STAIRS, p -> new StairBlock(this.get().defaultBlockState(), p));
    }

    public BlockBatch addSlab() {
        return this.add(Variant.SLAB, SlabBlock::new);
    }

    public BlockBatch addCarpet() {
        return this.add(Variant.CARPET, CarpetBlock::new, p -> p.strength(0.1F).pushReaction(PushReaction.POPPED));
    }

    public BlockBatch addWall() {
        return this.add(Variant.WALL, WallBlock::new, BlockBehaviour.Properties::forceSolidOn);
    }

    public BlockBatch addPane() {
        return this.add(Variant.PANE, IronBarsBlock::new, BlockBehaviour.Properties::noOcclusion);
    }

    public BlockBatch addFence() {
        return this.add(Variant.FENCE, FenceBlock::new);
    }

    public BlockBatch addFenceGate(WoodType type) {
        return this.add(Variant.FENCE_GATE, p -> new FenceGateBlock(type, p), BlockBehaviour.Properties::forceSolidOn);
    }

    public BlockBatch addPressurePlate(BlockSetType type) {
        return this.add(Variant.PRESSURE_PLATE, p -> new PressurePlateBlock(type, p), p -> p.forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.POPPED));
    }

    public BlockBatch addButton(BlockSetType type, int ticksToStayPressed) {
        return this.add(Variant.BUTTON, p -> new ButtonBlock(type, ticksToStayPressed, p), p -> p.noCollision().strength(0.5F).pushReaction(PushReaction.POPPED));
    }

    public BlockBatch stonelike() {
        this.addStairs();
        this.addSlab();
        this.addWall();
        return this;
    }

    public BlockBatch stonelike(BlockSetType type, int ticksToStayPressed) {
        this.addPlateAndButton(type, ticksToStayPressed);
        return this.stonelike();
    }

    public BlockBatch woodLike(WoodType wood, BlockSetType type, int ticksToStayPressed) {
        this.addStairs();
        this.addSlab();
        this.addFence();
        this.addFenceGate(wood);
        this.addPlateAndButton(type, ticksToStayPressed);
        return this;
    }

    public BlockBatch addPlateAndButton(BlockSetType type, int ticksToStayPressed) {
        this.addPressurePlate(type);
        this.addButton(type, ticksToStayPressed);
        return this;
    }

    @ApiStatus.Internal
    public @Nullable Block get(Variant variant) {
        return this.blocks.get(variant);
    }

    @ApiStatus.Internal
    public @Nullable Item getItem(Variant variant) {
        if (!this.blocks.containsKey(variant)) return null;
        return Optional.ofNullable(this.blocks.get(variant)).map(Block::asItem).orElse(null);
    }

    @ApiStatus.Internal
    public BlockBatch add(Variant var, Function<BlockBehaviour.Properties, Block> function) {
        return this.add(var, function, p -> p);
    }

    @ApiStatus.Internal
    public BlockBatch add(Variant var, Function<BlockBehaviour.Properties, Block> function, Function<BlockBehaviour.Properties, BlockBehaviour.Properties> settings) {
        if (!this.blocks.containsKey(var)) this.blocks.put(var, scribbler.of("%s_%s".formatted(this.blocks.get(Variant.BLOCK).builtInRegistryHolder().key().identifier().getPath(), var.name().toLowerCase(Locale.ROOT)), function, settings.apply(BlockBehaviour.Properties.ofFullCopy(this.get()))));
        return this;
    }

    /// All standard block variants that use the same base texture
    public enum Variant {
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