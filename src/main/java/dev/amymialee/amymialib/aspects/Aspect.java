package dev.amymialee.amymialib.aspects;

import dev.amymialee.amymialib.aspects.builder.AspectFileManager;
import dev.amymialee.amymialib.aspects.builder.AspectIcon;
import dev.amymialee.amymialib.aspects.builder.AspectType;
import dev.amymialee.amymialib.aspects.builder.AspectSaveLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Aspect<T> {
    public final Identifier id;
    public final AspectType<T> type;
    public final AspectSaveLocation saveLocation;
    public final AspectIcon icon;
    public final String nameKey;
    public final String descriptionKey;
    public final Component name;
    public final Component description;
    public final Path filePath;
    public final Predicate<Player> hasPermission;
    public final Runnable onChangeServer;
    public final Runnable onChangeClient;
    public final Identifier category;
    T value;

    private Aspect(@NonNull Identifier id, AspectType<T> type, AspectSaveLocation saveLocation, String nameKey, String descriptionKey, AspectIcon icon, Predicate<Player> hasPermission, BiConsumer<Aspect<T>, MinecraftServer> onChangeServer, Consumer<Aspect<T>> onChangeClient, Identifier category) {
        this.id = id;
        this.type = type;
        this.saveLocation = saveLocation;
        this.icon = icon;
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.name = Component.translatable(this.nameKey);
        this.description = Component.translatable(this.descriptionKey).append("\n").append(Component.literal(id.getNamespace()).withStyle(ChatFormatting.BLUE));
        this.filePath = this.saveLocation.resolve(id.toString().replace(":", "_") + ".json");
        this.hasPermission = hasPermission;
        this.onChangeServer = onChangeServer == null ? null : () -> onChangeServer.accept(this, AspectManager.SERVER);
        this.onChangeClient = onChangeClient == null ? null : () -> onChangeClient.accept(this);
        this.category = category;
        this.value = this.type.defaultValue;
    }

    /**
     * Players with relevant permissions can change config Aspects at any time.
     */
    public static <T> @NotNull AspectBuilder<T> config(Identifier id, AspectType<T> type) {
        return new AspectBuilder<>(id, type, AspectSaveLocation.CONFIG);
    }

    /**
     * Players can change client Aspects at any time.
     */
    public static <T> @NotNull AspectBuilder<T> client(Identifier id, AspectType<T> type) {
        return new AspectBuilder<>(id, type, AspectSaveLocation.CLIENT);
    }

    public T get() {
        return this.value;
    }

    void set(T value) {
        this.value = value;
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

    public static class AspectBuilder<T> {
        private final Identifier id;
        private final AspectType<T> type;
        private final AspectSaveLocation saveLocation;
        public final String nameKey;
        public final String descriptionKey;
        private final List<AspectIcon> icons = new ArrayList<>();
        private Predicate<Player> hasPermission;
        private BiConsumer<Aspect<T>, MinecraftServer> onChangeServer;
        private Consumer<Aspect<T>> onChangeClient;
        private Identifier category;

        protected AspectBuilder(@NonNull Identifier id, AspectType<T> type, @NonNull AspectSaveLocation saveLocation) {
            this.id = id;
            this.type = type;
            this.saveLocation = saveLocation;
            this.nameKey = id.toLanguageKey("amymialib.aspect");
            this.descriptionKey = id.toLanguageKey("amymialib.aspect", "description");
            switch (saveLocation) {
                case CONFIG -> this.hasPermission = p -> p.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
                case CLIENT -> this.hasPermission = _ -> true;
            }
        }

        public AspectBuilder<T> icon(AspectIcon icon) {
            this.icons.add(icon);
            return this;
        }

        public AspectBuilder<T> icon(@NonNull Item item) {
            return this.icon(item::getDefaultInstance);
        }

        public AspectBuilder<T> icon(Supplier<ItemStack> stack) {
            this.icons.add(new AspectIcon.AspectIconStack(stack));
            return this;
        }

        public AspectBuilder<T> icon(Identifier texture, int width, int height, float u0, float u1, float v0, float v1) {
            this.icons.add(new AspectIcon.AspectIconTexture(texture, width, height, u0, u1, v0, v1));
            return this;
        }

        public AspectBuilder<T> icon(Identifier texture, int width, int height) {
            this.icons.add(new AspectIcon.AspectIconSprite(texture, width, height));
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

        public Aspect<T> build() {
            AspectIcon icon = icons.isEmpty() ? new AspectIcon.AspectIconEmpty() : (icons.size() == 1 ? icons.getFirst() : new AspectIcon.AspectMultiIcon(icons));
            return AspectManager.registerAspect(new Aspect<>(this.id, this.type, this.saveLocation, this.nameKey, this.descriptionKey, icon, this.hasPermission, this.onChangeServer, this.onChangeClient, this.category));
        }
    }
}