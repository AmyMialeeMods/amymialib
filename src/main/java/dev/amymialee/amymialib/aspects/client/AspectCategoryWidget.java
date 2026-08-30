package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.AspectCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
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

@Environment(EnvType.CLIENT)
public class AspectCategoryWidget implements Renderable {
    private static final Identifier ICON_BACKGROUND = AmyMialib.id("aspects/category_background");
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private static final Identifier WINDOW_LOCATION2 = AmyMialib.id("aspects/background_2");
    public final AspectEditorScreen screen;
    public final AspectCategory category;
    public final List<FormattedCharSequence> titleLines;
    public final List<FormattedCharSequence> description;
    public final int descriptionWidth;
    public final Rectangle shape;

    public AspectCategoryWidget(@NonNull AspectEditorScreen screen, @NonNull AspectCategory category, int x, int y) {
        this.screen = screen;
        this.shape = new Rectangle(x, y, 20, 20);
        this.category = category;
        var title = Language.getInstance().has(this.category.nameKey) ? category.name : Component.literal(category.id.getPath());
        this.titleLines = Minecraft.getInstance().font.split(title, 210);
        var titleWidth = Math.max(this.titleLines.stream().mapToInt(Minecraft.getInstance().font::width).max().orElse(0), 80);
        this.description = new ArrayList<>(Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(category.description, Style.EMPTY), titleWidth)));
        if (!this.description.isEmpty()) this.description.removeLast();
        var longestDescLine = 29 + titleWidth;
        for (var line : this.description) longestDescLine = Math.max(longestDescLine, Minecraft.getInstance().font.width(line));
        this.descriptionWidth = longestDescLine + 8;
    }

    public void extractHover(final @NonNull GuiGraphicsExtractor graphics, float delta) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(0, screen.categoryScroll);
        var itemSize = 20;
        var titleBarHeight = 9 * this.titleLines.size() + 9 + 8;
        var titleTop = this.shape.y + (itemSize - titleBarHeight) / 2;
        var titleLeft = this.shape.x - 5;
        var backgroundHeight = titleBarHeight + 7 + this.description.size() * 11;
        if (!this.description.isEmpty() && Language.getInstance().has(this.category.descriptionKey)) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION2, titleLeft + 4, titleTop, this.descriptionWidth - 8, backgroundHeight);
            this.extractMultilineText(graphics, this.description, titleLeft + 13, titleTop + titleBarHeight + 2 + 1, -1);
        }
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION2, titleLeft, titleTop, this.descriptionWidth, titleBarHeight);
        this.extractMultilineText(graphics, this.titleLines, titleLeft + 29, titleTop + 9, -1);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, this.shape.x, this.shape.y, itemSize, itemSize);
        var mouseX = Minecraft.getInstance().mouseHandler.getScaledXPos(Minecraft.getInstance().getWindow());
        var mouseY = Minecraft.getInstance().mouseHandler.getScaledYPos(Minecraft.getInstance().getWindow());
        category.icon.renderIcon(graphics, category, shape.x + 2, shape.y + 2, mouseX, mouseY, screen.aspects.isActive(category), delta);
        graphics.pose().popMatrix();
    }

    private void extractMultilineText(final GuiGraphicsExtractor graphics, final @NonNull List<FormattedCharSequence> lines, final int x, final int y, final int color) {
        for (var i = 0; i < lines.size(); i++) graphics.text(this.screen.getFont(), lines.get(i), x, y + i * 11, color);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, shape.x, shape.y, 20, 20);
        category.icon.renderIcon(graphics, category, shape.x + 2, shape.y + 2, mouseX, mouseY, screen.aspects.isActive(category), delta);
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
        return shape.contains(mouseX, mouseY - this.screen.categoryScroll);
    }

    public boolean contains(double mouseX, double mouseY) {
        return shape.contains(mouseX, mouseY - this.screen.categoryScroll);
    }
}