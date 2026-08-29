package dev.amymialee.amymialib.aspects.builder;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class AspectType<T> {
    public static final AspectType<Boolean> BOOLEAN_TRUE = new AspectType<>(ByteBufCodecs.BOOL, Codec.BOOL, true);
    public static final AspectType<Boolean> BOOLEAN_FALSE = new AspectType<>(ByteBufCodecs.BOOL, Codec.BOOL, false);
    public final StreamCodec<? super ByteBuf, T> stream;
    public final Codec<T> codec;
    public final T defaultValue;

    public AspectType(StreamCodec<? super ByteBuf, T> stream, Codec<T> codec, T defaultValue) {
        this.stream = stream;
        this.codec = codec;
        this.defaultValue = defaultValue;
    }
}