package dev.amymialee.amymialib;

import dev.amymialee.amymialib.aspects.AspectManager;
import dev.amymialee.amymialib.aspects.builder.AspectSyncPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AmyMialibClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID_CHANGED, (payload, context) -> AspectManager.receivePayloadClient(payload, context, true));
        ClientPlayNetworking.registerGlobalReceiver(AspectSyncPayload.ID_UNCHANGED, (payload, context) -> AspectManager.receivePayloadClient(payload, context, false));
    }
}