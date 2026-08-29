package dev.amymialee.amymialib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.amymialee.amymialib.aspects.Aspect;
import dev.amymialee.amymialib.aspects.AspectSyncPayload;
import dev.amymialee.amymialib.aspects.AspectType;
import dev.amymialee.amymialib.util.AMRegistry;
import dev.upcraft.datasync.api.util.Entitlements;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
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
    private static final Component ASPECTS_SCREEN = Component.translatable(REGISTRY.createTranslation(AmyMialib.id("aspects.screen").toLanguageKey(), "Aspect Editor"));
    private static final Component ASPECTS_SCREEN_EMPTY = ASPECTS_SCREEN.copy().append("\n").append(Component.translatable(REGISTRY.createTranslation(AmyMialib.id("aspects.screen.empty").toLanguageKey(), "No (Available) Configs Found")).withColor(new Color(0xBD6868).getRGB()));

    public static final Aspect<Boolean> DISABLE_END_PORTALS = Aspect.config(AmyMialib.id("disable_end_portals"), AspectType.BOOLEAN_TRUE).icon(Items.END_PORTAL_FRAME).build();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.serverboundPlay().register(AspectSyncPayload.ID, AspectSyncPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AspectSyncPayload.ID, AspectSyncPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID, new AspectSyncPayload.ServerHandler());
    }

    public static @NonNull SpriteIconButton aspectsButton(Screen screen) {
        var none = true;
//        for (var category : MValueCategory.CATEGORIES) {
//            if (!category.getValues(this.minecraft.player).isEmpty()) {
//                none = false;
//                break;
//            }
//        }
        var tooltip = ASPECTS_SCREEN;
        if (none) tooltip = ASPECTS_SCREEN_EMPTY;
        return SpriteIconButton.builder(ASPECTS_SCREEN, _ -> Minecraft.getInstance().setScreenAndShow(new OptionsScreen(screen, Minecraft.getInstance().options, false)), true)
                .tooltip(tooltip).width(20).sprite(AmyMialib.id("logo"), 16, 16).build();
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