package dev.amymialee.amymialib.aspects;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.client.AspectIcon;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Aspect<T> {
    public final Identifier id;
    public final AspectSaveLocation saveLocation;
    public final AspectType<T> type;
    public final AspectIcon icon;
    public final String nameKey;
    public final String descriptionKey;
    public final Component name;
    public final Component description;
    public final Path filePath;
    public final Predicate<Player> hasPermission;
    public final Runnable onChange;
    public final Identifier category;
    final Map<UUID, T> players = new HashMap<>();
    T value;

    private Aspect(@NonNull Identifier id, AspectSaveLocation saveLocation, AspectType<T> type, AspectIcon icon, Predicate<Player> hasPermission, Consumer<Aspect<T>> onChange, Identifier category) {
        this.id = id;
        this.saveLocation = saveLocation;
        this.type = type;
        this.icon = icon;
        this.nameKey = id.toLanguageKey("amymialib.aspect");
        this.descriptionKey = id.toLanguageKey("amymialib.aspect", "description");
        this.name = Component.translatable(this.nameKey);
        this.description = Component.translatable(this.descriptionKey).append("\n").append(Component.literal(id.getNamespace()).withStyle(ChatFormatting.BLUE));
        this.filePath = this.saveLocation.resolve(id.toString().replace(":", "_") + ".json");
        this.hasPermission = hasPermission;
        this.onChange = onChange == null ? null : () -> onChange.accept(this);
        this.category = category;
        this.value = this.type.defaultValue;
    }

    /**
     * Players with relevant permissions can change config Aspects at any time.<br>
     * Config Aspects are stored in the config folder and will be shared between worlds.
     */
    public static <T> @NotNull AspectBuilder<T> config(Identifier id, AspectType<T> type) {
        return new AspectBuilder<>(id, type, AspectSaveLocation.CONFIG);
    }

    /**
     * Players with relevant permissions can change world Aspects while in a world or on a server.<br>
     * World Aspects are stored in the world folder and will not be shared between worlds.<br>
     * Functionally identical to config Aspects while on a server, unless you change the world folder.
     */
    public static <T> @NotNull AspectBuilder<T> world(Identifier id, AspectType<T> type) {
        return new AspectBuilder<>(id, type, AspectSaveLocation.WORLD);
    }

    /**
     * Players can change user Aspects at any time.<br>
     * User aspects are stored in AppData and are shared between instances.
     */
    public static <T> @NotNull AspectBuilder<T> user(Identifier id, AspectType<T> type) {
        return new AspectBuilder<>(id, type, AspectSaveLocation.USER);
    }

    /**
     * Players can change client Aspects at any time.<br>
     * Client Aspects are stored in the client's config folder.
     */
    public static <T> @NotNull AspectBuilder<T> client(Identifier id, AspectType<T> type) {
        return new AspectBuilder<>(id, type, AspectSaveLocation.CLIENT);
    }

    public T get() {
        return this.value;
    }

    public T get(@NonNull Player player) {
        return this.get(player.getUUID());
    }

    public T get(UUID player) {
        if (this.saveLocation.serverBased) {
            AmyMialib.LOGGER.error("Aspect {} is server based so does not have a user specific value.", this);
            return this.value;
        }
        return this.players.getOrDefault(player, this.type.defaultValue);
    }

    public @SuppressWarnings("OptionalUsedAsFieldOrParameterType") void set(T value, @NonNull Optional<Map<UUID, T>> playerValues) {
        if (playerValues.isPresent()) {
            this.players.clear();
            this.players.putAll(playerValues.get());
        }
        this.set(value);
    }

    public void set(T value) {
        this.value = value;
        AspectManager.onChange(this);
    }

    public void set(T value, @NonNull Player player) {
        this.set(value, player.getUUID());
    }

    public void set(T value, UUID player) {
        if (this.saveLocation.serverBased) {
            AmyMialib.LOGGER.error("Aspect {} is server based so does not have a user specific value.", this);
            return;
        }
        this.players.put(player, value);
        AspectManager.onChange(this);
    }

    public static class AspectBuilder<T> {
        private final Identifier id;
        private final AspectType<T> type;
        private final List<AspectIcon> icons = new ArrayList<>();
        private final AspectSaveLocation saveLocation;
        private Predicate<Player> hasPermission;
        private Consumer<Aspect<T>> onChange;
        private Identifier category;

        protected AspectBuilder(Identifier id, AspectType<T> type, @NonNull AspectSaveLocation saveLocation) {
            this.id = id;
            this.type = type;
            this.saveLocation = saveLocation;
            switch (saveLocation) {
                case CONFIG, WORLD -> this.hasPermission = p -> p.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
                case USER, CLIENT -> this.hasPermission = _ -> true;
            }
        }

        public AspectBuilder<T> icon(AspectIcon icon) {
            this.icons.add(icon);
            return this;
        }

        public AspectBuilder<T> icon(Item item) {
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

        public AspectBuilder<T> onChange(Consumer<Aspect<T>> onChange) {
            this.onChange = onChange;
            return this;
        }

        public AspectBuilder<T> category(Identifier category) {
            this.category = category;
            return this;
        }

        public Aspect<T> build() {
            AspectIcon icon = icons.isEmpty() ? new AspectIcon.AspectIconEmpty() : (icons.size() == 1 ? icons.getFirst() : new AspectIcon.AspectMultiIcon(icons));
            return AspectManager.registerAspect(this.saveLocation, new Aspect<>(this.id, this.saveLocation, this.type, icon, this.hasPermission, this.onChange, this.category));
        }
    }
}