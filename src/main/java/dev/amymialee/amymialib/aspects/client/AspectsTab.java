package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.aspects.AspectManager;
import dev.amymialee.amymialib.aspects.AspectCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AspectsTab {
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    public final List<Renderable> renderables = new ArrayList<>();
    public final AspectEditorScreen screen;
    public AspectCategory category;
    public AspectWidget hovered;
    public StringWidget title;
    public List<FormattedCharSequence> description;
    public int height;
    public float fade;

    public AspectsTab(AspectEditorScreen screen) {
        this.screen = screen;
    }

    public void repositionElements() {
        this.renderables.clear();
        this.height = 0;
        if (category != null) {
            var aspectWidth = 100;
            var aspectGap = 4;
            var y = screen.aspectInner.y;
            var aspects = AspectManager.getCategoryEntries(category);
            for (var i = 0; i < aspects.size(); i++) {
                var aspect = aspects.get(i);
                var tileOffset = screen.aspectInner.width / 2f - (screen.aspectSpaces * aspectWidth + (screen.aspectSpaces - 1) * aspectGap) / 2f + (i % screen.aspectSpaces) * (aspectWidth + aspectGap);
                if (i >= (aspects.size() / screen.aspectSpaces) * screen.aspectSpaces) {
                    var onRow = aspects.size() % screen.aspectSpaces;
                    tileOffset = screen.aspectInner.width / 2f - (onRow * aspectWidth + (onRow - 1) * aspectGap) / 2f + (i % onRow) * aspectWidth;
                }
                renderables.add(new AspectWidget(this.screen, aspect, (int) (screen.aspectInner.x + tileOffset) + 1, y, aspectWidth, 26));
                if (i % screen.aspectSpaces == screen.aspectSpaces - 1) y += 30;
            }
            this.height = 4 + 30 * (aspects.size() / screen.aspectSpaces + 1);
            var title = Language.getInstance().has(this.category.nameKey) ? category.name : Component.literal(category.id.getPath());
            this.title = new StringWidget(0, -8, title.am$width(), 9, title, screen.getFont()).setMaxWidth(120, StringWidget.TextOverflow.SCROLLING);
            this.description = new ArrayList<>(Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(this.category.description, Style.EMPTY.withColor(TextColor.GRAY)), 200)));
            if (!this.description.isEmpty()) this.description.removeLast();
        } else {
            this.title = new StringWidget(0, -8, 120, 9, Component.translatable("advancements.sad_label"), screen.getFont()).setMaxWidth(120, StringWidget.TextOverflow.SCROLLING);
            this.description = new ArrayList<>(Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(Component.translatable("advancements.empty"), Style.EMPTY.withColor(TextColor.GRAY)), 200)));
        }
        this.title.setPosition(screen.aspectOuter.x + 4, screen.aspectOuter.y + 4);
    }

    public void tickHovered(int mouseX, int mouseY) {
        this.hovered = null;
        if (this.screen.aspectInner.contains(mouseX, mouseY)) for (var renderable : renderables) if (renderable instanceof AspectWidget widget) if (widget.contains(mouseX, mouseY)) {
            this.hovered = widget;
            break;
        }
    }

    public void extractAspects(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float delta) {
        graphics.enableScissor(screen.aspectOuter.x, screen.aspectOuter.y, screen.aspectOuter.x + screen.aspectOuter.width, screen.aspectOuter.y + screen.aspectOuter.height);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.FILL, screen.aspectOuter.x, screen.aspectOuter.y, screen.aspectOuter.width, screen.aspectOuter.height);
        graphics.pose().pushMatrix();
        graphics.pose().translate(0, screen.aspectScroll);
        renderables.forEach(renderable -> renderable.extractRenderState(graphics, mouseX, mouseY, delta));
        graphics.pose().popMatrix();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.FILL, screen.aspectOuter.x, screen.aspectOuter.y, screen.aspectOuter.width, screen.aspectOuter.height, Mth.hsvToArgb(0f, 0f, 0f, Mth.floor(this.fade * 255.0F) << 24));
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.BORDER, screen.aspectOuter.x, screen.aspectOuter.y, screen.aspectOuter.width, screen.aspectOuter.height, Mth.hsvToArgb(.82f, .61f, 1f, 255));
        graphics.disableScissor();

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.FILL, screen.aspectOuter.x - 1, screen.aspectOuter.y - 1, screen.aspectOuter.width + 2, 37);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.BORDER, screen.aspectOuter.x - 1, screen.aspectOuter.y - 1, screen.aspectOuter.width + 2, 37, Mth.hsvToArgb(.82f, .61f, 1f, 255));

        this.title.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.pose().pushMatrix();
        graphics.pose().translate(screen.aspectOuter.x + 4, screen.aspectOuter.y + 16);
        graphics.pose().scale(0.6f);
        graphics.pose().translate(0, -9 / 2f);
        this.extractMultilineText(graphics, this.description, 0, 15, -1);
        graphics.pose().popMatrix();
    }

    private void extractMultilineText(final GuiGraphicsExtractor graphics, final @NonNull List<FormattedCharSequence> lines, final int x, final int y, final int color) {
        var y2 = y - (lines.size() * this.screen.getFont().lineHeight) / 2;
        for (var i = 0; i < lines.size(); i++) {
            graphics.text(this.screen.getFont(), lines.get(i), x, y2 + i * 10, color);
        }
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

    public boolean isActive(AspectCategory category) {
        return this.category == category;
    }

    public int getHeight() {
        return this.height;
    }
}