package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.Aspect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AspectWidget implements Renderable {
    private static final Identifier ICON_BACKGROUND = AmyMialib.id("aspects/aspect_background");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private static final Identifier WINDOW_LOCATION2 = AmyMialib.id("aspects/background_2");
    public final AspectEditorScreen screen;
    public final Aspect aspect;
    public final Component title;
    public final List<FormattedCharSequence> description;
    public final int descriptionWidth;
    public final Rectangle shape;

    public AspectWidget(@NonNull AspectEditorScreen screen, @NonNull Aspect aspect, int x, int y) { // 144
        this.screen = screen;
        this.shape = new Rectangle(x, y, 144, 20);
        this.aspect = aspect;
        this.title = Language.getInstance().has(this.aspect.nameKey) ? aspect.name : Component.literal(aspect.id.getPath());
        var titleWidth = Math.max(title.am$width(), 80);
        this.description = new ArrayList<>(Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(aspect.description, Style.EMPTY), titleWidth)));
        if (!this.description.isEmpty()) this.description.removeLast();
        var longestDescLine = 29 + titleWidth;
        for (var line : this.description) longestDescLine = Math.max(longestDescLine, Minecraft.getInstance().font.width(line));
        this.descriptionWidth = longestDescLine + 8;
    }

    public void extractHover(final @NonNull GuiGraphicsExtractor graphics, float delta) {
//        graphics.pose().pushMatrix();
//        graphics.pose().translate(0, screen.aspectScroll);
//        var itemWidth = 70;
//        var itemHeight = 20;
//        var titleBarHeight = 9 * this.titleLines.size() + 9 + 8;
//        var titleTop = this.shape.y + (itemHeight - titleBarHeight) / 2;
//        var titleLeft = this.shape.x - 5;
//        var backgroundHeight = titleBarHeight + 7 + this.description.size() * 11;
//        if (!this.description.isEmpty() && Language.getInstance().has(this.aspect.descriptionKey)) {
//            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION2, titleLeft + 4, titleTop, this.descriptionWidth - 8, backgroundHeight);
//            this.extractMultilineText(graphics, this.description, titleLeft + 13, titleTop + titleBarHeight + 2 + 1, -1);
//        }
//        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION2, titleLeft, titleTop, this.descriptionWidth, titleBarHeight);
//        this.extractMultilineText(graphics, this.titleLines, titleLeft + 29, titleTop + 9, -1);
//        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, this.shape.x, this.shape.y, itemWidth, itemHeight);
//        var mouseX = Minecraft.getInstance().mouseHandler.getScaledXPos(Minecraft.getInstance().getWindow());
//        var mouseY = Minecraft.getInstance().mouseHandler.getScaledYPos(Minecraft.getInstance().getWindow());
//        aspect.icon.renderIcon(graphics, aspect, shape.x + 2, shape.y + 2, mouseX, mouseY, screen.aspects.hovered == this, delta);
//        graphics.pose().popMatrix();
    }

    private void extractMultilineText(final GuiGraphicsExtractor graphics, final @NonNull List<FormattedCharSequence> lines, final int x, final int y, final int color) {
        for (var i = 0; i < lines.size(); i++) graphics.text(this.screen.getFont(), lines.get(i), x, y + i * 11, color);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        var itemWidth = 70;
        var itemHeight = 20;
        var titleBarHeight = 26;
        var titleTop = this.shape.y + (itemHeight - titleBarHeight) / 2;
        var titleLeft = this.shape.x - 5;
        var backgroundHeight = titleBarHeight + 7 + this.description.size() * 11;
        if (!this.description.isEmpty() && Language.getInstance().has(this.aspect.descriptionKey)) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION2, titleLeft + 4, titleTop, this.descriptionWidth - 8, backgroundHeight);
            this.extractMultilineText(graphics, this.description, titleLeft + 13, titleTop + titleBarHeight + 2 + 1, -1);
        }
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION2, titleLeft, titleTop, this.descriptionWidth, titleBarHeight);
        graphics.text(this.screen.getFont(), this.title, titleLeft + 29, titleTop + 9, -1);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, this.shape.x, this.shape.y, itemHeight, itemHeight);
        aspect.icon.renderIcon(graphics, aspect, shape.x + 2, shape.y + 2, mouseX, mouseY, screen.aspects.hovered == this, delta);
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
        return shape.contains(mouseX, mouseY);
    }

    public void mouseClicked(double x, double y) {
        
    }

    public void mouseDragged(double mouseX, double mouseY, double dx, double dy) {

    }
}