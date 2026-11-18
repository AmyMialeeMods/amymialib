package dev.amymialee.amymialib;

import com.google.common.collect.ImmutableSet;
import dev.amymialee.amymialib.modules.dossier.Dossier;
import dev.amymialee.amymialib.modules.scribbler.auto.Decipher;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;

public class AmyMialibDossier extends Dossier {
    @Decipher("Dev Ready")
    public static final ResourceKey<FlatLevelGeneratorPreset> DEV_READY = AmyMialib.SCRIBBLER.flatPreset("dev_ready", ItemIds.AMETHYST_SHARD, Biomes.DARK_FOREST, ImmutableSet.of(), false, false, new FlatLayerInfo(15, Blocks.RED_SANDSTONE), new FlatLayerInfo(112, Blocks.DEEPSLATE), new FlatLayerInfo(1, Blocks.BEDROCK));
    @Decipher("Blast Proof")
    public static final ResourceKey<FlatLevelGeneratorPreset> BLAST_PROOF = AmyMialib.SCRIBBLER.flatPreset("blast_proof", BlockItemIds.TNT.item(), Biomes.DARK_FOREST, ImmutableSet.of(), false, false, new FlatLayerInfo(15, Blocks.OBSIDIAN), new FlatLayerInfo(112, Blocks.REINFORCED_DEEPSLATE), new FlatLayerInfo(1, Blocks.BEDROCK));

    public AmyMialibDossier() {
        super(AmyMialib.SCRIBBLER);
    }
}