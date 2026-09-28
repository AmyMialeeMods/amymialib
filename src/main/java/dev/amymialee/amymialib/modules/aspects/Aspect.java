package dev.amymialee.amymialib.modules.aspects;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleInit;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@ScribbleInit
public final class Aspect<T> {
    public static final int CLIENT_COLOUR = new Color(0xBD6868).getRGB();
    public static final Component CLIENT_ONLY = Component.translatable(AmyMialib.SCRIBBLER.translation(AmyMialib.id("aspects.client_only").toShortLanguageKey(), "[Client Only]")).withColor(CLIENT_COLOUR);
    public final Identifier id;
    public final AspectType<T> type;
    public final AspectSaveLocation saveLocation;
    public final AspectIcon<Aspect<T>> icon;
    public final String nameKey;
    public final String descriptionKey;
    public final Component name;
    public final Component description;
    public final Path filePath;
    public final Predicate<Player> hasPermission;
    public final @Nullable Consumer<MinecraftServer> onChangeServer;
    public final @Nullable Runnable onChangeClient;
    public final @Nullable Identifier category;
    T value;

    private Aspect(
            Identifier id,
            AspectType<T> type,
            AspectSaveLocation saveLocation,
            String nameKey,
            String descriptionKey,
            AspectIcon<Aspect<T>> icon,
            Predicate<Player> hasPermission,
            @Nullable BiConsumer<Aspect<T>, MinecraftServer> onChangeServer,
            @Nullable Consumer<Aspect<T>> onChangeClient,
            @Nullable Identifier category) {
        this.id = id;
        this.type = type;
        this.saveLocation = saveLocation;
        this.icon = icon;
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.name = Component.translatable(this.nameKey);
        var name = this.id.getNamespace();
        var container = FabricLoader.getInstance().getModContainer(this.id.getNamespace());
        if (container.isPresent()) name = container.get().getMetadata().getName();
        var description = Component.translatable(this.descriptionKey);
        if (this.saveLocation == AspectSaveLocation.CLIENT) description = description.append("\n").append(CLIENT_ONLY);
        this.description = description.append("\n").append(Component.literal(name).withStyle(ChatFormatting.BLUE));
        this.filePath = this.saveLocation.resolve(id.toString().replace(":", "_") + ".json");
        this.hasPermission = hasPermission;
        this.onChangeServer = onChangeServer == null ? null : s -> onChangeServer.accept(this, s);
        this.onChangeClient = onChangeClient == null ? null : () -> onChangeClient.accept(this);
        this.category = category;
        this.value = this.type.defaultValue;
    }

    /**
     * Players with relevant permissions can change config Aspects at any time.
     */
    public static <T> AspectBuilder<T> config(Identifier id, AspectType<T> type) {
        return new AspectBuilder<>(id, type, AspectSaveLocation.CONFIG);
    }

    /**
     * Players can change client Aspects at any time.
     */
    public static <T> AspectBuilder<T> client(Identifier id, AspectType<T> type) {
        return new AspectBuilder<>(id, type, AspectSaveLocation.CLIENT);
    }

    public T get() {
        return this.value;
    }

    void set(T value) {
        this.value = value;
    }

    public String toString() {
        return this.type.toString(this.value);
    }

    public void saveToFile() {
        AspectFileManager.save(this);
    }

    public void loadFromFile(T value) {
        this.value = value;
    }

    public boolean hasPermission(Player player) {
        return this.hasPermission.test(player);
    }

    public void onDrag(double xProgress) {

    }

    public void onClick(MouseButtonEvent event) {

    }

    public static class AspectBuilder<T> {
        private final Identifier id;
        private final AspectType<T> type;
        private final AspectSaveLocation saveLocation;
        public final String nameKey;
        public final String descriptionKey;
        private final List<AspectIcon<Aspect<T>>> icons = new ArrayList<>();
        private Predicate<Player> hasPermission;
        private @Nullable BiConsumer<Aspect<T>, MinecraftServer> onChangeServer;
        private @Nullable Consumer<Aspect<T>> onChangeClient;
        private @Nullable Identifier category;

        protected AspectBuilder(Identifier id, AspectType<T> type, AspectSaveLocation saveLocation) {
            this.id = id;
            this.type = type;
            this.saveLocation = saveLocation;
            this.nameKey = id.toLanguageKey("amymialib.aspect");
            this.descriptionKey = id.toLanguageKey("amymialib.aspect", "description");
            this.hasPermission = switch (saveLocation) {
                case CONFIG -> p -> p.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
                case CLIENT -> _ -> true;
            };
        }

        public AspectBuilder<T> icon(AspectIcon<Aspect<T>> icon) {
            this.icons.add(icon);
            return this;
        }

        public AspectBuilder<T> icon(Item item) {
            return this.icon(item::getDefaultInstance);
        }

        public AspectBuilder<T> icon(Supplier<ItemStack> stack) {
            this.icons.add(new AspectIcon.AspectIconStack<>(stack));
            return this;
        }

        public AspectBuilder<T> icon(Identifier texture, int width, int height, float u0, float u1, float v0, float v1) {
            this.icons.add(new AspectIcon.AspectIconTexture<>(texture, width, height, u0, u1, v0, v1));
            return this;
        }

        public AspectBuilder<T> icon(Identifier texture, int width, int height) {
            this.icons.add(new AspectIcon.AspectIconSprite<>(texture, width, height));
            return this;
        }

        public AspectBuilder<T> permission(Predicate<Player> hasPermission) {
            this.hasPermission = hasPermission;
            return this;
        }

        public AspectBuilder<T> onChangeServer(BiConsumer<Aspect<T>, MinecraftServer> onChangeServer) {
            this.onChangeServer = onChangeServer;
            return this;
        }

        public AspectBuilder<T> onChangeClient(Consumer<Aspect<T>> onChangeClient) {
            this.onChangeClient = onChangeClient;
            return this;
        }

        public AspectBuilder<T> category(Identifier category) {
            this.category = category;
            return this;
        }

        public AspectBuilder<T> category(AspectCategory category) {
            this.category = category.id;
            return this;
        }

        public Aspect<T> build() {
            AspectIcon<Aspect<T>> icon = this.icons.isEmpty() ? new AspectIcon.AspectIconEmpty<>()
                                                              : (this.icons.size() == 1 ? this.icons.getFirst() : new AspectIcon.AspectMultiIcon<>(
                                                                 this.icons));
            return AspectManager.registerAspect(new Aspect<>(
                    this.id,
                    this.type,
                    this.saveLocation,
                    this.nameKey,
                    this.descriptionKey,
                    icon,
                    this.hasPermission,
                    this.onChangeServer,
                    this.onChangeClient,
                    this.category));
        }
    }
}