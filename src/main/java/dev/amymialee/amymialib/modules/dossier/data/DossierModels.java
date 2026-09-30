package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import dev.amymialee.amymialib.modules.scribbler.helpers.BlockBatch;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

public interface DossierModels {
    @FunctionalInterface
    interface DossierBlockModels extends DossierModels, DossierLanyard {
        void commitBlockModels(Provider provider, BlockModelGenerators generator);

        @Override default String name() { return "Block Models"; }
    }

    @FunctionalInterface
    interface DossierItemModels extends DossierModels, DossierLanyard {
        void commitItemModels(Provider provider, ItemModelGenerators generator);

        @Override default String name() { return "Item Models"; }
    }

    final class Provider extends FabricModelProvider {
        public final Map<Block, TexturedModel> TEXTURED_MODELS = new HashMap<>();
        public final Dossier dossier;
        public BlockModelGenerators blocks;
        public ItemModelGenerators items;

        public Provider(Dossier dossier, FabricPackOutput output) {
            super(output);
            this.dossier = dossier;
        }

        @Override
        public void generateBlockStateModels(@NonNull BlockModelGenerators generator) {
            this.blocks = generator;
            this.dossier.registryEach(p -> p.commitBlockModels(this, generator), DossierBlockModels.class);
        }

        @Override
        public void generateItemModels(@NonNull ItemModelGenerators generator) {
            this.items = generator;
            this.dossier.registryEach(p -> p.commitItemModels(this, generator), DossierItemModels.class);
        }

        public TextureMapping getMapping(Block block) {
            return TEXTURED_MODELS.getOrDefault(block, TexturedModel.CUBE.get(block)).getMapping();
        }

        public void generateBatch(BlockBatch batch) {
            var base = batch.get();
            Optional.ofNullable(batch.get(BlockBatch.Variant.BLOCK)).ifPresent(blocks::createTrivialCube);
            Optional.ofNullable(batch.get(BlockBatch.Variant.STAIRS)).ifPresent(b -> stairs(b, base));
            Optional.ofNullable(batch.get(BlockBatch.Variant.SLAB)).ifPresent(b -> slab(b, base));
            Optional.ofNullable(batch.get(BlockBatch.Variant.CARPET)).ifPresent(b -> carpet(b, base));
            Optional.ofNullable(batch.get(BlockBatch.Variant.WALL)).ifPresent(b -> wall(b, base));
            Optional.ofNullable(batch.get(BlockBatch.Variant.PANE)).ifPresent(b -> pane(b, base));
            Optional.ofNullable(batch.get(BlockBatch.Variant.FENCE)).ifPresent(b -> fence(b, base));
            Optional.ofNullable(batch.get(BlockBatch.Variant.FENCE_GATE)).ifPresent(b -> fenceGate(b, base));
            Optional.ofNullable(batch.get(BlockBatch.Variant.PRESSURE_PLATE)).ifPresent(b -> pressurePlate(b, base));
            Optional.ofNullable(batch.get(BlockBatch.Variant.BUTTON)).ifPresent(b -> button(b, base));
        }

