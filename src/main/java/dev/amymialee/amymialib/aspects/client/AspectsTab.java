package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.AspectManager;
import dev.amymialee.amymialib.aspects.builder.AspectCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AspectsTab {
    private static final Identifier ICON_BACKGROUND = AmyMialib.id("aspects/aspect_background");
    private static final Identifier COVER = AmyMialib.id("aspects/aspect_cover");
    public final List<Renderable> renderables = new ArrayList<>();
    public final AspectEditorScreen screen;
    public AspectCategory category;
    public AspectWidget hovered;
    public int height;
    public float fade;

    private static final Identifier WINDOW_LOCATION = AmyMialib.id("aspects/background");
    private static final Identifier WINDOW_LOCATION2 = AmyMialib.id("aspects/background_2");
    private static final Identifier OVERLAY = AmyMialib.id("aspects/overlay");
    private static final Identifier VALUE_COVER = AmyMialib.id("aspects/value_cover");
    private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
    private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");

    public AspectsTab(AspectEditorScreen screen) {
        this.screen = screen;
    }

    public void repositionElements() {
        this.renderables.clear();
        this.height = 0;
        if (category != null) {
            var y = screen.aspectInner.y + 4;
            var aspects = AspectManager.getCategoryEntries(category);
            for (var i = 0; i < aspects.size(); i++) {
                var aspect = aspects.get(i);
                var tileOffset = screen.aspectInner.width / 2f - (screen.categorySpaces * 20 + (screen.categorySpaces - 1) * 2) / 2f + (i % screen.categorySpaces) * 72f;
                if (i >= (aspects.size() / screen.categorySpaces) * screen.categorySpaces) {
                    var onRow = aspects.size() % screen.categorySpaces;
                    tileOffset = screen.aspectInner.width / 2f - (onRow * 20 + (onRow - 1) * 2) / 2f + (i % onRow) * 72f;
                }
                renderables.add(new AspectWidget(this.screen, aspect, (int) (screen.aspectInner.x + tileOffset), y));
                if (i % screen.categorySpaces == screen.categorySpaces - 1) y += 22;
            }
            this.height = 4 + 22 * (aspects.size() / screen.categorySpaces + 1);
        }
    }

    public void tickHovered(int mouseX, int mouseY) {
        this.hovered = null;
        var hovering = false;
        if (this.screen.aspectInner.contains(mouseX, mouseY)) for (var renderable : renderables) if (renderable instanceof AspectWidget widget) if (widget.contains(mouseX, mouseY)) {
            hovering = true;
            this.hovered = widget;
            break;
        }
        this.fade = Mth.lerp(.25f, this.fade, hovering ? .5f : 0);
    }

    public void extractAspects(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float delta) {
        graphics.enableScissor(screen.aspectOuter.x, screen.aspectOuter.y, screen.aspectOuter.x + screen.aspectOuter.width, screen.aspectOuter.y + screen.aspectOuter.height);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, screen.aspectOuter.x - 1, screen.aspectOuter.y, screen.aspectOuter.width, screen.aspectOuter.height);
        graphics.pose().pushMatrix();
        graphics.pose().translate(0, screen.aspectScroll);
        renderables.forEach(renderable -> renderable.extractRenderState(graphics, mouseX, mouseY, delta));
        graphics.pose().popMatrix();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, COVER, screen.aspectOuter.x, screen.aspectOuter.y, screen.aspectOuter.width, screen.aspectOuter.height);
        graphics.disableScissor();
    }
//        var tab = this.selectedTab;
//        if (tab != null) {
//            var mouseX = this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
//            var mouseY = this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
//            tab.extractContents(graphics, this.leftPos + 9, this.topPos + 18, mouseX, mouseY, delta);
//        } else {
//            graphics.fill(this.leftPos + 9, this.topPos + 18, this.leftPos + 9 + 234, this.topPos + 18 + 113, -16777216);
//            var midX = this.leftPos + 9 + 117;
//            graphics.centeredText(this.font, NO_ADVANCEMENTS_LABEL, midX, this.topPos + 18 + 56 - 9 / 2, -1);
//            graphics.centeredText(this.font, VERY_SAD_LABEL, midX, this.topPos + 18 + 113 - 9, -1);
//        }
//        graphics.enableScissor(this.valueOuter.x, this.valueOuter.y, this.valueOuter.x + this.valueOuter.width, this.valueOuter.y + this.valueOuter.height);
//        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, VALUE_COVER, this.valueOuter.x, this.valueOuter.y, this.valueOuter.width, this.valueOuter.height);
//        graphics.text(this.font, TITLE, TITLE.am$center(this.valueOuter.x + this.valueOuter.width / 2), this.topPos + 6, -12566464, false);
//        graphics.disableScissor();

    public boolean isActive(AspectCategory category) {
        return this.category == category;
    }

    public int getHeight() {
        return this.height;
    }
}