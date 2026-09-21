package dev.amymialee.amymialib.modules.aspects;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.net.URI;
import java.util.function.Supplier;

public class AspectLink {
    public final Identifier id;
    public final String nameKey;
    public final String descriptionKey;
    public final AspectIcon<AspectLink> icon;
    public final Component name;
    public final Component description;
    public final URI link;

    private AspectLink(Identifier id, String nameKey, String descriptionKey, AspectIcon<AspectLink> icon, URI link) {
        this.id = id;
        this.icon = icon;
        this.nameKey = nameKey;
        this.descriptionKey = descriptionKey;
        this.name = Component.translatable(this.nameKey);
        this.description = Component.translatable(this.descriptionKey).withColor(TextColor.GRAY).append("\n").append(Component.literal(link.toString()).withStyle(ChatFormatting.BLUE));
        this.link = link;
    }

    public static AspectLinkBuilder of(Identifier id, String link) {
        return new AspectLinkBuilder(id, link);
    }

    public static class AspectLinkBuilder {
        private final Identifier id;
        private final URI link;
        public final String nameKey;
        public final String descriptionKey;
        private AspectIcon<AspectLink> icon = new AspectIcon.AspectIconEmpty<>();

        protected AspectLinkBuilder(Identifier id, String link) {
            this.id = id;
            this.link = URI.create(link);
            this.nameKey = id.toLanguageKey("amymialib.aspect.link");
            this.descriptionKey = id.toLanguageKey("amymialib.aspect.link", "description");
        }

        public AspectLinkBuilder icon(AspectIcon<AspectLink> icon) {
            this.icon = icon;
            return this;
        }

        public AspectLinkBuilder icon(Item item) {
            return this.icon(item::getDefaultInstance);
        }

        public AspectLinkBuilder icon(Supplier<ItemStack> stack) {
            this.icon = new AspectIcon.AspectIconStack<>(stack);
            return this;
        }

        public AspectLinkBuilder icon(Identifier texture, int width, int height, float u0, float u1, float v0, float v1) {
            this.icon = new AspectIcon.AspectIconTexture<>(texture, width, height, u0, u1, v0, v1);
            return this;
        }

        public AspectLinkBuilder icon(Identifier texture, int width, int height) {
            this.icon = new AspectIcon.AspectIconSprite<>(texture, width, height);
            return this;
        }

        public AspectLink build() {
            return AspectManager.registerLink(new AspectLink(this.id, this.nameKey, this.descriptionKey, this.icon, this.link));
        }
    }
}