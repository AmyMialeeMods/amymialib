package dev.amymialee.amymialib;

import com.google.common.collect.ImmutableSet;
import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.dossier.plans.FlatPreset;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

public class AmyMialibDossier extends Dossier {
    public static final ResourceKey<FlatLevelGeneratorPreset> DEV_READY = FlatPreset.of(AmyMialib.SCRIBBLER, "dev_ready", "Dev Ready", ItemIds.AMETHYST_SHARD, Biomes.DARK_FOREST, ImmutableSet.of(), false, false, new FlatLayerInfo(15, Blocks.RED_SANDSTONE), new FlatLayerInfo(112, Blocks.DEEPSLATE), new FlatLayerInfo(1, Blocks.BEDROCK));
    public static final ResourceKey<FlatLevelGeneratorPreset> BLAST_PROOF = FlatPreset.of(AmyMialib.SCRIBBLER, "blast_proof", "Blast Proof", BlockItemIds.TNT.item(), Biomes.DARK_FOREST, ImmutableSet.of(), false, false, new FlatLayerInfo(15, Blocks.OBSIDIAN), new FlatLayerInfo(112, Blocks.REINFORCED_DEEPSLATE), new FlatLayerInfo(1, Blocks.BEDROCK));

    public AmyMialibDossier() {
        super(AmyMialib.SCRIBBLER);
    }
}