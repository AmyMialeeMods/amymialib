package dev.amymialee.amymialib.modules.scribbler.scribbles;

import dev.amymialee.amymialib.modules.dossier.data.DossierTags;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import net.minecraft.references.BlockItemId;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.PushReaction;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ScribbleBlockBatch {
    public final Scribbler scribbler;
    private final HashMap<Variant, ScribbleBlock> blocks = new HashMap<>();
    private @Nullable Supplier<Item.Properties> itemProperties = () -> new Item.Properties().useBlockDescriptionPrefix();

    @ApiStatus.Internal
    public ScribbleBlockBatch(Scribbler scribbler, ScribbleBlock block) {
        this.scribbler = scribbler;
        this.blocks.put(Variant.BLOCK, block);
    }

    @ApiStatus.Internal
    public ScribbleBlockBatch(Scribbler scribbler, String path, Function<BlockBehaviour.Properties,Block> function, BlockBehaviour.Properties settings) {
        this.scribbler = scribbler;
        this.blocks.put(Variant.BLOCK, scribbler.of(path, function, settings));
    }

    public static ScribbleBlockBatch fake(Scribbler scribbler, BlockItemId key) {
        return new ScribbleBlockBatch(scribbler, ScribbleBlock.fakeBlock(scribbler, key.block()));
    }

    public ScribbleBlockBatch translation(String translation) {
        for (var entry : this.blocks.entrySet()) {
            entry.getValue().translation(entry.getKey() == Variant.BLOCK ? translation : "%s %s".formatted(translation, WordUtils.capitalizeFully(entry.getKey().name.replace('_', ' '))));
        }
        return this;
    }

    public ScribbleBlockBatch addTo(TagKey<Block> target) {
        for (var entry : this.blocks.entrySet()) this.scribbler.dossierData.add((DossierTags) (provider) -> provider.tag(target).add(entry.getValue().key()));
        return this;
    }

    public ScribbleBlockBatch noItem() {
        this.itemProperties = null;
        return this;
    }

    public ScribbleBlockBatch setItem(Supplier<Item.Properties> properties) {
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

    public ResourceKey<Block> key() {
        return this.blocks.get(Variant.BLOCK).key();
    }

    public ScribbleBlockBatch setStairs(BlockItemId existing) {
        this.blocks.put(Variant.STAIRS, ScribbleBlock.fakeBlock(this.scribbler, existing.block()));
        return this;
    }

    public ScribbleBlockBatch setSlab(BlockItemId existing) {
        this.blocks.put(Variant.SLAB, ScribbleBlock.fakeBlock(this.scribbler, existing.block()));
        return this;
    }

    public ScribbleBlockBatch setCarpet(BlockItemId existing) {
        this.blocks.put(Variant.CARPET, ScribbleBlock.fakeBlock(this.scribbler, existing.block()));
        return this;
    }

    public ScribbleBlockBatch setWall(BlockItemId existing) {
        this.blocks.put(Variant.WALL, ScribbleBlock.fakeBlock(this.scribbler, existing.block()));
        return this;
    }

    public ScribbleBlockBatch setPane(BlockItemId existing) {
        this.blocks.put(Variant.PANE, ScribbleBlock.fakeBlock(this.scribbler, existing.block()));
        return this;
    }

    public ScribbleBlockBatch setFence(BlockItemId existing) {
        this.blocks.put(Variant.FENCE, ScribbleBlock.fakeBlock(this.scribbler, existing.block()));
        return this;
    }

    public ScribbleBlockBatch setFenceGate(BlockItemId existing) {
        this.blocks.put(Variant.FENCE_GATE, ScribbleBlock.fakeBlock(this.scribbler, existing.block()));
        return this;
    }

    public ScribbleBlockBatch setPressurePlate(BlockItemId existing) {
        this.blocks.put(Variant.PRESSURE_PLATE, ScribbleBlock.fakeBlock(this.scribbler, existing.block()));
        return this;
    }

    public ScribbleBlockBatch setButton(BlockItemId existing) {
        this.blocks.put(Variant.BUTTON, ScribbleBlock.fakeBlock(this.scribbler, existing.block()));
        return this;
    }

    public ScribbleBlockBatch addStairs() {
        return this.add(Variant.STAIRS, p -> new StairBlock(this.get().defaultBlockState(), p));
    }

    public ScribbleBlockBatch addSlab() {
        return this.add(Variant.SLAB, SlabBlock::new);
    }

    public ScribbleBlockBatch addCarpet() {
        return this.add(Variant.CARPET, CarpetBlock::new, p -> p.strength(0.1F).pushReaction(PushReaction.POPPED));
    }

    public ScribbleBlockBatch addWall() {
        return this.add(Variant.WALL, WallBlock::new, BlockBehaviour.Properties::forceSolidOn);
    }

    public ScribbleBlockBatch addPane() {
        return this.add(Variant.PANE, IronBarsBlock::new, BlockBehaviour.Properties::noOcclusion);
    }

    public ScribbleBlockBatch addFence() {
        return this.add(Variant.FENCE, FenceBlock::new);
    }

    public ScribbleBlockBatch addFenceGate(WoodType type) {
        return this.add(Variant.FENCE_GATE, p -> new FenceGateBlock(type, p), BlockBehaviour.Properties::forceSolidOn);
    }

    public ScribbleBlockBatch addPressurePlate(BlockSetType type) {
        return this.add(Variant.PRESSURE_PLATE, p -> new PressurePlateBlock(type, p), p -> p.forceSolidOn().noCollision().strength(0.5F).pushReaction(PushReaction.POPPED));
    }

    public ScribbleBlockBatch addButton(BlockSetType type, int ticksToStayPressed) {
        return this.add(Variant.BUTTON, p -> new ButtonBlock(type, ticksToStayPressed, p), p -> p.noCollision().strength(0.5F).pushReaction(PushReaction.POPPED));
    }

    public ScribbleBlockBatch stonelike() {
        this.addStairs();
        this.addSlab();
        this.addWall();
        return this;
    }

    public ScribbleBlockBatch stonelike(BlockSetType type, int ticksToStayPressed) {
        this.addPlateAndButton(type, ticksToStayPressed);
        return this.stonelike();
    }

    public ScribbleBlockBatch woodLike(WoodType wood, BlockSetType type, int ticksToStayPressed) {
        this.addStairs();
        this.addSlab();
        this.addFence();
        this.addFenceGate(wood);
        this.addPlateAndButton(type, ticksToStayPressed);
        return this;
    }

    public ScribbleBlockBatch addPlateAndButton(BlockSetType type, int ticksToStayPressed) {
        this.addPressurePlate(type);
        this.addButton(type, ticksToStayPressed);
        return this;
    }

    @ApiStatus.Internal
    private @Nullable Block get(Variant variant) {
        return this.blocks.get(variant).get();
    }

    @ApiStatus.Internal
    private @Nullable Item getItem(Variant variant) {
        if (!this.blocks.containsKey(variant)) return null;
        return Optional.ofNullable(this.blocks.get(variant)).map(ScribbleBlock::item).map(Scribble::get).orElse(null);
    }

    public Optional<Block> fetchBlock(Variant variant) {
        if (!this.blocks.containsKey(variant) || this.blocks.get(variant).getter == null) return Optional.empty();
        return Optional.of(this.blocks.get(variant).get());
    }

    public Optional<Item> fetchItem(Variant variant) {
        if (!this.blocks.containsKey(variant)) return Optional.empty();
        return Optional.ofNullable(this.blocks.get(variant)).map(ScribbleBlock::item).map(Scribble::get);
    }

    public Optional<Scribble<Block>> fetchBlockScribble(Variant variant) {
        if (!this.blocks.containsKey(variant) || this.blocks.get(variant).getter == null) return Optional.empty();
        return Optional.of(this.blocks.get(variant));
    }

    public Optional<Scribble<Item>> fetchItemScribble(Variant variant) {
        if (!this.blocks.containsKey(variant) || this.blocks.get(variant).getter == null) return Optional.empty();
        return Optional.ofNullable(this.blocks.get(variant).item());
    }

    @ApiStatus.Internal
    private ScribbleBlockBatch add(Variant var, Function<BlockBehaviour.Properties, Block> function) {
        return this.add(var, function, p -> p);
    }

    @ApiStatus.Internal
    private ScribbleBlockBatch add(Variant var, Function<BlockBehaviour.Properties, Block> function, Function<BlockBehaviour.Properties, BlockBehaviour.Properties> settings) {
        if (!this.blocks.containsKey(var)) this.blocks.put(var, scribbler.of("%s_%s".formatted(this.blocks.get(Variant.BLOCK).key().identifier().getPath(), var.name().toLowerCase(Locale.ROOT)), function, settings.apply(BlockBehaviour.Properties.ofFullCopy(this.get()))));
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