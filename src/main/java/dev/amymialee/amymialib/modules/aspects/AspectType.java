package dev.amymialee.amymialib.modules.aspects;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import dev.amymialee.amymialib.modules.AMEnvironmentType;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;

import java.util.function.BiFunction;
import java.util.function.Function;

public class AspectType<T> {
    public static final AspectType<Boolean> BOOLEAN = new AspectType<>(ByteBufCodecs.BOOL, Codec.BOOL, BoolArgumentType.bool(), BoolArgumentType::getBool, v -> v ? "True ✔" : "False ✖", v -> v);
    public static final AspectType<Float> PERCENT = new AspectType<>(ByteBufCodecs.FLOAT, Codec.FLOAT, FloatArgumentType.floatArg(0, 100), (context, name) -> FloatArgumentType.getFloat(context, name) / 100f, v -> String.format("%.0f%%", v * 100f), v -> Mth.clamp(v, 0f, 1f));
    public static final AspectType<String> STRING = new AspectType<>(ByteBufCodecs.STRING_UTF8, Codec.STRING, StringArgumentType.greedyString(), StringArgumentType::getString, v -> v, v -> v);
    public final StreamCodec<? super ByteBuf, T> stream;
    public final Codec<T> codec;
    public final ArgumentType<T> argumentType;
    public final BiFunction<CommandContext<?>, String, T> argumentExtractor;
    public final Function<T, String> toString;
    public final Function<T, T> validator;

    public AspectType(StreamCodec<? super ByteBuf, T> stream, Codec<T> codec, ArgumentType<T> argumentType, BiFunction<CommandContext<?>, String, T> argumentExtractor, Function<T, String> toString, Function<T, T> validator) {
        this.stream = stream;
        this.codec = codec;
        this.argumentType = argumentType;
        this.argumentExtractor = argumentExtractor;
        this.toString = toString;
        this.validator = validator;
    }

    public String toString(T value) {
        return this.toString.apply(value);
    }

    public void commands(Aspect<T> Aspect) {
        if (Aspect.saveLocation == AspectSaveLocation.CONFIG) {
            this.registerServerCommand(Aspect);
        } else if (AMEnvironmentType.getEnv().isClient) {
            this.registerClientCommand(Aspect);
        }
    }

    private void registerServerCommand(Aspect<T> aspect) {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
            var node = dispatcher.register(Commands.literal("Aspect").requires(s -> s.permissions().hasPermission(aspect.permission))
                    .then(Commands.literal(aspect.id.toString())
                            .then(Commands.argument("value", argumentType)
                                    .executes(ctx -> {
                                        var value = validator.apply(argumentExtractor.apply(ctx, "value"));
                                        if (value == null) {
                                            ctx.getSource().sendFailure(Component.translatable("commands.Aspect.fail", aspect.name));
                                            return 0;
                                        }
                                        aspect.set(ctx.getSource().getServer(), value);
                                        ctx.getSource().sendSuccess(() -> Component.translatable("commands.Aspect.set", aspect.name, this.toString.apply(aspect.get())), true);
                                        return 1;
                                    })
                            ).executes(ctx -> {
                                ctx.getSource().sendSuccess(() -> Component.translatable("commands.Aspect.query", aspect.name, this.toString.apply(aspect.get())), false);
                                ctx.getSource().sendSuccess(() -> aspect.description, false);
                                return 1;
                            })));
            dispatcher.register(Commands.literal("rfts:Aspect").redirect(node));
        });
    }

    private void registerClientCommand(Aspect<T> aspect) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> {
            var node = dispatcher.register(ClientCommands.literal("Aspect").requires(s -> s.permissions().hasPermission(aspect.permission))
                    .then(ClientCommands.literal(aspect.id.toString())
                            .then(ClientCommands.argument("value", argumentType)
                                    .executes(ctx -> {
                                        var value = validator.apply(argumentExtractor.apply(ctx, "value"));
                                        if (value == null) {
                                            ctx.getSource().sendError(Component.translatable("commands.Aspect.fail", aspect.name));
                                            return 0;
                                        }
                                        aspect.set(ctx.getSource().getClient(), value);
                                        ctx.getSource().sendFeedback(Component.translatable("commands.Aspect.set", aspect.name, this.toString.apply(aspect.get())));
                                        return 1;
                                    })
                            ).executes(ctx -> {
                                ctx.getSource().sendFeedback(Component.translatable("commands.Aspect.query", aspect.name, this.toString.apply(aspect.get())));
                                ctx.getSource().sendFeedback(aspect.description);
                                return 1;
                            })));
            dispatcher.register(ClientCommands.literal("rfts:Aspect").redirect(node));
        });
    }
}