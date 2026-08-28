package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jspecify.annotations.NonNull;

import java.util.List;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

public interface AMGenModels extends AMNameProvider {
    default void generateBlockStateModels(AMModelProvider provider, BlockModelGenerators generator) {}

    default void generateItemModels(AMModelProvider provider, ItemModelGenerators generator) {}

    static FabricDataGenerator.Pack.@NonNull RegistryDependentFactory<?> listProvider(String name, List<Object> list) {
        return (dataOutput, _) -> new AMModelProvider(new AMGenModels() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public void generateBlockStateModels(AMModelProvider provider, BlockModelGenerators generator) {
                for (var object : list) if (object instanceof AMGenModels task) task.generateBlockStateModels(provider, generator);
            }

            @Override
            public void generateItemModels(AMModelProvider provider, ItemModelGenerators generator) {
                for (var object : list) if (object instanceof AMGenModels task) task.generateItemModels(provider, generator);
            }
        }, dataOutput);
    }

    class AMModelProvider extends FabricModelProvider {
        private final AMGenModels dataGen;

        public AMModelProvider(AMGenModels gen, FabricPackOutput output) {
            super(output);
            this.dataGen = gen;
        }

        @Override
        public void generateBlockStateModels(@NonNull BlockModelGenerators blockModelGenerators) {
            this.dataGen.generateBlockStateModels(this, blockModelGenerators);
        }

        @Override
        public void generateItemModels(@NonNull ItemModelGenerators itemModelGenerators) {
            this.dataGen.generateItemModels(this, itemModelGenerators);
        }

        public void grass(@NonNull BlockModelGenerators generator, Block grassy, Block base) {
            generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(grassy, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(TexturedModel.CUBE_TOP_BOTTOM.get(grassy).updateTextures(m -> m.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(base))).create(grassy, generator.modelOutput)))));
        }

        public void pillar(@NonNull BlockModelGenerators generator, Block b) {
            var verticalModel = BlockModelGenerators.plainVariant(TexturedModel.COLUMN.create(b, generator.modelOutput));
            var horizontalModel = BlockModelGenerators.plainVariant(TexturedModel.COLUMN_HORIZONTAL.create(b, generator.modelOutput));
            generator.blockStateOutput.accept(BlockModelGenerators.createRotatedPillarWithHorizontalVariant(b, verticalModel, horizontalModel));
        }

        public void anchor(@NonNull BlockModelGenerators generator, Block b, IntegerProperty property) {
            var bottom = TextureMapping.getBlockTexture(b, "_bottom");
            var topOff = TextureMapping.getBlockTexture(b, "_top_off");
            var topOn = TextureMapping.getBlockTexture(b, "_top");
            var chargeLevelModels = new Identifier[5];
            for (var i = 0; i < 5; i++) chargeLevelModels[i] = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(b, "_" + i, new TextureMapping().put(TextureSlot.BOTTOM, bottom).put(TextureSlot.TOP, i == 0 ? topOff : topOn).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(b, "_side" + i)), generator.modelOutput);
            generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(b).with(PropertyDispatch.initial(property).generate(ix -> BlockModelGenerators.plainVariant(chargeLevelModels[ix]))));
            generator.registerSimpleItemModel(b, chargeLevelModels[0]);
        }

        public void fire(@NonNull BlockModelGenerators generator, Block b) {
            var sideFireModels = generator.createSideFireModels(b);
            generator.blockStateOutput.accept(MultiPartGenerator.multiPart(b).with(generator.createFloorFireModels(b)).with(sideFireModels).with(sideFireModels.with(Y_ROT_90)).with(sideFireModels.with(Y_ROT_180)).with(sideFireModels.with(Y_ROT_270)));
        }

        @Override
        public @NonNull String getName() {
            return this.dataGen.getName() + " " + super.getName();
        }
    }
}