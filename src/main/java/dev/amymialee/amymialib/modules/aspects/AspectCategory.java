package dev.amymialee.amymialib.modules.aspects;

import com.google.common.collect.ImmutableList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class AspectCategory {
    public final Identifier id;
    public final String nameKey;
    public final String descriptionKey;
    public final AspectIcon<AspectCategory> icon;
    public final AspectBackground background;
    public final List<AspectLink> links;
    public final Component name;
    public final Component description;

    private AspectCategory(
            Identifier id,
            String nameKey,
            String descriptionKey,
            AspectIcon<AspectCategory> icon,
            AspectBackground background,
            List<AspectLink> links) {
        this.id = id;
        this.icon = icon;
        this.background = background;
        this.links = ImmutableList.copyOf(links);
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.name = Component.translatable(this.nameKey);
        var name = this.id.getNamespace();
        var container = FabricLoader.getInstance().getModContainer(this.id.getNamespace());
        if (container.isPresent()) name = container.get().getMetadata().getName();
        this.description = Component.translatable(this.descriptionKey).withColor(TextColor.GRAY).append("\n").append(Component.literal(name).withStyle(ChatFormatting.BLUE));
    }

    public static AspectCategoryBuilder of(Identifier id) {
        return new AspectCategoryBuilder(id);
    }

    public static class AspectCategoryBuilder {
        private final Identifier id;
        public final String nameKey;
        public final String descriptionKey;
        private final List<AspectIcon<AspectCategory>> icons = new ArrayList<>();
        private final List<AspectLink> links = new ArrayList<>();
        private AspectBackground background = AspectBackground.EMPTY;

        protected AspectCategoryBuilder(Identifier id) {
            this.id = id;
            this.nameKey = id.toLanguageKey("amymialib.aspect.category");
            this.descriptionKey = id.toLanguageKey("amymialib.aspect.category", "description");
        }

        public AspectCategoryBuilder icon(AspectIcon<AspectCategory> icon) {
            this.icons.add(icon);
            return this;
        }

        public AspectCategoryBuilder icon(Item item) {
            return this.icon(item::getDefaultInstance);
        }

        public AspectCategoryBuilder icon(Supplier<ItemStack> stack) {
            this.icons.add(new AspectIcon.AspectIconStack<>(stack));
            return this;
        }

        public AspectCategoryBuilder icon(Identifier texture, int width, int height, float u0, float u1, float v0, float v1) {
            this.icons.add(new AspectIcon.AspectIconTexture<>(texture, width, height, u0, u1, v0, v1));
            return this;
        }

        public AspectCategoryBuilder icon(Identifier texture, int width, int height) {
            this.icons.add(new AspectIcon.AspectIconSprite<>(texture, width, height));
            return this;
        }

        public AspectCategoryBuilder background(AspectBackground background) {
            this.background = background;
            return this;
        }

        public AspectCategoryBuilder backgroundTexture(Identifier texture, int width, int height, float u0, float u1, float v0, float v1) {
            this.background = new AspectBackground.AspectBackgroundTexture(texture, width, height, u0, u1, v0, v1);
            return this;
        }

        public AspectCategoryBuilder backgroundSprite(Identifier sprite) {
            this.background = new AspectBackground.AspectBackgroundSprite(sprite);
            return this;
        }

        public AspectCategoryBuilder link(AspectLink link) {
            this.links.add(link);
            return this;
        }

        public AspectCategoryBuilder link(Identifier texture, int width, int height, float u0, float u1, float v0, float v1) {
            this.background = new AspectBackground.AspectBackgroundTexture(texture, width, height, u0, u1, v0, v1);
            return this;
        }

        public AspectCategoryBuilder link(Identifier sprite) {
            this.background = new AspectBackground.AspectBackgroundSprite(sprite);
            return this;
        }

        public AspectCategory build() {
            AspectIcon<AspectCategory> icon = this.icons.isEmpty() ? new AspectIcon.AspectIconEmpty<>()
                                                                   : (this.icons.size() == 1 ? this.icons.getFirst() : new AspectIcon.AspectMultiIcon<>(this.icons));
            return AspectManager.registerCategory(new AspectCategory(this.id, this.nameKey, this.descriptionKey, icon, this.background, this.links));
        }
    }
}