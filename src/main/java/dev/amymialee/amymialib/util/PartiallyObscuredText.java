package dev.amymialee.amymialib.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Random;

public interface PartiallyObscuredText {
    Random RANDOM = new Random();

    static MutableComponent obscure(Component text, float power) {
        RANDOM.setSeed((System.nanoTime() % 1000000L) / 600L);
        RANDOM.nextInt(RANDOM.nextInt(256));
        var base = Component.empty();
        for (var c : text.getString().toCharArray())
            if (RANDOM.nextFloat() > (1f - power))
                base.append(Component.literal(String.valueOf(c)).withStyle(ChatFormatting.OBFUSCATED));
            else base.append(Component.literal(String.valueOf(c)));
        return base;
    }
}