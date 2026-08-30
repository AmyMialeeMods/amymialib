package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.AspectCategory;
import dev.amymialee.amymialib.aspects.AspectManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AspectsTab {
    public final List<Renderable> renderables = new ArrayList<>();
    public final List<Renderable> renderablesHeader = new ArrayList<>();
    public final AspectEditorScreen screen;
    public AspectCategory category = AmyMialib.AMYMIALIB;
    public AspectWidget hovered;
    public AspectLinkWidget hoveredLink;
    public Component title;
    public List<FormattedCharSequence> description;
    public int height;
    public float fade;
    public int headerTextWidth;

    public AspectsTab(AspectEditorScreen screen) {
        this.screen = screen;
    }

    public void repositionElements() {
        this.renderables.clear();
        this.renderablesHeader.clear();
        this.height = 0;
        if (category != null) {
            this.headerTextWidth = screen.aspectOuter.width - 4 - 21 * this.category.links.size();
            var linkSize = 21;
            for (var i = 0; i < this.category.links.size(); i++) {
                var aspect = this.category.links.get(i);
                var tileOffset = 25 + i * linkSize;
                renderablesHeader.add(new AspectLinkWidget(this.screen, aspect, (screen.aspectOuter.x + screen.aspectOuter.width - tileOffset) + 1, screen.aspectOuter.y + 7));
            }
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
            this.title = Language.getInstance().has(this.category.nameKey) ? category.name : Component.literal(category.id.getPath());
            var description = this.category.description;
            this.description = new ArrayList<>(Minecraft.getInstance().font.split(description, (int) (this.headerTextWidth / 0.6f)));
            this.description.removeLast();
        } else {
            this.headerTextWidth = screen.aspectOuter.width - 4;
            this.title = Component.translatable("advancements.sad_label");
            var description = Component.translatable("advancements.empty");
            this.description = new ArrayList<>(Minecraft.getInstance().font.split(description, this.headerTextWidth));
        }
    }

    public void tickHovered(int mouseX, int mouseY) {
        this.hovered = null;
        if (this.screen.aspectInner.contains(mouseX, mouseY)) for (var renderable : renderables) if (renderable instanceof AspectWidget widget) if (widget.contains(mouseX, mouseY)) {
            this.hovered = widget;
            return;
        }
        this.hoveredLink = null;
        if (this.screen.aspectOuter.contains(mouseX, mouseY)) for (var renderable : renderablesHeader) if (renderable instanceof AspectLinkWidget widget) if (widget.contains(mouseX, mouseY)) {
            this.hoveredLink = widget;
            return;
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

        graphics.text(this.screen.getFont(), this.title, screen.aspectOuter.x - this.title.am$width() / 2 + this.headerTextWidth / 2, screen.aspectOuter.y + 5, -1);
        graphics.pose().pushMatrix();
        graphics.pose().translate(screen.aspectOuter.x + this.headerTextWidth / 2f, screen.aspectOuter.y + 16);
        graphics.pose().scale(0.6f);
        graphics.pose().translate(0, -9 / 2f);
        this.extractMultilineText(graphics, this.description, 0, 16, -1);
        graphics.pose().popMatrix();
        renderablesHeader.forEach(renderable -> renderable.extractRenderState(graphics, mouseX, mouseY, delta));
    }

    private void extractMultilineText(GuiGraphicsExtractor graphics, @NonNull List<FormattedCharSequence> lines, int x, int y, int color) {
        var width = lines.stream().mapToInt(line -> this.screen.getFont().width(line)).max().orElse(0);
        x -= width / 2;
        y = y - (lines.size() * this.screen.getFont().lineHeight) / 2;
        for (var i = 0; i < lines.size(); i++) {
            graphics.text(this.screen.getFont(), lines.get(i), x, y + i * 10, color);
        }
    }

    public boolean isActive(AspectCategory category) {
        return this.category == category;
    }

    public int getHeight() {
        return this.height;
    }
}