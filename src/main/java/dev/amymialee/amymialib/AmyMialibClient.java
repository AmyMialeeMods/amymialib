package dev.amymialee.amymialib;

import dev.amymialee.amymialib.aspects.AspectManager;
import dev.amymialee.amymialib.aspects.AspectSyncPayload;
import dev.amymialee.amymialib.aspects.client.AspectEditorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.awt.*;

public class AmyMialibClient implements ClientModInitializer {
    public static final Component ASPECTS_SCREEN = Component.translatable(AmyMialib.REGISTRY.createTranslation(AmyMialib.id("aspects.screen").toLanguageKey(), "Aspect Editor"));
    public static final int CLIENT_COLOUR = new Color(0xBD6868).getRGB();
    public static final Component CLIENT_ONLY = Component.translatable(AmyMialib.REGISTRY.createTranslation(AmyMialib.id("aspects.client_only").toLanguageKey(), "[Client Only]")).withColor(CLIENT_COLOUR);

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID_CHANGED, (payload, context) -> AspectManager.receivePayloadClient(payload, context, true));
        ClientPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID_UNCHANGED, (payload, context) -> AspectManager.receivePayloadClient(payload, context, false));
    }

    public static @NonNull SpriteIconButton aspectsButton(Screen screen) {
        return SpriteIconButton.builder(ASPECTS_SCREEN, _ -> Minecraft.getInstance().setScreenAndShow(new AspectEditorScreen(screen)), true)
                .tooltip(ASPECTS_SCREEN).width(20).sprite(AmyMialib.id("logo"), 16, 16).build();
    }
}