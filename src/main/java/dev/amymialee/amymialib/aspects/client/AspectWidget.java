package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.Aspect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
@Environment(EnvType.CLIENT)
public class AspectWidget implements Renderable {
    private static final Identifier ICON_BACKGROUND = AmyMialib.id("aspects/aspect_background");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private static final Identifier WINDOW_LOCATION2 = AmyMialib.id("aspects/background_2");
    public final AspectEditorScreen screen;
    public final Aspect aspect;
    public final StringWidget title;
    public final List<FormattedCharSequence> description;
    public final Rectangle shape;

    public AspectWidget(@NonNull AspectEditorScreen screen, @NonNull Aspect aspect, int x, int y, int width, int height) {
        this.screen = screen;
        this.shape = new Rectangle(x, y, width, height);
        this.aspect = aspect;
        var title = Language.getInstance().has(this.aspect.nameKey) ? aspect.name : Component.literal(aspect.id.getPath());
        this.title = new StringWidget(0, -8, title.am$width(), 9, title, screen.getFont()).setMaxWidth(120, StringWidget.TextOverflow.SCROLLING);
        this.description = new ArrayList<>(Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(aspect.description, Style.EMPTY.withColor(TextColor.GRAY)), 115)));
        if (!this.description.isEmpty()) this.description.removeLast();
    }

    public void mouseClicked(@NonNull MouseButtonEvent event, double x, double y) {
        aspect.onClick(event);
    }

    public void mouseDragged(double mouseX) {
        var left = this.shape.x + 24.;
        var right = this.shape.x + this.shape.width - 3.;
        var xProgress = Math.clamp((mouseX - left) / (right - left), 0, 1);
        aspect.onDrag(xProgress);
    }

    public void extractHover(final @NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        var centerY = this.shape.y + shape.height / 2;
        if (!this.description.isEmpty() && Language.getInstance().has(this.aspect.descriptionKey)) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION2, this.shape.x + 2, centerY + 8, this.shape.width - 4, (int) (11 + this.description.size() * 8.25f));
            graphics.pose().pushMatrix();
            graphics.pose().translate(this.shape.x + 7, centerY + 15);
            graphics.pose().scale(0.75f);
            graphics.pose().translate(0, -9 / 2f);
            this.extractMultilineText(graphics, this.description, 0, 5, -1);
            graphics.pose().popMatrix();
        }
        this.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        var centerY = this.shape.y + shape.height / 2;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, this.shape.x, this.shape.y, this.shape.width, this.shape.height);
        graphics.pose().pushMatrix();
        graphics.pose().translate(this.shape.x + 24, this.shape.y + shape.height / 2f);
        graphics.pose().scale(0.75f);
        graphics.pose().translate(0, -9 / 2f);
        this.title.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.text(this.screen.getFont(), this.aspect.toString(), 0, 9, -1);
        graphics.pose().popMatrix();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, this.shape.x + 2, centerY - 10, 20, 20);
        aspect.icon.renderIcon(graphics, aspect, shape.x + 4, centerY - 8, mouseX, mouseY, screen.aspects.hovered == this, delta);
    }

    private void extractMultilineText(final GuiGraphicsExtractor graphics, final @NonNull List<FormattedCharSequence> lines, final int x, final int y, final int color) {
        for (var i = 0; i < lines.size(); i++) graphics.text(this.screen.getFont(), lines.get(i), x, y + i * 11, color);
    }

    private List<FormattedText> findOptimalLines(final Component input, final int preferredWidth) {
        var splitter = Minecraft.getInstance().font.getSplitter();
        List<FormattedText> bestSplit = null;
        var bestDistance = Float.MAX_VALUE;
        for (var testMargin : TEST_SPLIT_OFFSETS) {
            var testSplit = splitter.splitLines(input, preferredWidth - testMargin, Style.EMPTY);
            var distance = Math.abs(getMaxWidth(splitter, testSplit) - preferredWidth);
            if (distance <= 10.0F) return testSplit;
            if (distance < bestDistance) {
                bestDistance = distance;
                bestSplit = testSplit;
            }
        }
        return bestSplit;
    }

    private static float getMaxWidth(final @NonNull StringSplitter splitter, final @NonNull List<FormattedText> input) {
        return (float) input.stream().mapToDouble(splitter::stringWidth).max().orElse(0.0);
    }

    public boolean contains(int mouseX, int mouseY) {
        return shape.contains(mouseX, mouseY - this.screen.aspectScroll);
    }

    public boolean contains(double mouseX, double mouseY) {
        return shape.contains(mouseX, mouseY - this.screen.categoryScroll);
    }
}