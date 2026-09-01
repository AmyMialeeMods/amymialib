package dev.amymialee.amymialib.aspects.client;

import dev.amymialee.amymialib.aspects.AspectCategory;
import dev.amymialee.amymialib.aspects.AspectManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AspectCategoriesTab {
    public final Map<String, List<AspectCategory>> namedCategories = new HashMap<>();
    public final List<Renderable> renderables = new ArrayList<>();
    public final AspectEditorScreen screen;
    public @Nullable AspectCategoryWidget hovered;
    public int height;
    public float fade;

    public AspectCategoriesTab(AspectEditorScreen screen) {
        this.screen = screen;
    }

    public void init() {
        this.namedCategories.clear();
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
            renderables.add(new StringWidget(screen.categoryInner.x - 2, y, name.am$width(), 9, name, screen.getFont()).setMaxWidth(screen.categoryInner.width + 4, StringWidget.TextOverflow.SCROLLING));
            var categoryWidth = 20;
            var categoryGap = 2;
            y += 12;
            var categories = entry.getValue();
            for (var i = 0; i < categories.size(); i++) {
                var category = categories.get(i);
                var tileOffset = screen.categoryInner.width / 2f - (screen.categorySpaces * categoryWidth + (screen.categorySpaces - 1) * categoryGap) / 2f + (i % screen.categorySpaces) * (categoryWidth + categoryGap);
                if (i >= (categories.size() / screen.categorySpaces) * screen.categorySpaces) {
                    var onRow = categories.size() % screen.categorySpaces;
                    tileOffset = screen.categoryInner.width / 2f - (onRow * categoryWidth + (onRow - 1) * categoryGap) / 2f + (i % onRow) * (categoryWidth + categoryGap);
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
        if (this.screen.categoryInner.contains(mouseX, mouseY)) for (var renderable : renderables) if (renderable instanceof AspectCategoryWidget widget) if (widget.contains(mouseX, mouseY)) {
            this.hovered = widget;
            break;
        }
    }

    public void extractCategories(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float delta) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.FILL, screen.categoryOuter.x, screen.categoryOuter.y, screen.categoryOuter.width + 8, screen.categoryOuter.height);
        graphics.enableScissor(screen.categoryOuter.x, screen.categoryOuter.y, screen.categoryOuter.x + screen.categoryOuter.width, screen.categoryOuter.y + screen.categoryOuter.height);
        graphics.pose().pushMatrix();
        graphics.pose().translate(0, screen.categoryScroll);
        renderables.forEach(renderable -> {
            if (renderable instanceof StringWidget widget && widget.getWidth() < screen.categoryInner.width) {
                var x = widget.getX();
                var y = widget.getY();
                widget.setPosition(screen.categoryInner.x + screen.categoryInner.width / 2 - widget.getWidth() / 2, y);
                renderable.extractRenderState(graphics, mouseX, mouseY, delta);
                widget.setPosition(x, y);
            } else {
                renderable.extractRenderState(graphics, mouseX, mouseY, delta);
            }
        });
        graphics.pose().popMatrix();
        graphics.disableScissor();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.FILL, screen.categoryOuter.x, screen.categoryOuter.y, screen.categoryOuter.width + 8, screen.categoryOuter.height, Mth.hsvToArgb(0f, 0f, 0f, Mth.floor(this.fade * 255.0F) << 24));
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.BORDER, screen.categoryOuter.x, screen.categoryOuter.y, screen.categoryOuter.width + 8, screen.categoryOuter.height, Mth.hsvToArgb(.82f, .61f, 1f, 255));
    }

    public int getHeight() {
        return this.height;
    }
}