        public void grass(Block grassy, Block base) {
            this.blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(grassy, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(TexturedModel.CUBE_BOTTOM_TOP.get(grassy).updateTextures(m -> m.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(base))).create(grassy, this.blocks.modelOutput)))));
        }

        public void pillar(Block b) {
            var verticalModel = BlockModelGenerators.plainVariant(TexturedModel.COLUMN.create(b, this.blocks.modelOutput));
            var horizontalModel = BlockModelGenerators.plainVariant(TexturedModel.COLUMN_HORIZONTAL.create(b, this.blocks.modelOutput));
            this.blocks.blockStateOutput.accept(BlockModelGenerators.createRotatedPillarWithHorizontalVariant(b, verticalModel, horizontalModel));
        }

        public void anchor(Block b, IntegerProperty property) {
            var bottom = TextureMapping.getBlockTexture(b, "_bottom");
            var topOff = TextureMapping.getBlockTexture(b, "_top_off");
            var topOn = TextureMapping.getBlockTexture(b, "_top");
            var chargeLevelModels = new Identifier[5];
            for (var i = 0; i < 5; i++) chargeLevelModels[i] = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(b, "_" + i, new TextureMapping().put(TextureSlot.BOTTOM, bottom).put(TextureSlot.TOP, i == 0 ? topOff : topOn).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(b, "_side" + i)), this.blocks.modelOutput);
            this.blocks.blockStateOutput.accept(MultiVariantGenerator.dispatch(b).with(PropertyDispatch.initial(property).generate(ix -> BlockModelGenerators.plainVariant(chargeLevelModels[ix]))));
            this.blocks.registerSimpleItemModel(b, chargeLevelModels[0]);
        }

        public void fire(Block b) {
            var sideFireModels = this.blocks.createSideFireModels(b);
            this.blocks.blockStateOutput.accept(MultiPartGenerator.multiPart(b).with(this.blocks.createFloorFireModels(b)).with(sideFireModels).with(sideFireModels.with(Y_ROT_90)).with(sideFireModels.with(Y_ROT_180)).with(sideFireModels.with(Y_ROT_270)));
        }

        public void stairs(Block stairs, Block base) {
            var inner = BlockModelGenerators.plainVariant(ModelTemplates.STAIRS_INNER.create(stairs, getMapping(base), this.blocks.modelOutput));
            var straight = ModelTemplates.STAIRS_STRAIGHT.create(stairs, getMapping(base), this.blocks.modelOutput);
            var outer = BlockModelGenerators.plainVariant(ModelTemplates.STAIRS_OUTER.create(stairs, getMapping(base), this.blocks.modelOutput));
            this.blocks.blockStateOutput.accept(BlockModelGenerators.createStairs(stairs, inner, BlockModelGenerators.plainVariant(straight), outer));
            this.blocks.registerSimpleItemModel(stairs, straight);
        }

        public void slab(Block slab, Block base) {
            var fullBlockModel = BlockModelGenerators.plainVariant(ModelLocationUtils.getModelLocation(base));
            var bottom = ModelTemplates.SLAB_BOTTOM.create(slab, getMapping(base), this.blocks.modelOutput);
            var top = BlockModelGenerators.plainVariant(ModelTemplates.SLAB_TOP.create(slab, getMapping(base), this.blocks.modelOutput));
            this.blocks.blockStateOutput.accept(BlockModelGenerators.createSlab(slab, BlockModelGenerators.plainVariant(bottom), top, fullBlockModel));
            this.blocks.registerSimpleItemModel(slab, bottom);
        }

        public void carpet(Block carpet, Block base) {
            var mapping = getMapping(base);
            var carpetMapping = new TextureMapping().put(TextureSlot.WOOL, mapping.get(TextureSlot.ALL));
            var model = ModelTemplates.CARPET.create(carpet, carpetMapping, this.blocks.modelOutput);
            this.blocks.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(carpet, BlockModelGenerators.plainVariant(model)));
            this.blocks.registerSimpleItemModel(carpet, model);
        }

        public void wall(Block wall, Block base) {
            var mapping = getMapping(base);
            var post = BlockModelGenerators.plainVariant(ModelTemplates.WALL_POST.create(wall, mapping, this.blocks.modelOutput));
            var low = BlockModelGenerators.plainVariant(ModelTemplates.WALL_LOW_SIDE.create(wall, mapping, this.blocks.modelOutput));
            var high = BlockModelGenerators.plainVariant(ModelTemplates.WALL_TALL_SIDE.create(wall, mapping, this.blocks.modelOutput));
            this.blocks.blockStateOutput.accept(BlockModelGenerators.createWall(wall, post, low, high));
            var inventory = ModelTemplates.WALL_INVENTORY.create(wall, mapping, this.blocks.modelOutput);
            this.blocks.registerSimpleItemModel(wall, inventory);
        }

        public void pane(Block pane, Block base) {
            var mapping = getMapping(base);
            var textures = new TextureMapping().put(TextureSlot.BARS, mapping.get(TextureSlot.ALL)).put(TextureSlot.EDGE, mapping.get(TextureSlot.ALL));
            this.blocks.createBars(pane, ModelTemplates.BARS_POST_ENDS.create(pane, textures, this.blocks.modelOutput), ModelTemplates.BARS_POST.create(pane, textures, this.blocks.modelOutput), ModelTemplates.BARS_CAP.create(pane, textures, this.blocks.modelOutput), ModelTemplates.BARS_CAP_ALT.create(pane, textures, this.blocks.modelOutput), ModelTemplates.BARS_POST_SIDE.create(pane, textures, this.blocks.modelOutput), ModelTemplates.BARS_POST_SIDE_ALT.create(pane, textures, this.blocks.modelOutput));
            this.blocks.registerSimpleFlatItemModel(pane);
        }

        public void fence(Block fence, Block base) {
            var mapping = getMapping(base);
            var post = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_POST.create(fence, mapping, this.blocks.modelOutput));
            var side = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_SIDE.create(fence, mapping, this.blocks.modelOutput));
            this.blocks.blockStateOutput.accept(BlockModelGenerators.createFence(fence, post, side));
            var inventory = ModelTemplates.FENCE_INVENTORY.create(fence, mapping, this.blocks.modelOutput);
            this.blocks.registerSimpleItemModel(fence, inventory);
        }

        public void fenceGate(Block fenceGate, Block base) {
            var mapping = getMapping(base);
            var open = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_OPEN.create(fenceGate, mapping, this.blocks.modelOutput));
            var closed = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_CLOSED.create(fenceGate, mapping, this.blocks.modelOutput));
            var openWall = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_WALL_OPEN.create(fenceGate, mapping, this.blocks.modelOutput));
            var closedWall = BlockModelGenerators.plainVariant(ModelTemplates.FENCE_GATE_WALL_CLOSED.create(fenceGate, mapping, this.blocks.modelOutput));
            this.blocks.blockStateOutput.accept(BlockModelGenerators.createFenceGate(fenceGate, open, closed, openWall, closedWall, true));
        }

        public void pressurePlate(Block pressurePlate, Block base) {
            var mapping = getMapping(base);
            var off = BlockModelGenerators.plainVariant(ModelTemplates.PRESSURE_PLATE_UP.create(pressurePlate, mapping, this.blocks.modelOutput));
            var on = BlockModelGenerators.plainVariant(ModelTemplates.PRESSURE_PLATE_DOWN.create(pressurePlate, mapping, this.blocks.modelOutput));
            this.blocks.blockStateOutput.accept(BlockModelGenerators.createPressurePlate(pressurePlate, off, on));
        }

        public void button(Block button, Block base) {
            var mapping = getMapping(base);
            var normal = BlockModelGenerators.plainVariant(ModelTemplates.BUTTON.create(button, mapping, this.blocks.modelOutput));
            var pressed = BlockModelGenerators.plainVariant(ModelTemplates.BUTTON_PRESSED.create(button, mapping, this.blocks.modelOutput));
            this.blocks.blockStateOutput.accept(BlockModelGenerators.createButton(button, normal, pressed));
            var inventory = ModelTemplates.BUTTON_INVENTORY.create(button, mapping, this.blocks.modelOutput);
            this.blocks.registerSimpleItemModel(button, inventory);
        }

        @Override
        public @NonNull String getName() {
            return this.dossier.name() + " " + super.getName();
        }
    }
}