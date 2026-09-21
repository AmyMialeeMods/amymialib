package dev.amymialee.amymialib.modules;

import dev.upcraft.datasync.api.util.Entitlements;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public class AMSupporter {
    private static final Identifier TIER1_ID = Identifier.fromNamespaceAndPath("amymialee", "tier1");
    private static final Identifier TIER2_ID = Identifier.fromNamespaceAndPath("amymialee", "tier2");
    private static final Identifier TIER3_ID = Identifier.fromNamespaceAndPath("amymialee", "tier3");

    public static boolean isSupporterTier1(UUID uuid) {
        return Entitlements.getOrEmpty(uuid).keys().contains(TIER1_ID);
    }

    public static boolean isSupporterTier2(UUID uuid) {
        return Entitlements.getOrEmpty(uuid).keys().contains(TIER2_ID);
    }

    public static boolean isSupporterTier3(UUID uuid) {
        return Entitlements.getOrEmpty(uuid).keys().contains(TIER3_ID);
    }
}