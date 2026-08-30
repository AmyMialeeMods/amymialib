package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.AspectManager;
import dev.amymialee.amymialib.aspects.AspectCategory;
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
            var aspectWidth = 100;
            var aspectGap = 4;
            var y = screen.aspectInner.y + 4;
            var aspects = AspectManager.getCategoryEntries(category);
            for (var i = 0; i < aspects.size(); i++) {
                var aspect = aspects.get(i);
                var tileOffset = screen.aspectInner.width / 2f - (screen.aspectSpaces * aspectWidth + (screen.aspectSpaces - 1) * aspectGap) / 2f + (i % screen.aspectSpaces) * (aspectWidth + aspectGap);
                if (i >= (aspects.size() / screen.aspectSpaces) * screen.aspectSpaces) {
                    var onRow = aspects.size() % screen.aspectSpaces;
                    tileOffset = screen.aspectInner.width / 2f - (onRow * aspectWidth + (onRow - 1) * aspectGap) / 2f + (i % onRow) * aspectWidth;
                }
                renderables.add(new AspectWidget(this.screen, aspect, (int) (screen.aspectInner.x + tileOffset), y, aspectWidth, 26));
                if (i % screen.aspectSpaces == screen.aspectSpaces - 1) y += 30;
            }
            this.height = 4 + 30 * (aspects.size() / screen.aspectSpaces + 1);
        }
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
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, screen.aspectOuter.x - 1, screen.aspectOuter.y, screen.aspectOuter.width, screen.aspectOuter.height);
        graphics.pose().pushMatrix();
        graphics.pose().translate(0, screen.aspectScroll);
        renderables.forEach(renderable -> renderable.extractRenderState(graphics, mouseX, mouseY, delta));
        graphics.pose().popMatrix();
        graphics.fill(screen.aspectOuter.x, screen.aspectOuter.y, screen.aspectOuter.x + screen.aspectOuter.width, screen.aspectOuter.y + screen.aspectOuter.height, Mth.floor(this.fade * 255.0F) << 24);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, COVER, screen.aspectOuter.x, screen.aspectOuter.y, screen.aspectOuter.width, screen.aspectOuter.height);
        graphics.disableScissor();
        this.fade = Mth.lerp(.05f, this.fade, this.hovered != null ? .3f : 0);
    }

    public boolean isActive(AspectCategory category) {
        return this.category == category;
    }

    public int getHeight() {
        return this.height;
    }
}