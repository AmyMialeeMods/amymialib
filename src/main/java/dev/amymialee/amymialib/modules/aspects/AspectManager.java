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
public @SuppressWarnings("rawtypes") class AspectManager {
    public static final Map<Identifier, Aspect> ASPECTS = new HashMap<>();
    private static final Map<Identifier, AspectCategory> CATEGORIES = new HashMap<>();
    private static final Map<Identifier, List<Aspect<?>>> CATEGORY_ENTRIES = new HashMap<>();
    private static final Map<Identifier, AspectLink> LINKS = new HashMap<>();

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
            if (aspect.saveLocation == AspectSaveLocation.CLIENT) return;
            aspect.value = data;
            aspect.saveToFile();
            if (changed && aspect.onChangeClient != null) aspect.onChangeClient.run();
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
            if (!aspect.hasPermission(context.player())) {
                syncUnChangedS2C(aspect, context.player());
                return;
            }
            if (aspect.value != data) {
                aspect.value = data;
                aspect.saveToFile();
                AspectManager.syncChangedS2C(context.server(), aspect);
                if (aspect.onChangeServer != null) aspect.onChangeServer.accept(context.server());
            }
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
     * Registers the aspect for future use.<br>
     * Called automatically in the builder.
     */
    public static <T> Aspect<T> registerAspect(Aspect<T> aspect) {
        ASPECTS.put(aspect.id, aspect);
        if (aspect.category != null) CATEGORY_ENTRIES.computeIfAbsent(aspect.category, _ -> new ArrayList<>()).add(aspect);
        return aspect;
    }

    /**
     * Registers an aspect category.<br>
     * If this is not called, the category data will never be reflected in the menu.
     */
    public static AspectCategory registerCategory(AspectCategory category) {
        CATEGORIES.put(category.id, category);
        return category;
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

    /**
     * Registers an aspect link.<br>
     * If this is not called, the link will not show up in any category menu.
     */
    public static AspectLink registerLink(AspectLink aspectLink) {
        LINKS.put(aspectLink.id, aspectLink);
        return aspectLink;
    }
}