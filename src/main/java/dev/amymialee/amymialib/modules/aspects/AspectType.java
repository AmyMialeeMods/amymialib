package dev.amymialee.amymialib.modules.aspects;

import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.modules.AMEnvironmentType;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleInit;
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

@ScribbleInit
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

    public static AspectType<Integer> intType(int min, int max) {
        return new AspectType<>(ByteBufCodecs.INT, Codec.INT, IntegerArgumentType.integer(min, max), IntegerArgumentType::getInteger, v -> String.format("%d", v), v -> Mth.clamp(v, min, max));
    }

    public static AspectType<Float> floatType(float min, float max) {
        return new AspectType<>(ByteBufCodecs.FLOAT, Codec.FLOAT, FloatArgumentType.floatArg(min, max), FloatArgumentType::getFloat, v -> String.format("%.2f", v), v -> Mth.clamp(v, min, max));
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

    public static final String COMMAND_FAIL = AmyMialib.SCRIBBLER.translate("commands.amymialib.aspect.fail", "Failed to set aspect %s");
    public static final String COMMAND_SET = AmyMialib.SCRIBBLER.translate("commands.amymialib.aspect.set", "Set aspect %s to %s");
    public static final String COMMAND_QUERY = AmyMialib.SCRIBBLER.translate("commands.amymialib.aspect.query", "Aspect %s is currently set to %s");

    private void registerServerCommand(Aspect<T> aspect) {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(Commands.literal("aspect").requires(s -> s.permissions().hasPermission(aspect.permission))
                .then(Commands.literal(aspect.id.getNamespace())
                        .then(Commands.literal(aspect.id.getPath())
                                .then(Commands.argument("value", argumentType)
                                        .executes(ctx -> {
                                            var value = validator.apply(argumentExtractor.apply(ctx, "value"));
                                            if (value == null) {
                                                ctx.getSource().sendFailure(Component.translatable(COMMAND_FAIL, aspect.name));
                                                return 0;
                                            }
                                            aspect.set(ctx.getSource().getServer(), value);
                                            ctx.getSource().sendSuccess(() -> Component.translatable(COMMAND_SET, aspect.name, this.toString.apply(aspect.get())), true);
                                            return 1;
                                        })
                                ).executes(ctx -> {
                                    ctx.getSource().sendSuccess(() -> Component.translatable(COMMAND_QUERY, aspect.name, this.toString.apply(aspect.get())), false);
                                    ctx.getSource().sendSuccess(() -> aspect.description, false);
                                    return 1;
                                })))));
    }

    private void registerClientCommand(Aspect<T> aspect) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommands.literal("aspect").requires(s -> s.permissions().hasPermission(aspect.permission))
                .then(ClientCommands.literal(aspect.id.getNamespace())
                        .then(ClientCommands.literal(aspect.id.getPath())
                                .then(ClientCommands.argument("value", argumentType)
                                        .executes(ctx -> {
                                            var value = validator.apply(argumentExtractor.apply(ctx, "value"));
                                            if (value == null) {
                                                ctx.getSource().sendError(Component.translatable(COMMAND_FAIL, aspect.name));
                                                return 0;
                                            }
                                            aspect.set(ctx.getSource().getClient(), value);
                                            ctx.getSource().sendFeedback(Component.translatable(COMMAND_SET, aspect.name, this.toString.apply(aspect.get())));
                                            return 1;
                                        })
                                ).executes(ctx -> {
                                    ctx.getSource().sendFeedback(Component.translatable(COMMAND_QUERY, aspect.name, this.toString.apply(aspect.get())));
                                    ctx.getSource().sendFeedback(aspect.description);
                                    return 1;
                                })))));
    }
}