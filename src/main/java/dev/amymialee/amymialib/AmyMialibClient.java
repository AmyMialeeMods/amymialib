package dev.amymialee.amymialib;

import dev.amymialee.amymialib.aspects.AspectSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AmyMialibClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID, new AspectSyncPayload.ClientHandler());
    }
}