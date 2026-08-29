package dev.amymialee.amymialib.aspects;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.apache.logging.log4j.core.util.UuidUtil;

import java.util.Map;
import java.util.UUID;

public class AspectType<T> {
    public static final AspectType<Boolean> BOOLEAN_TRUE = new AspectType<>(ByteBufCodecs.BOOL, Codec.BOOL, true);
    public static final AspectType<Boolean> BOOLEAN_FALSE = new AspectType<>(ByteBufCodecs.BOOL, Codec.BOOL, false);
    public final StreamCodec<? super ByteBuf, T> stream;
    public final Codec<T> codec;
    public final Codec<Map<UUID, T>> playersCodec;
    public final T defaultValue;

    public AspectType(StreamCodec<? super ByteBuf, T> stream, Codec<T> codec, T defaultValue) {
        this.stream = stream;
        this.codec = codec;
        this.playersCodec = Codec.unboundedMap(UUIDUtil.CODEC, codec);
        this.defaultValue = defaultValue;
    }

    public void registerCommands(Aspect<T> aspect) {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(Commands.literal("aspect").requires(c -> c.isPlayer() && aspect.hasPermission.test(c.getPlayer()))
                .then(Commands.literal(aspect.id.toString())
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    ((Aspect<Boolean>) aspect).set(BoolArgumentType.getBool(ctx, "enabled"));
                                    return 1;
                                })
                        ))));
    }
}
