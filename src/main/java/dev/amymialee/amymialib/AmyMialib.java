package dev.amymialee.amymialib;

import dev.amymialee.amymialib.util.AMRegistry;
import dev.upcraft.datasync.api.util.Entitlements;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class AmyMialib implements ModInitializer {
    public static final String MOD_ID = "amymialib";
    public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getName();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final AMRegistry REGISTRY = new AMRegistry(MOD_ID);
    public static final Random RANDOM = new Random();
    private static final Identifier TIER1_ID = Identifier.fromNamespaceAndPath("amymialee", "tier1");
    private static final Identifier TIER2_ID = Identifier.fromNamespaceAndPath("amymialee", "tier2");
    private static final Identifier TIER3_ID = Identifier.fromNamespaceAndPath("amymialee", "tier3");

    @Override
    public void onInitialize() {
    }

    public static boolean isSupporterTier1(UUID uuid) {
        return Entitlements.getOrEmpty(uuid).keys().contains(TIER1_ID);
    }

    public static boolean isSupporterTier2(UUID uuid) {
        return Entitlements.getOrEmpty(uuid).keys().contains(TIER2_ID);
    }

    public static boolean isSupporterTier3(UUID uuid) {
        return Entitlements.getOrEmpty(uuid).keys().contains(TIER3_ID);
    }

    public static @NotNull Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}