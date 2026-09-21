package dev.amymialee.amymialib.modules.dossier.data;

import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.DossierLanyard;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
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
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.client.data.models.BlockModelGenerators.*;

public interface DossierModels {
    @FunctionalInterface
    interface DossierBlockModels extends DossierModels, DossierLanyard {
        void commitBlockModels(Provider provider, BlockModelGenerators generator);
    }

    @FunctionalInterface
    interface DossierItemModels extends DossierModels, DossierLanyard {
        void commitItemModels(Provider provider, ItemModelGenerators generator);
    }

    @ApiStatus.Internal
    class Provider extends FabricModelProvider {
        private final Dossier dossier;
        public final FabricPackOutput output;
        public final CompletableFuture<HolderLookup.Provider> registriesFuture;

        public Provider(Dossier dossier, FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
            super(output);
            this.dossier = dossier;
            this.output = output;
            this.registriesFuture = registriesFuture;
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
            this.dossier.registryEach(DossierBlockModels.class, p -> p.commitBlockModels(this, blockModelGenerators));
        }

        @Override
        public void generateItemModels(ItemModelGenerators itemModelGenerators) {
            this.dossier.registryEach(DossierItemModels.class, p -> p.commitItemModels(this, itemModelGenerators));
        }

        public void grass(BlockModelGenerators generator, Block grassy, Block base) {
            generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(grassy, BlockModelGenerators.createRotatedVariants(BlockModelGenerators.plainModel(TexturedModel.CUBE_BOTTOM_TOP.get(grassy).updateTextures(m -> m.put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(base))).create(grassy, generator.modelOutput)))));
        }

        public void pillar(BlockModelGenerators generator, Block b) {
            var verticalModel = BlockModelGenerators.plainVariant(TexturedModel.COLUMN.create(b, generator.modelOutput));
            var horizontalModel = BlockModelGenerators.plainVariant(TexturedModel.COLUMN_HORIZONTAL.create(b, generator.modelOutput));
            generator.blockStateOutput.accept(BlockModelGenerators.createRotatedPillarWithHorizontalVariant(b, verticalModel, horizontalModel));
        }

        public void anchor(BlockModelGenerators generator, Block b, IntegerProperty property) {
            var bottom = TextureMapping.getBlockTexture(b, "_bottom");
            var topOff = TextureMapping.getBlockTexture(b, "_top_off");
            var topOn = TextureMapping.getBlockTexture(b, "_top");
            var chargeLevelModels = new Identifier[5];
            for (var i = 0; i < 5; i++) chargeLevelModels[i] = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(b, "_" + i, new TextureMapping().put(TextureSlot.BOTTOM, bottom).put(TextureSlot.TOP, i == 0 ? topOff : topOn).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(b, "_side" + i)), generator.modelOutput);
            generator.blockStateOutput.accept(MultiVariantGenerator.dispatch(b).with(PropertyDispatch.initial(property).generate(ix -> BlockModelGenerators.plainVariant(chargeLevelModels[ix]))));
            generator.registerSimpleItemModel(b, chargeLevelModels[0]);
        }

        public void fire(BlockModelGenerators generator, Block b) {
            var sideFireModels = generator.createSideFireModels(b);
            generator.blockStateOutput.accept(MultiPartGenerator.multiPart(b).with(generator.createFloorFireModels(b)).with(sideFireModels).with(sideFireModels.with(Y_ROT_90)).with(sideFireModels.with(Y_ROT_180)).with(sideFireModels.with(Y_ROT_270)));
        }

        @Override
        public String getName() {
            return this.dossier.name() + " " + super.getName();
        }
    }
}