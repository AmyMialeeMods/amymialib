package dev.amymialee.amymialib.modules.aspects.client;

import dev.amymialee.amymialib.modules.aspects.AspectCategory;
import dev.amymialee.amymialib.modules.aspects.AspectManager;
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
            this.namedCategories.computeIfAbsent(name, _ -> new ArrayList<>()).add(category);
        });
    }

    public void repositionElements() {
        this.renderables.clear();
        var y = this.screen.categoryInner.y + 4;
        for (var entry : this.namedCategories.entrySet().stream().sorted(Map.Entry.comparingByKey()).toList()) {
            var name = Component.literal(entry.getKey());
            this.renderables.add(new StringWidget(this.screen.categoryInner.x - 2, y, name.am$width(), 9, name, this.screen.getFont()).setMaxWidth(this.screen.categoryInner.width + 4, StringWidget.TextOverflow.SCROLLING));
            var categoryWidth = 20;
            var categoryGap = 2;
            y += 12;
            var categories = entry.getValue();
            for (var i = 0; i < categories.size(); i++) {
                var category = categories.get(i);
                var tileOffset = this.screen.categoryInner.width / 2f - (this.screen.categorySpaces * categoryWidth + (this.screen.categorySpaces - 1) * categoryGap) / 2f + (i % this.screen.categorySpaces) * (categoryWidth + categoryGap);
                if (i >= (categories.size() / this.screen.categorySpaces) * this.screen.categorySpaces) {
                    var onRow = categories.size() % this.screen.categorySpaces;
                    tileOffset = this.screen.categoryInner.width / 2f - (onRow * categoryWidth + (onRow - 1) * categoryGap) / 2f + (i % onRow) * (categoryWidth + categoryGap);
                }
                this.renderables.add(new AspectCategoryWidget(this.screen, category, (int) (this.screen.categoryInner.x + tileOffset), y));
                if (i % this.screen.categorySpaces == this.screen.categorySpaces - 1) y += 22;
            }
            if (categories.size() % this.screen.categorySpaces != 0) y += 22;
            y += 2;
        }
        this.height = 4 + this.namedCategories.values().stream().mapToInt(l -> {
            var size = 14;
            for (var i = 0; i < l.size(); i++) if (i % this.screen.categorySpaces == this.screen.categorySpaces - 1) size += 22;
            if (l.size() % this.screen.categorySpaces != 0) size += 22;
            return size;
        }).sum();
    }

    public void tickHovered(int mouseX, int mouseY) {
        this.hovered = null;
        if (this.screen.categoryInner.contains(mouseX, mouseY)) for (var renderable : this.renderables) if (renderable instanceof AspectCategoryWidget widget) if (widget.contains(mouseX, mouseY)) {
            this.hovered = widget;
            break;
        }
    }

    public void extractCategories(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, float delta) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.FILL, this.screen.categoryOuter.x, this.screen.categoryOuter.y, this.screen.categoryOuter.width + 8, this.screen.categoryOuter.height);
        graphics.enableScissor(this.screen.categoryOuter.x, this.screen.categoryOuter.y, this.screen.categoryOuter.x + this.screen.categoryOuter.width, this.screen.categoryOuter.y + this.screen.categoryOuter.height);
        graphics.pose().pushMatrix();
        graphics.pose().translate(0, this.screen.categoryScroll);
        this.renderables.forEach(renderable -> {
            if (renderable instanceof StringWidget widget && widget.getWidth() < this.screen.categoryInner.width) {
                var x = widget.getX();
                var y = widget.getY();
                widget.setPosition(this.screen.categoryInner.x + this.screen.categoryInner.width / 2 - widget.getWidth() / 2, y);
                renderable.extractRenderState(graphics, mouseX, mouseY, delta);
                widget.setPosition(x, y);
            } else {
                renderable.extractRenderState(graphics, mouseX, mouseY, delta);
            }
        });
        graphics.pose().popMatrix();
        graphics.disableScissor();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.FILL, this.screen.categoryOuter.x, this.screen.categoryOuter.y, this.screen.categoryOuter.width + 8, this.screen.categoryOuter.height, Mth.hsvToArgb(0f, 0f, 0f, Mth.floor(this.fade * 255.0F) << 24));
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.BORDER, this.screen.categoryOuter.x, this.screen.categoryOuter.y, this.screen.categoryOuter.width + 8, this.screen.categoryOuter.height, Mth.hsvToArgb(.82f, .61f, 1f, 255));
    }

    public int getHeight() {
        return this.height;
    }
}
