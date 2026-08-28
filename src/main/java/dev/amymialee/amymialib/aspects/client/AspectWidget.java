package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.aspects.Aspect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.util.TriConsumer;
import org.jspecify.annotations.NonNull;

@Environment(EnvType.CLIENT)
public class AspectWidget<T> extends AbstractWidget {
    public final Aspect<T> aspect;

    public AspectWidget(@NonNull Aspect<T> aspect, int x, int y, int width) {
        super(x, y, width, 18, aspect.name);
        this.aspect = aspect;
        this.setMessage(this.aspect.name);
        this.setTooltip(Tooltip.create(this.aspect.description));
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        var client = Minecraft.getInstance();

        var textures = AbstractButton.SPRITES;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, textures.get(true, this.isHovered), this.getX(), this.getY(), 18, this.height, 0xFFFFFFFF);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, textures.get(true, this.isHovered), this.getX() + 18, this.getY(), this.width - 18, this.height, 0xFFFFFFFF);
        aspect.icon.renderWidget(graphics, this.aspect, this.getX() + 1, this.getY() + 1, mouseX, mouseY, this.isHovered(), a);

        final TriConsumer<Float, Float, Float> moveAndScale = (x, y, s) -> {
            graphics.pose().pushMatrix();
            graphics.pose().translate(x, y);
            graphics.pose().scale(s);
        };
        var scale = 0.7f;
        moveAndScale.accept(this.getX() + this.getWidth() - 3f, this.getY() + this.getHeight() / 2f, scale);
        var valueText = Component.literal("true");
        graphics.text(client.font, valueText, -client.font.width(valueText), -client.font.lineHeight / 2, 0xFFFFFFFF, true);
        graphics.pose().popMatrix();
        moveAndScale.accept(this.getX() + 21f, this.getY() + this.getHeight() / 2f, scale);
        var wrapLines = client.font.split(this.getMessage(), 165 - client.font.width(valueText));
        for (var i = 0; i < wrapLines.size(); i++) {
            var text = wrapLines.get(i);
            var y = -(wrapLines.size()) * client.font.lineHeight * .5 + client.font.lineHeight * i;
            graphics.text(client.font, text, 0, (int) y, 0xFFFFFFFF, true);
        }
        graphics.pose().popMatrix();
    }

    @Override
    public void updateWidgetNarration(@NonNull NarrationElementOutput builder) {
        this.defaultButtonNarrationText(builder);
    }
}