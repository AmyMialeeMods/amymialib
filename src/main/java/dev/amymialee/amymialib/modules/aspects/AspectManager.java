package dev.amymialee.amymialib.modules.aspects;

import dev.amymialee.amymialib.AmyMialib;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages aspects, namely their syncing and storing them for future use.
 */
public @SuppressWarnings({"rawtypes", "unchecked"}) class AspectManager {
    public static final Map<Identifier, Aspect> ASPECTS = new HashMap<>();
    static final Map<Identifier, AspectCategory> CATEGORIES = new HashMap<>();
    static final Map<Identifier, List<Aspect<?>>> CATEGORY_ENTRIES = new HashMap<>();
    static final Map<Identifier, AspectLink> LINKS = new HashMap<>();

    /**
     * This is received from a server after an Aspect has been changed.<br>
     * We accept it always, as the server has full control.<br>
     * We only run {@link Aspect#onChangeClient} if the payload says that it has been changed.<br>
     * @param changed tells us if the value has been changed, or if this is just arbitrary syncing.
     */
    public static void receivePayloadClient(AspectSyncPayload payload, ClientPlayNetworking.Context context, boolean changed) {
        var id = payload.id();
        var data = payload.data();
        try {
            var aspect = AspectManager.ASPECTS.get(id);
            if (aspect.saveLocation == AspectSaveLocation.CLIENT || aspect.value == data) return;
            if (changed) {
                aspect.set(context.client(), data);
            } else {
                aspect.loadFromFile(data);
            }
        } catch (Exception e) {
            AmyMialib.LOGGER.error("Failed to receive aspect {} from server", id, e);
        }
    }

    /**
     * This is received from a client trying to change the value of an Aspect.<br>
     * We need to verify that they are allowed and that the value is actually different from the current.<br>
     * If allowed, we change it and save, sync, and run the {@link Aspect#onChangeServer} if relevant.
     */
    public static void receivePayloadServer(AspectSyncPayload payload, ServerPlayNetworking.Context context) {
        var id = payload.id();
        var data = payload.data();
        try {
            var aspect = AspectManager.ASPECTS.get(id);
            if (aspect.saveLocation == AspectSaveLocation.CLIENT) return;
            if (!context.player().permissions().hasPermission(aspect.permission)) {
                syncUnChangedS2C(aspect, context.player());
                return;
            }
            if (aspect.value != data) aspect.set(context.server(), data);
        } catch (Exception e) {
            AmyMialib.LOGGER.error("Failed to receive aspect {} from {}", id, context.player().getGameProfile().name(), e);
        }
    }

    /**
     * Attempts to change the value of an aspect on the server.<br>
     * If successful, the server will sync the new value back to us and everyone else.<br>
     * If failed, the server will sync the old value back to us.
     */
    public static <T> void sendC2S(Aspect<T> aspect, T data) {
        ClientPlayNetworking.send(AspectSyncPayload.c2s(aspect, data));
    }

    /**
     * Syncs the given aspect to every player on the server.<br>
     * This is done after the value has been changed.
     */
    public static void syncChangedS2C(MinecraftServer server, Aspect<?> aspect) {
        server.getPlayerList().getPlayers().forEach(p -> ServerPlayNetworking.send(p, AspectSyncPayload.changed(aspect)));
    }

    /**
     * Syncs the every aspect to a specific player.<br>
     * This is done when we believe the player doesn't have the most up-to-date configs.
     */
    public static void syncUnChangedS2C(ServerPlayer player) {
        ASPECTS.values().forEach(aspect -> ServerPlayNetworking.send(player, AspectSyncPayload.unchanged(aspect)));
    }

    /**
     * Syncs the given aspect to a specific player.<br>
     * This is only done when the player tries to change an aspect without relevant permissions.
     */
    public static void syncUnChangedS2C(Aspect<?> aspect, ServerPlayer player) {
        ServerPlayNetworking.send(player, AspectSyncPayload.unchanged(aspect));
    }

    /**
     * Creates a map of all categories and all of their contained aspects.<br>
     * Mostly used for the aspect editor.<br>
     * Uses a copy of the aspect list to avoid unauthorised edits.
     */
    public static Map<AspectCategory, List<Aspect<?>>> getCategoryEntries() {
        var map = new HashMap<AspectCategory, List<Aspect<?>>>();
        CATEGORIES.values().forEach(category -> map.put(category, new ArrayList<>(CATEGORY_ENTRIES.get(category.id))));
        return map;
    }

    /**
     * Creates a map of a category and all of its contained aspects.<br>
     * Mostly used for the aspect editor.<br>
     * Uses a copy of the aspect list to avoid unauthorised edits.
     */
    public static List<Aspect<?>> getCategoryEntries(AspectCategory category) {
        return new ArrayList<>(CATEGORY_ENTRIES.getOrDefault(category.id, new ArrayList<>()));
    }
}