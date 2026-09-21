package dev.amymialee.amymialib.modules.aspects;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class AspectType<T> {
    public static final AspectType<Boolean> BOOLEAN_TRUE = new AspectType<>(ByteBufCodecs.BOOL, Codec.BOOL, b -> b ? "True ✔" : "False ✖", true);
    public static final AspectType<Boolean> BOOLEAN_FALSE = new AspectType<>(ByteBufCodecs.BOOL, Codec.BOOL, b -> b ? "True ✔" : "False ✖", false);
    public static final AspectType<Float> FLOAT = new AspectType<>(ByteBufCodecs.FLOAT, Codec.FLOAT, f -> String.format("%.2f", f), 1.f);
    public final StreamCodec<? super ByteBuf, T> stream;
    public final Codec<T> codec;
    public final Function<T, String> toString;
    public final T defaultValue;

    public AspectType(StreamCodec<? super ByteBuf, T> stream, Codec<T> codec, Function<T, String> toString, T defaultValue) {
        this.stream = stream;
        this.codec = codec;
        this.toString = toString;
        this.defaultValue = defaultValue;
    }

    public String toString(T value) {
        return this.toString.apply(value);
    }
}