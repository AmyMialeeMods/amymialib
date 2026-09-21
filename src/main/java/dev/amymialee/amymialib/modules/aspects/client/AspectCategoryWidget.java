package dev.amymialee.amymialib.modules.aspects.client;

import dev.amymialee.amymialib.modules.aspects.AspectCategory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AspectCategoryWidget implements Renderable {
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    public final AspectEditorScreen screen;
    public final AspectCategory category;
    public final List<FormattedCharSequence> titleLines;
    public final List<FormattedCharSequence> description;
    public final int descriptionWidth;
    public final Rectangle shape;

    public AspectCategoryWidget(AspectEditorScreen screen, AspectCategory category, int x, int y) {
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

    public void extractHover(final GuiGraphicsExtractor graphics, float delta) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(0, this.screen.categoryScroll);
        var itemSize = 20;
        var titleBarHeight = 9 * this.titleLines.size() + 9 + 8;
        var titleTop = this.shape.y + (itemSize - titleBarHeight) / 2;
        var titleLeft = this.shape.x - 5;
        var backgroundHeight = titleBarHeight + 7 + this.description.size() * 11;
        if (!this.description.isEmpty() && Language.getInstance().has(this.category.descriptionKey)) {
            AspectEditorScreen.border(graphics, titleLeft + 4, titleTop, this.descriptionWidth - 8, backgroundHeight, Mth.hsvToArgb(.82f, .61f, 1f, 255));
            this.extractMultilineText(graphics, this.description, titleLeft + 13, titleTop + titleBarHeight + 2 + 1, -1);
        }
        AspectEditorScreen.border(graphics, titleLeft, titleTop, this.descriptionWidth, titleBarHeight, Mth.hsvToArgb(.82f, .61f, 1f, 255));
        this.extractMultilineText(graphics, this.titleLines, titleLeft + 29, titleTop + 9, -1);
        var mouseX = (int) Minecraft.getInstance().mouseHandler.getScaledXPos(Minecraft.getInstance().getWindow());
        var mouseY = (int) Minecraft.getInstance().mouseHandler.getScaledYPos(Minecraft.getInstance().getWindow());
        this.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.pose().popMatrix();
    }

    private void extractMultilineText(final GuiGraphicsExtractor graphics, final List<FormattedCharSequence> lines, final int x, final int y, final int color) {
        for (var i = 0; i < lines.size(); i++) graphics.text(this.screen.getFont(), lines.get(i), x, y + i * 11, color);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        AspectEditorScreen.thin(graphics, this.shape.x, this.shape.y, 20, 20, Mth.hsvToArgb(.82f, .61f, 1f, 255));
        this.category.icon.renderIcon(graphics, this.category, this.shape.x + 2, this.shape.y + 2, mouseX, mouseY, this.screen.aspects.isActive(this.category), delta);
    }

    private List<FormattedText> findOptimalLines(final Component input, final int preferredWidth) {
        var splitter = Minecraft.getInstance().font.getSplitter();
        List<FormattedText> bestSplit = List.of(input);
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

    private static float getMaxWidth(final StringSplitter splitter, final List<FormattedText> input) {
        return (float) input.stream().mapToDouble(splitter::stringWidth).max().orElse(0.0);
    }

    public boolean contains(int mouseX, int mouseY) {
        return this.shape.contains(mouseX, mouseY - this.screen.categoryScroll);
    }

    public boolean contains(double mouseX, double mouseY) {
        return this.shape.contains(mouseX, mouseY - this.screen.categoryScroll);
    }
}