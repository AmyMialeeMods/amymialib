package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.AspectManager;
import dev.amymialee.amymialib.aspects.builder.AspectCategory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class AspectCategoriesTab {
    private static final Identifier ICON_BACKGROUND = AmyMialib.id("aspects/category_background");
    private static final Identifier COVER = AmyMialib.id("aspects/category_cover");
    private final Map<String, List<AspectCategory>> namedCategories = new HashMap<>();
    public final List<Renderable> renderables = new ArrayList<>();
    public final AspectEditorScreen screen;
    public AspectCategoryWidget hovered;
    public int height;
    public float fade;

    public AspectCategoriesTab(AspectEditorScreen screen) {
        this.screen = screen;
    }

    public void init() {
        AspectManager.getCategoryEntries().forEach((category, _) -> {
            var name = category.id.getNamespace();
            var container = FabricLoader.getInstance().getModContainer(category.id.getNamespace());
            if (container.isPresent()) name = container.get().getMetadata().getName();
            namedCategories.computeIfAbsent(name, _ -> new ArrayList<>()).add(category);
        });
    }

    public void repositionElements() {
        this.renderables.clear();
        var y = screen.categoryInner.y + 4;
        for (var entry : namedCategories.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
            var name = Component.literal(entry.getKey());
            renderables.add(new StringWidget(name.am$center((int) screen.categoryInner.getCenterX()), y, name.am$width(), 9, name, screen.getFont()));
            y += 12;
            var categories = entry.getValue();
            for (var i = 0; i < categories.size(); i++) {
                var category = categories.get(i);
                var tileOffset = screen.categoryInner.width / 2f - (screen.categorySpaces * 20 + (screen.categorySpaces - 1) * 2) / 2f + (i % screen.categorySpaces) * 22f;
                if (i >= (categories.size() / screen.categorySpaces) * screen.categorySpaces) {
                    var onRow = categories.size() % screen.categorySpaces;
                    tileOffset = screen.categoryInner.width / 2f - (onRow * 20 + (onRow - 1) * 2) / 2f + (i % onRow) * 22f;
                }
                renderables.add(new AspectCategoryWidget(this.screen, category, (int) (screen.categoryInner.x + tileOffset), y));
                if (i % screen.categorySpaces == screen.categorySpaces - 1) y += 22;
            }
            if (categories.size() % screen.categorySpaces != 0) y += 22;
            y += 2;
        }
        this.height = 4 + this.namedCategories.values().stream().mapToInt(l -> {
            var size = 14;
            for (var i = 0; i < l.size(); i++) if (i % screen.categorySpaces == screen.categorySpaces - 1) size += 22;
            if (l.size() % screen.categorySpaces != 0) size += 22;
            return size;
        }).sum();
    }

    public void tickHovered(int mouseX, int mouseY) {
        this.hovered = null;
        var hovering = false;
        if (this.screen.categoryInner.contains(mouseX, mouseY)) for (var renderable : renderables) if (renderable instanceof AspectCategoryWidget widget) if (widget.contains(mouseX, mouseY)) {
            hovering = true;
            this.hovered = widget;
            break;
        }
        this.fade = Mth.lerp(.25f, this.fade, hovering ? .5f : 0);
    }

    public void extractCategories(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float delta) {
        graphics.enableScissor(screen.categoryOuter.x, screen.categoryOuter.y, screen.categoryOuter.x + screen.categoryOuter.width, screen.categoryOuter.y + screen.categoryOuter.height);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ICON_BACKGROUND, screen.categoryOuter.x, screen.categoryOuter.y, screen.categoryOuter.width + 1, screen.categoryOuter.height);
        graphics.pose().pushMatrix();
        graphics.pose().translate(0, screen.categoryScroll);
        renderables.forEach(renderable -> renderable.extractRenderState(graphics, mouseX, mouseY, delta));
        graphics.fill(screen.categoryOuter.x, screen.categoryOuter.y, screen.categoryOuter.x + screen.categoryOuter.width, screen.categoryOuter.y + screen.categoryOuter.height, Mth.floor(this.fade * 255.0F) << 24);
        graphics.pose().popMatrix();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, COVER, screen.categoryOuter.x, screen.categoryOuter.y, screen.categoryOuter.width, screen.categoryOuter.height);
        graphics.disableScissor();
    }

    public int getHeight() {
        return this.height;
    }
}
