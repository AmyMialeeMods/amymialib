package dev.amymialee.amymialib.modules.aspects.client;

import dev.amymialee.amymialib.modules.aspects.AspectLink;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class AspectLinkWidget implements Renderable {
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    public final AspectEditorScreen screen;
    public final AspectLink link;
    public final List<FormattedCharSequence> titleLines;
    public final List<FormattedCharSequence> description;
    public final int titleWidth;
    public final int descriptionWidth;
    public final int totalWidth;
    public final Rectangle shape;

    public AspectLinkWidget(AspectEditorScreen screen, AspectLink link, int x, int y) {
        this.screen = screen;
        this.shape = new Rectangle(x, y, 20, 20);
        this.link = link;
        var title = Language.getInstance().has(this.link.nameKey) ? link.name : Component.literal(link.id.getPath());
        this.titleLines = Minecraft.getInstance().font.split(title, 120);
        this.titleWidth = this.titleLines.stream().mapToInt(Minecraft.getInstance().font::width).max().orElse(10);
        this.description = new ArrayList<>(Minecraft.getInstance().font.split(link.description, 160));
        this.descriptionWidth = (int) (this.description.stream().mapToInt(Minecraft.getInstance().font::width).max().orElse(10) * 0.75f);
        this.totalWidth = Math.max(this.titleWidth, this.descriptionWidth);
    }

    public void mouseClicked(MouseButtonEvent event, double x, double y) {
        ConfirmLinkScreen.confirmLinkNow(this.screen, this.link.link);
    }

    public void extractHover(final GuiGraphicsExtractor graphics, float delta) {
        if (!this.description.isEmpty() && Language.getInstance().has(this.link.descriptionKey)) {
            AspectEditorScreen.border(graphics,
                    this.shape.x - 4 - this.totalWidth + 2,
                    this.shape.y + this.shape.height / 2 + this.titleLines.size() * 8 - 8,
                    this.shape.width + 8 + this.totalWidth - 4, 9 + (1 + this.description.size()) * 8, Mth.hsvToArgb(.82f, .61f, 1f, 255));
            graphics.pose().pushMatrix();
            graphics.pose().translate(
                    this.shape.x - 4 - this.totalWidth + 8,
                    this.shape.y + this.shape.height + 2 + this.titleLines.size() * 8 - 8);
            graphics.pose().scale(0.75f);
            graphics.pose().translate(0, -9 / 2f);
            this.extractMultilineText(graphics, this.description, 0, 5, -1);
            graphics.pose().popMatrix();
        }
        AspectEditorScreen.border(graphics, this.shape.x - 4 - this.totalWidth, this.shape.y + 1,
                this.shape.width + 8 + this.totalWidth,
                this.shape.height - 2 - 8 + this.titleLines.size() * 8, Mth.hsvToArgb(.82f, .61f, 1f, 255));
        this.extractMultilineText(graphics, this.titleLines,
                this.shape.x + 1 - this.titleWidth / 2 - this.totalWidth / 2,
                this.shape.y + 6, -1);
        var mouseX = (int) Minecraft.getInstance().mouseHandler.getScaledXPos(Minecraft.getInstance().getWindow());
        var mouseY = (int) Minecraft.getInstance().mouseHandler.getScaledYPos(Minecraft.getInstance().getWindow());
        this.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    private void extractMultilineText(final GuiGraphicsExtractor graphics, final List<FormattedCharSequence> lines, final int x, final int y, final int color) {
        for (var i = 0; i < lines.size(); i++) graphics.text(this.screen.getFont(), lines.get(i), x, y + i * 11, color);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        AspectEditorScreen.thin(graphics, this.shape.x, this.shape.y, 20, 20, Mth.hsvToArgb(.82f, .61f, 1f, 255));
        this.link.icon.renderIcon(graphics, this.link, this.shape.x + 2, this.shape.y + 2, mouseX, mouseY, this.screen.aspects.hoveredLink == this, delta);
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
        return this.shape.contains(mouseX, mouseY);
    }

    public boolean contains(double mouseX, double mouseY) {
        return this.shape.contains(mouseX, mouseY);
    }
}