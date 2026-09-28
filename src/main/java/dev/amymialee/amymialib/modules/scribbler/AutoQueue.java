package dev.amymialee.amymialib.modules.scribbler;

import com.mojang.datafixers.util.Pair;
import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.modules.scribbler.scribbles.AutoScribbler;
import dev.amymialee.amymialib.modules.scribbler.scribbles.EarlyScribble;
import dev.amymialee.amymialib.modules.scribbler.scribbles.LateScribble;
import dev.amymialee.amymialib.modules.scribbler.scribbles.Scribble;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@ApiStatus.Internal
public final class AutoQueue {
    private static final Set<Pair<Scribbler, String>> AUTO_QUEUE = new HashSet<>();
    private static boolean locked;
    private static boolean ran;
    private static final Queue<AutoScribbler> AUTO_SCRIBBLES = new ConcurrentLinkedQueue<>();
    private static boolean scribbled;
    private static final Queue<Scribbler> SCRIBBLERS = new ConcurrentLinkedQueue<>();

    public static void clearDatagen() {
        SCRIBBLERS.forEach(scribbler -> scribbler.dossierData.clear());
    }

    public static void forEach(BiConsumer<Scribbler, String> consumer) {
        locked = true;
        AUTO_QUEUE.forEach(pair -> consumer.accept(pair.getFirst(), pair.getSecond()));
        ran = true;
    }

    public static void forAny(Scribbler scribbler, Consumer<String> consumer) {
        locked = true;
        AUTO_QUEUE.forEach(pair -> { if (pair.getFirst() == scribbler) consumer.accept(pair.getSecond()); });
        ran = true;
    }

    public static boolean findAny(Scribbler scribbler, Function<String, Boolean> finder) {
        locked = true;
        var result = AUTO_QUEUE.stream().anyMatch(pair -> pair.getFirst() == scribbler && finder.apply(pair.getSecond()));
        ran = true;
        return result;
    }

    public static void add(Scribbler scribbler, String text) {
        AUTO_QUEUE.add(Pair.of(scribbler, text));
        var error = ran ? "AutoQueue found new auto tasks after completion, make sure to run auto() in ModInitializer.onInitialize!" :
                locked ? "AutoQueue found new auto tasks while running, make sure to run auto() in ModInitializer.onInitialize!" : "";
        if (error.isEmpty()) return;
        AmyMialib.LOGGER.error(error);
        throw new RuntimeException(error, null, true, false) {};
    }

    public static void add(Scribbler scribbler) {
        SCRIBBLERS.add(scribbler);
    }

    public static void initializeScribbles() {
        var forNext = new ConcurrentLinkedQueue<AutoScribbler>();
        AutoScribbler auto;

        while ((auto = AUTO_SCRIBBLES.poll()) != null) {
            forNext.add(auto);
            if (auto instanceof EarlyScribble before) before.before();
        }
        AUTO_SCRIBBLES.clear();
        AUTO_SCRIBBLES.addAll(forNext);
        forNext.clear();

        while ((auto = AUTO_SCRIBBLES.poll()) != null) {
            forNext.add(auto);
            if (!(auto instanceof Scribble<?> scribble)) continue;
            scribble.holder();
            if (scribble.get() instanceof Item item && scribble.scribbler.creativeTab != null) CreativeModeTabEvents.modifyOutputEvent(scribble.scribbler.creativeTab).register(output -> output.accept(item));
        }
        AUTO_SCRIBBLES.clear();
        AUTO_SCRIBBLES.addAll(forNext);
        forNext.clear();

        while ((auto = AUTO_SCRIBBLES.poll()) != null) {
            forNext.add(auto);
            if (auto instanceof LateScribble after) after.after();
        }
        AUTO_SCRIBBLES.clear();
        AUTO_SCRIBBLES.addAll(forNext);
        forNext.clear();

        scribbled = true;
    }

    public static void addScribble(AutoScribbler auto) {
        if (scribbled) AmyMialib.LOGGER.warn("AutoQueue found new AutoScribble {} after running, this might cause a game crash if registries are frozen!", auto.debugName());
        AUTO_SCRIBBLES.add(auto);
    }
}