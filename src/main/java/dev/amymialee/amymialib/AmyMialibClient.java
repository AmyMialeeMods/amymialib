package dev.amymialee.amymialib;

import dev.amymialee.amymialib.modules.aspects.AspectManager;
import dev.amymialee.amymialib.modules.aspects.AspectSyncPayload;
import dev.amymialee.amymialib.modules.aspects.client.AspectEditorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class AmyMialibClient implements ClientModInitializer {
    public static final Component ASPECTS_SCREEN = Component.translatable(AmyMialib.SCRIBBLER.translation(AmyMialib.id("aspects.screen").toLanguageKey(), "Aspect Editor"));
    public static final int CLIENT_COLOUR = new Color(0xBD6868).getRGB();
    public static final Component CLIENT_ONLY = Component.translatable(AmyMialib.SCRIBBLER.translation(AmyMialib.id("aspects.client_only").toLanguageKey(), "[Client Only]")).withColor(CLIENT_COLOUR);

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID_CHANGED, (payload, context) -> AspectManager.receivePayloadClient(payload, context, true));
        ClientPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID_UNCHANGED, (payload, context) -> AspectManager.receivePayloadClient(payload, context, false));
    }

    public static SpriteIconButton aspectsButton(Screen screen) {
        return SpriteIconButton.builder(ASPECTS_SCREEN, _ -> Minecraft.getInstance().setScreenAndShow(new AspectEditorScreen(screen)), true)
                .tooltip(ASPECTS_SCREEN).width(20).sprite(AmyMialib.id("logo"), 16, 16).build();
    }
}