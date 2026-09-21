package dev.amymialee.amymialib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Pair;
import dev.amymialee.amymialib.modules.aspects.*;
import dev.amymialee.amymialib.modules.scribbler.Scribble;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import dev.upcraft.datasync.api.util.Entitlements;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AmyMialib implements ModInitializer {
    public static final String MOD_ID = "amymialib";
    public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getName();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final List<Scribble<?>> SCRIBBLES = new ArrayList<>();
    public static final List<Pair<Scribbler, String>> AUTO_QUEUE = new ArrayList<>();
    public static final Scribbler SCRIBBLER = new Scribbler(MOD_ID).auto("dev.amymialee.amymialib");
    public static final Random RANDOM = new Random();
    private static final Identifier TIER1_ID = Identifier.fromNamespaceAndPath("amymialee", "tier1");
    private static final Identifier TIER2_ID = Identifier.fromNamespaceAndPath("amymialee", "tier2");
    private static final Identifier TIER3_ID = Identifier.fromNamespaceAndPath("amymialee", "tier3");

    public static final AspectLink p = SCRIBBLER.link("website", "https://amymialee.dev", "AmyMialee", "AmyMialee Website").icon(Items.DIAMOND).build();
    public static final AspectCategory AMYMIALIB = SCRIBBLER.category("amymialib", "AmyMialib", "AmyMialib Aspects").link(p).link(p).link(p).link(p).icon(Items.DIAMOND).backgroundSprite(id("aspects/background_2")).build();
    public static final Aspect<Boolean> DISABLE_END_PORTALS = SCRIBBLER.config("disable_end_portals", AspectType.BOOLEAN_TRUE, "Disable End Portals", "Should end portals be disabled?").icon(Items.END_PORTAL_FRAME).category(AMYMIALIB).build();
    //TODO: Disable MovedTooQuickly

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(AspectSyncPayload.ID_CHANGED, AspectSyncPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AspectSyncPayload.ID_CHANGED, AspectSyncPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID_CHANGED, AspectManager::receivePayloadServer);
        PayloadTypeRegistry.serverboundPlay().register(AspectSyncPayload.ID_UNCHANGED, AspectSyncPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AspectSyncPayload.ID_UNCHANGED, AspectSyncPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID_UNCHANGED, AspectManager::receivePayloadServer);
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

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}