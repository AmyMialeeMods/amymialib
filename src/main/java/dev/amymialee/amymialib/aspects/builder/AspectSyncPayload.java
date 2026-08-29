package dev.amymialee.amymialib.aspects.builder;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.Aspect;
import dev.amymialee.amymialib.aspects.AspectManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record AspectSyncPayload(Type<AspectSyncPayload> type, Identifier id, Object data) implements CustomPacketPayload {
    public static final Type<AspectSyncPayload> ID_CHANGED = new CustomPacketPayload.Type<>(AmyMialib.id("aspect_changed"));
    public static final Type<AspectSyncPayload> ID_UNCHANGED = new CustomPacketPayload.Type<>(AmyMialib.id("aspect_unchanged"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AspectSyncPayload> CODEC = CustomPacketPayload.codec(AspectSyncPayload::write, AspectSyncPayload::fromBuf);

    public static @NonNull AspectSyncPayload c2s(@NonNull Aspect<?> aspect, Object data) {
        return new AspectSyncPayload(ID_CHANGED, aspect.id, data);
    }

    public static @NonNull AspectSyncPayload changed(@NonNull Aspect<?> aspect) {
        return new AspectSyncPayload(ID_CHANGED, aspect.id, aspect.get());
    }

    public static @NonNull AspectSyncPayload unchanged(@NonNull Aspect<?> aspect) {
        return new AspectSyncPayload(ID_UNCHANGED, aspect.id, aspect.get());
    }

    private @SuppressWarnings("unchecked") static @NonNull AspectSyncPayload fromBuf(@NonNull RegistryFriendlyByteBuf buf) {
        var type = buf.readBoolean() ? ID_CHANGED : ID_UNCHANGED;
        var id = buf.readIdentifier();
        return new AspectSyncPayload(type, id, AspectManager.ASPECTS.get(id).type.stream.decode(buf));
    }

    private @SuppressWarnings("unchecked") void write(@NonNull RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(type == ID_CHANGED);
        buf.writeIdentifier(this.id);
        AspectManager.ASPECTS.get(id).type.stream.encode(buf, this.data);
    }
}