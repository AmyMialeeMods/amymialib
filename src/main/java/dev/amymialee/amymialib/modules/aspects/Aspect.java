package dev.amymialee.amymialib.modules.aspects;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.modules.scribbler.auto.ScribbleInit;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.awt.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ScribbleInit
public final class Aspect<T> {
    public static final int CLIENT_COLOUR = new Color(0xBD6868).getRGB();
    public static final Component CLIENT_ONLY = Component.translatable(AmyMialib.SCRIBBLER.translate(AmyMialib.id("aspects.client_only").toShortLanguageKey(), "[Client Only]")).withColor(CLIENT_COLOUR);
    public final Identifier id;
    public final AspectType<T> type;
    public final AspectSaveLocation saveLocation;
    public final AspectIcon<Aspect<T>> icon;
    public final String nameKey;
    public final String descriptionKey;
    public final Component name;
    public final Component description;
    public final Supplier<Path> filePath;
    public final Permission permission;
    public final @Nullable Consumer<MinecraftServer> onChangeServer;
    public final @Nullable Runnable onChangeClient;
    public final @Nullable Identifier category;
    public final T defaultValue;
    T value;

    private Aspect(
            T defaultValue,
            Identifier id,
            AspectType<T> type,
            AspectSaveLocation saveLocation,
            String nameKey,
            String descriptionKey,
            AspectIcon<Aspect<T>> icon,
            Permission permission,
            @Nullable BiConsumer<Aspect<T>, MinecraftServer> onChangeServer,
            @Nullable Consumer<Aspect<T>> onChangeClient,
            @Nullable Identifier category) {
        this.defaultValue = defaultValue;
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
        this.filePath = () -> this.saveLocation.resolve(id.toString().replace(":", "_") + ".json");
        this.permission = permission;
        this.onChangeServer = onChangeServer == null ? null : s -> onChangeServer.accept(this, s);
        this.onChangeClient = onChangeClient == null ? null : () -> onChangeClient.accept(this);
        this.category = category;
        this.type.commands(this);
        AspectManager.ASPECTS.put(this.id, this);
        if (this.category != null) AspectManager.CATEGORY_ENTRIES.computeIfAbsent(this.category, _ -> new ArrayList<>()).add(this);
    }

    /**
     * Players with relevant permissions can change config Aspects at any time.
     */
    public static <T> AspectBuilder<T> config(T defaultValue, Identifier id, AspectType<T> type) {
        return new AspectBuilder<>(defaultValue, id, type, AspectSaveLocation.CONFIG);
    }

    /**
     * Players can change client Aspects at any time.
     */
    public static <T> AspectBuilder<T> client(T defaultValue, Identifier id, AspectType<T> type) {
        return new AspectBuilder<>(defaultValue, id, type, AspectSaveLocation.CLIENT);
    }

    public T get() {
        return this.value;
    }

    void set(T value) {
        this.value = value;
    }

    void set(MinecraftServer server, T value) {
        this.value = value;
        AspectFileManager.save(this);
        AspectManager.syncChangedS2C(server, this);
        if (onChangeServer != null) onChangeServer.accept(server);
    }

    /**
     * {@link Minecraft} included to prevent calling this on server, and for potential future use.
     */
    void set(Minecraft ignoredClient, T value) {
        this.value = value;
        AspectFileManager.save(this);
        if (onChangeClient != null) onChangeClient.run();
    }

    public void saveToFile() {
        AspectFileManager.save(this);
    }

    public void loadFromFile(T value) {
        this.value = value;
    }

    public void onDrag(double xProgress) {

    }

    public void onClick(MouseButtonEvent event) {

    }

    public String toString() {
        return "Tuning:[" + this.id.toString() + "=" + this.type.toString.apply(this.get()) + "]";
    }

    public static class AspectBuilder<T> {
        private final Identifier id;
        private final AspectType<T> type;
        private final AspectSaveLocation saveLocation;
        public final String nameKey;
        public final String descriptionKey;
        private final List<AspectIcon<Aspect<T>>> icons = new ArrayList<>();
        private Permission permission;
        private @Nullable BiConsumer<Aspect<T>, MinecraftServer> onChangeServer;
        private @Nullable Consumer<Aspect<T>> onChangeClient;
        private @Nullable Identifier category;
        public final T defaultValue;

        protected AspectBuilder(T defaultValue, Identifier id, AspectType<T> type, AspectSaveLocation saveLocation) {
            this.defaultValue = defaultValue;
            this.id = id;
            this.type = type;
            this.saveLocation = saveLocation;
            this.nameKey = id.toLanguageKey("amymialib.aspect");
            this.descriptionKey = id.toLanguageKey("amymialib.aspect", "description");
            this.permission = switch (saveLocation) {
                case CONFIG -> Permissions.COMMANDS_GAMEMASTER;
                case CLIENT -> new Permission.HasCommandLevel(PermissionLevel.ALL);
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

        public AspectBuilder<T> permission(Permission permission) {
            this.permission = permission;
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
            return new Aspect<>(
                    this.defaultValue,
                    this.id,
                    this.type,
                    this.saveLocation,
                    this.nameKey,
                    this.descriptionKey,
                    icon,
                    this.permission,
                    this.onChangeServer,
                    this.onChangeClient,
                    this.category);
        }
    }
}