package dev.amymialee.amymialib.aspects;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.amymialee.amymialib.AmyMialib;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class AspectManager {
    public static MinecraftServer SERVER;

    public static final Map<Identifier, Aspect> ASPECTS = new HashMap<>();
    public static final Map<Identifier, Aspect> CONFIG_ASPECTS = new HashMap<>();
    public static final Map<Identifier, Aspect> WORLD_ASPECTS = new HashMap<>();
    public static final Map<Identifier, Aspect> USER_ASPECTS = new HashMap<>();
    public static final Map<Identifier, Aspect> CLIENT_ASPECTS = new HashMap<>();

    public static final Map<Identifier, AspectCategory> CATEGORIES = new HashMap<>();
    public static final Map<Identifier, List<Aspect>> CATEGORY_ENTRIES = new HashMap<>();

    // only write to file if server is not null, or its client/user
    // store temporary values for stuff thats only synced

    @SuppressWarnings({"unchecked", "OptionalUsedAsFieldOrParameterType"})
    public static <T> void receiveClient(Identifier id, T data, Optional<Map<UUID, T>> playerValues, ClientPlayNetworking.@NotNull Context context) {
        try {
            var aspect = (Aspect<T>) AspectManager.ASPECTS.get(id);
            switch (aspect.saveLocation) { case CLIENT, USER -> { return; } }
            aspect.set(data, playerValues);
        } catch (Exception e) {
            AmyMialib.LOGGER.error("Failed to receive aspect {} from server", id, e);
        }
    }

    @SuppressWarnings({"unchecked", "OptionalUsedAsFieldOrParameterType"})
    public static <T> void receiveServer(Identifier id, T data, Optional<Map<UUID, T>> playerValues, ServerPlayNetworking.@NotNull Context context) {
        try {
            var aspect = (Aspect<T>) AspectManager.ASPECTS.get(id);
            if (!aspect.hasPermission.test(context.player())) return;
            aspect.set(data, playerValues);
        } catch (Exception e) {
            AmyMialib.LOGGER.error("Failed to receive aspect {} from {}", id, context.player().getGameProfile().name(), e);
        }
    }

    public static <T> void onChange(@NonNull Aspect<T> aspect) {
        if (aspect.onChange != null) aspect.onChange.run();
        AspectManager.save(aspect);
        AspectManager.sync(aspect);
    }

    public static void saveAll() {
        ASPECTS.values().forEach(AspectManager::save);
    }

    public static <T> void save(@NonNull Aspect<T> aspect) {
        if (switch (aspect.saveLocation) {
            case CLIENT, USER -> SERVER != null && !(SERVER instanceof IntegratedServer);
            case CONFIG, WORLD -> SERVER == null;
        }) return;
        var config = new JsonObject();
        config.add("value", aspect.type.codec.encodeStart(JsonOps.INSTANCE, aspect.value).getOrThrow());
        config.add("players", aspect.type.playersCodec.encodeStart(JsonOps.INSTANCE, aspect.players).getOrThrow());
        try (var fileWriter = Files.newBufferedWriter(aspect.filePath)) {
            fileWriter.write(AmyMialib.GSON.toJson(config));
        } catch (IOException e) {
            AmyMialib.LOGGER.error("Couldn't write Aspect file for {}", aspect.id, e);
        }
    }

    public static void loadAll() {
        ASPECTS.values().forEach(AspectManager::load);
    }

    public static <T> void load(@NonNull Aspect<T> aspect) {
        JsonObject data = null;
        try {
            data = AmyMialib.GSON.fromJson(Files.readString(aspect.filePath), JsonObject.class);
        } catch (Exception _) {}
        if (data == null) return;
        var value = data.get("value");
        if (value != null) aspect.type.codec.decode(JsonOps.INSTANCE, value).result().ifPresent(v -> aspect.set(v.getFirst()));
        var players = data.get("players");
        if (players != null) aspect.type.playersCodec.decode(JsonOps.INSTANCE, players).result().ifPresent(m -> {
            aspect.players.clear();
            aspect.players.putAll(m.getFirst());
        });
    }

    public static <T> void sync(@NonNull Aspect<T> aspect) {
        if (SERVER != null) {
            SERVER.getPlayerList().getPlayers().forEach(p -> ServerPlayNetworking.send(p, new AspectSyncPayload(aspect)));
        }
    }

    public static <T> @NonNull Aspect<T> registerAspect(@NonNull AspectSaveLocation saveLocation, Aspect<T> aspect) {
        ASPECTS.put(aspect.id, aspect);
        switch (saveLocation) {
            case CONFIG -> CONFIG_ASPECTS.put(aspect.id, aspect);
            case WORLD -> WORLD_ASPECTS.put(aspect.id, aspect);
            case USER -> USER_ASPECTS.put(aspect.id, aspect);
            case CLIENT -> CLIENT_ASPECTS.put(aspect.id, aspect);
        }
        if (aspect.category != null) CATEGORY_ENTRIES.computeIfAbsent(aspect.category, _ -> new ArrayList<>()).add(aspect);
        aspect.type.registerCommands(aspect);
        return aspect;
    }

    public static @NonNull AspectCategory registerCategory(AspectCategory category) {
        CATEGORIES.put(category.id, category);
        return category;
    }
}