package dev.amymialee.amymialib.aspects.builder;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

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
        public void renderIcon(@NonNull GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta) {}

        public int width() {
            return 0;
        }
    }

    class AspectIconStack<T> implements AspectIcon<T> {
        private final Supplier<ItemStack> stack;

        public AspectIconStack(Supplier<ItemStack> stack) {
            this.stack = stack;
        }

        @Override
        public void renderIcon(@NonNull GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta) {
            context.item(stack.get(), x, y);
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
        public void renderIcon(@NonNull GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta) {
            context.blit(this.texture, x, y, x + width, y + height, this.u0, this.u1, this.v0, this.v1);
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
        public void renderIcon(@NonNull GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, x, y, width, height);
        }
    }

    class AspectMultiIcon<T> implements AspectIcon<T> {
        private final List<AspectIcon<T>> icons;

        public AspectMultiIcon(AspectIcon<T> ... icons) {
            this.icons = Arrays.asList(icons);
        }

        public AspectMultiIcon(List<AspectIcon<T>> icons) {
            this.icons = icons;
        }

        @Override
        public void renderIcon(@NonNull GuiGraphicsExtractor context, T aspect, int x, int y, double mouseX, double mouseY, boolean hovered, float delta) {
            var offsetX = 0;
            for (var icon : icons) {
                icon.renderIcon(context, aspect, x + offsetX, y, mouseX, mouseY, hovered, delta);
                offsetX += icon.width();
            }
        }

        public int width() {
            return icons.stream().mapToInt(AspectIcon::width).sum();
        }
    }
}