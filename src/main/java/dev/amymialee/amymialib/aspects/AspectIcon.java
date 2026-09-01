package dev.amymialee.amymialib.aspects;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public interface AspectIcon<T> {
    void renderIcon(GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta);

    default int width() {
        return 18;
    }

    class AspectIconEmpty<T> implements AspectIcon<T> {
        @Override
        public void renderIcon(GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta) {}

        @Override public int width() {
            return 0;
        }
    }

    class AspectIconStack<T> implements AspectIcon<T> {
        private final Supplier<ItemStack> stack;

        public AspectIconStack(Supplier<ItemStack> stack) {
            this.stack = stack;
        }

        @Override
        public void renderIcon(GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta) {
            try {
                context.item(this.stack.get(), x, y);
            } catch (Exception _) {}
        }
    }

    class AspectIconTexture<T> implements AspectIcon<T> {
        private final Identifier texture;
        private final int width;
        private final int height;
        private final float u0;
        private final float u1;
        private final float v0;
        private final float v1;

        public AspectIconTexture(Identifier texture, int width, int height, float u0, float u1, float v0, float v1) {
            this.texture = texture;
            this.width = width;
            this.height = height;
            this.u0 = u0;
            this.u1 = u1;
            this.v0 = v0;
            this.v1 = v1;
        }

        @Override
        public void renderIcon(GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta) {
            context.blit(this.texture, x, y, x + this.width, y + this.height, this.u0, this.u1, this.v0, this.v1);
        }
    }

    class AspectIconSprite<T> implements AspectIcon<T> {
        private final Identifier texture;
        private final int width;
        private final int height;

        public AspectIconSprite(Identifier texture, int width, int height) {
            this.texture = texture;
            this.width = width;
            this.height = height;
        }

        @Override
        public void renderIcon(GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, x, y, this.width, this.height);
        }
    }

    class AspectMultiIcon<T> implements AspectIcon<T> {
        private final List<AspectIcon<T>> icons;

        @SafeVarargs public AspectMultiIcon(AspectIcon<T> ... icons) {
            this.icons = Arrays.asList(icons);
        }

        public AspectMultiIcon(List<AspectIcon<T>> icons) {
            this.icons = icons;
        }

        @Override
        public void renderIcon(GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta) {
            var offsetX = 0;
            for (var icon : this.icons) {
                icon.renderIcon(context, aspect, x + offsetX, y, mouseX, mouseY, hovered, delta);
                offsetX += icon.width();
            }
        }

        @Override public int width() {
            return this.icons.stream().mapToInt(AspectIcon::width).sum();
        }
    }
}