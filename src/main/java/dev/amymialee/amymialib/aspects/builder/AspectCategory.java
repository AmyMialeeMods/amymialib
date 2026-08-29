package dev.amymialee.amymialib.aspects.builder;

import dev.amymialee.amymialib.aspects.AspectManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class AspectCategory {
    public final Identifier id;
    public final AspectIcon icon;
    public final AspectBackground background;
    public final String nameKey;
    public final String descriptionKey;
    public final Component name;
    public final Component description;

    private AspectCategory(@NonNull Identifier id, AspectIcon icon, AspectBackground background) {
        this.id = id;
        this.icon = icon;
        this.background = background;
        this.nameKey = id.toLanguageKey("amymialib.aspect.category");
        this.descriptionKey = id.toLanguageKey("amymialib.aspect.category", "description");
        this.name = Component.translatable(this.nameKey);
        this.description = Component.translatable(this.descriptionKey).append("\n").append(Component.literal(id.getNamespace()).withStyle(ChatFormatting.BLUE));
    }

    public static @NotNull AspectCategoryBuilder create(Identifier id) {
        return new AspectCategoryBuilder(id);
    }

    public static class AspectCategoryBuilder {
        private final Identifier id;
        private final List<AspectIcon> icons = new ArrayList<>();
        private AspectBackground background = AspectBackground.EMPTY;

        protected AspectCategoryBuilder(Identifier id) {
            this.id = id;
        }

        public AspectCategoryBuilder icon(AspectIcon icon) {
            this.icons.add(icon);
            return this;
        }

        public AspectCategoryBuilder icon(@NonNull Item item) {
            return this.icon(item::getDefaultInstance);
        }

        public AspectCategoryBuilder icon(Supplier<ItemStack> stack) {
            this.icons.add(new AspectIcon.AspectIconStack(stack));
            return this;
        }

        public AspectCategoryBuilder icon(Identifier texture, int width, int height, float u0, float u1, float v0, float v1) {
            this.icons.add(new AspectIcon.AspectIconTexture(texture, width, height, u0, u1, v0, v1));
            return this;
        }

        public AspectCategoryBuilder icon(Identifier texture, int width, int height) {
            this.icons.add(new AspectIcon.AspectIconSprite(texture, width, height));
            return this;
        }

        public AspectCategoryBuilder background(AspectBackground background) {
            this.background = background;
            return this;
        }

        public AspectCategoryBuilder background(Identifier texture, int width, int height, float u0, float u1, float v0, float v1) {
            this.background = new AspectBackground.AspectBackgroundTexture(texture, width, height, u0, u1, v0, v1);
            return this;
        }

        public AspectCategoryBuilder background(Identifier texture, int width, int height) {
            this.background = new AspectBackground.AspectBackgroundSprite(texture, width, height);
            return this;
        }

        public AspectCategory build() {
            AspectIcon icon = icons.isEmpty() ? new AspectIcon.AspectIconEmpty() : (icons.size() == 1 ? icons.getFirst() : new AspectIcon.AspectMultiIcon(icons));
            return AspectManager.registerCategory(new AspectCategory(this.id, icon, this.background));
        }
    }
}