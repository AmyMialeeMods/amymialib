package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.aspects.Aspect;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public interface AspectIcon {
    AspectIconEmpty EMPTY = new AspectIconEmpty();

    void renderWidget(GuiGraphicsExtractor context, Aspect<?> aspect, int x, int y, int mouseX, int mouseY, boolean hovered, float delta);

    default int width() {
        return 18;
    }

    class AspectIconEmpty implements AspectIcon {
        @Override
        public void renderWidget(@NonNull GuiGraphicsExtractor context, Aspect<?> aspect, int x, int y, int mouseX, int mouseY, boolean hovered, float delta) {}

        public int width() {
            return 0;
        }
    }

    class AspectIconStack implements AspectIcon {
        private final Supplier<ItemStack> stack;

        public AspectIconStack(Supplier<ItemStack> stack) {
            this.stack = stack;
        }

        @Override
        public void renderWidget(@NonNull GuiGraphicsExtractor context, Aspect<?> aspect, int x, int y, int mouseX, int mouseY, boolean hovered, float delta) {
            context.item(stack.get(), x, y);
        }
    }

    class AspectIconTexture implements AspectIcon {
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
        public void renderWidget(@NonNull GuiGraphicsExtractor context, Aspect<?> aspect, int x, int y, int mouseX, int mouseY, boolean hovered, float delta) {
            context.blit(this.texture, x, y, x + width, y + height, this.u0, this.u1, this.v0, this.v1);
        }
    }

    class AspectIconSprite implements AspectIcon {
        private final Identifier texture;
        private final int width;
        private final int height;

        public AspectIconSprite(Identifier texture, int width, int height) {
            this.texture = texture;
            this.width = width;
            this.height = height;
        }

        @Override
        public void renderWidget(@NonNull GuiGraphicsExtractor context, Aspect<?> aspect, int x, int y, int mouseX, int mouseY, boolean hovered, float delta) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, x, y, width, height);
        }
    }

    class AspectMultiIcon implements AspectIcon {
        private final List<AspectIcon> icons;

        public AspectMultiIcon(AspectIcon ... icons) {
            this.icons = Arrays.asList(icons);
        }

        public AspectMultiIcon(List<AspectIcon> icons) {
            this.icons = icons;
        }

        @Override
        public void renderWidget(@NonNull GuiGraphicsExtractor context, Aspect<?> aspect, int x, int y, int mouseX, int mouseY, boolean hovered, float delta) {
            int offsetX = 0;
            for (var icon : icons) {
                icon.renderWidget(context, aspect, x + offsetX, y, mouseX, mouseY, hovered, delta);
                offsetX += icon.width();
            }
        }

        public int width() {
            return icons.stream().mapToInt(AspectIcon::width).sum();
        }
    }
}