package dev.amymialee.amymialib.aspects;

import dev.amymialee.amymialib.AmyMialib;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record AspectSyncPayload(Identifier id, Object data, Optional<Map<UUID, Object>> playerValues) implements CustomPacketPayload {
    public static final Type<AspectSyncPayload> ID = new CustomPacketPayload.Type<>(AmyMialib.id("aspect"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AspectSyncPayload> CODEC = CustomPacketPayload.codec(AspectSyncPayload::write, AspectSyncPayload::fromBuf);

    public AspectSyncPayload(Identifier id, Object data) {
        this(id, data, Optional.empty());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public AspectSyncPayload(@NonNull Aspect aspect) {
        this(aspect.id, aspect.value, Optional.of(aspect.players));
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    private @SuppressWarnings("unchecked") static @NonNull AspectSyncPayload fromBuf(@NonNull RegistryFriendlyByteBuf buf) {
        var id = buf.readIdentifier();
        return new AspectSyncPayload(id, AspectManager.ASPECTS.get(id).type.stream.decode(buf), buf.readOptional(f -> f.readMap(UUIDUtil.STREAM_CODEC, AspectManager.ASPECTS.get(id).type.stream)));
    }

    private @SuppressWarnings("unchecked") void write(@NonNull RegistryFriendlyByteBuf buf) {
        buf.writeIdentifier(this.id);
        AspectManager.ASPECTS.get(id).type.stream.encode(buf, this.data);
        buf.writeOptional(this.playerValues, (f, m) -> f.writeMap(m, UUIDUtil.STREAM_CODEC, AspectManager.ASPECTS.get(id).type.stream));
    }

    public static class ClientHandler implements ClientPlayNetworking.PlayPayloadHandler<AspectSyncPayload> {
        @Override
        public void receive(@NotNull @NonNull AspectSyncPayload payload, ClientPlayNetworking.@NotNull Context context) {
            AspectManager.receiveClient(payload.id, payload.data, payload.playerValues, context);
        }
    }

    public static class ServerHandler implements ServerPlayNetworking.PlayPayloadHandler<AspectSyncPayload> {
        @Override
        public void receive(@NotNull @NonNull AspectSyncPayload payload, ServerPlayNetworking.@NotNull Context context) {
            AspectManager.receiveServer(payload.id, payload.data, context);
        }
    }
}