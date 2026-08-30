package dev.amymialee.amymialib.aspects;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public interface AspectBackground {
    AspectBackgroundEmpty EMPTY = new AspectBackgroundEmpty();

    void renderBackground(GuiGraphicsExtractor context, int x, int y, int width, int height, double mouseX, double mouseY, float delta);

    class AspectBackgroundEmpty implements AspectBackground {
        @Override
        public void renderBackground(GuiGraphicsExtractor context, int x, int y, int width, int height, double mouseX, double mouseY, float delta) {}
    }

    class AspectBackgroundTexture implements AspectBackground {
        private final Identifier texture;
        private final int width;
        private final int height;
        private final float u0;
        private final float u1;
        private final float v0;
        private final float v1;

        public AspectBackgroundTexture(Identifier texture, int width, int height, float u0, float u1, float v0, float v1) {
            this.texture = texture;
            this.width = width;
            this.height = height;
            this.u0 = u0;
            this.u1 = u1;
            this.v0 = v0;
            this.v1 = v1;
        }

        @Override
        public void renderBackground(@NonNull GuiGraphicsExtractor context, int x, int y, int width, int height, double mouseX, double mouseY, float delta) {
            context.blit(this.texture, x, y, x + this.width, y + this.height, this.u0, this.u1, this.v0, this.v1);
        }
    }

    class AspectBackgroundSprite implements AspectBackground {
        private final Identifier texture;

        public AspectBackgroundSprite(Identifier texture) {
            this.texture = texture;
        }

        @Override
        public void renderBackground(@NonNull GuiGraphicsExtractor context, int x, int y, int width, int height, double mouseX, double mouseY, float delta) {
            context.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, x, y, width, height);
        }
    }
}