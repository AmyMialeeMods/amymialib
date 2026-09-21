package dev.amymialee.amymialib.modules.aspects;

import dev.amymialee.amymialib.AmyMialib;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AspectSyncPayload(Type<AspectSyncPayload> type, Identifier id, Object data) implements CustomPacketPayload {
    public static final Type<AspectSyncPayload> ID_CHANGED = new CustomPacketPayload.Type<>(AmyMialib.id("aspect_changed"));
    public static final Type<AspectSyncPayload> ID_UNCHANGED = new CustomPacketPayload.Type<>(AmyMialib.id("aspect_unchanged"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AspectSyncPayload> CODEC = CustomPacketPayload.codec(AspectSyncPayload::write, AspectSyncPayload::fromBuf);

    public static AspectSyncPayload c2s(Aspect<?> aspect, Object data) {
        return new AspectSyncPayload(ID_CHANGED, aspect.id, data);
    }

    public static AspectSyncPayload changed(Aspect<?> aspect) {
        return new AspectSyncPayload(ID_CHANGED, aspect.id, aspect.get());
    }

    public static AspectSyncPayload unchanged(Aspect<?> aspect) {
        return new AspectSyncPayload(ID_UNCHANGED, aspect.id, aspect.get());
    }

    private @SuppressWarnings("unchecked") static AspectSyncPayload fromBuf(RegistryFriendlyByteBuf buf) {
        var type = buf.readBoolean() ? ID_CHANGED : ID_UNCHANGED;
        var id = buf.readIdentifier();
        return new AspectSyncPayload(type, id, AspectManager.ASPECTS.get(id).type.stream.decode(buf));
    }

    private @SuppressWarnings("unchecked") void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(this.type == ID_CHANGED);
        buf.writeIdentifier(this.id);
        AspectManager.ASPECTS.get(this.id).type.stream.encode(buf, this.data);
    }
}