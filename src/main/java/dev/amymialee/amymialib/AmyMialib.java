package dev.amymialee.amymialib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.amymialee.amymialib.aspects.Aspect;
import dev.amymialee.amymialib.aspects.AspectManager;
import dev.amymialee.amymialib.aspects.AspectCategory;
import dev.amymialee.amymialib.aspects.AspectSyncPayload;
import dev.amymialee.amymialib.aspects.AspectType;
import dev.amymialee.amymialib.util.AMRegistry;
import dev.upcraft.datasync.api.util.Entitlements;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class AmyMialib implements ModInitializer {
    public static final String MOD_ID = "amymialib";
    public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getName();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final AMRegistry REGISTRY = new AMRegistry(MOD_ID);
    public static final Random RANDOM = new Random();
    private static final Identifier TIER1_ID = Identifier.fromNamespaceAndPath("amymialee", "tier1");
    private static final Identifier TIER2_ID = Identifier.fromNamespaceAndPath("amymialee", "tier2");
    private static final Identifier TIER3_ID = Identifier.fromNamespaceAndPath("amymialee", "tier3");

    public static final AspectCategory AMYMIALIB = REGISTRY.category("amymialib", "AmyMialib", "AmyMialib Aspects").icon(Items.DIAMOND).backgroundSprite(id("aspects/background_2")).build();
    public static final Aspect<Boolean> DISABLE_END_PORTALS = REGISTRY.config("disable_end_portals", AspectType.BOOLEAN_TRUE, "Disable End Portals", "Should end portals be disabled?").icon(Items.END_PORTAL_FRAME).category(AMYMIALIB).build();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(AspectSyncPayload.ID_CHANGED, AspectSyncPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AspectSyncPayload.ID_CHANGED, AspectSyncPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID_CHANGED, AspectManager::receivePayloadServer);
        PayloadTypeRegistry.serverboundPlay().register(AspectSyncPayload.ID_UNCHANGED, AspectSyncPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AspectSyncPayload.ID_UNCHANGED, AspectSyncPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID_UNCHANGED, AspectManager::receivePayloadServer);

        for (var z = 1; z < 27; z++) {
            var category = AspectCategory.of(AmyMialib.id("amymialib_%s".formatted(z))).icon(Items.BARRIER).backgroundSprite(id("aspects/background_2")).build();
            for (var i = 0; i < z; i++) {
                Aspect.config(AmyMialib.id("amymialib_%s".formatted(z)), AspectType.BOOLEAN_TRUE).icon(Items.STRUCTURE_BLOCK).category(category).build();
            }
        }
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