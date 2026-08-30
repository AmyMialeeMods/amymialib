package dev.amymialee.amymialib.aspects.client;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.AmyMialibClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.*;

public class AspectEditorScreen extends Screen {
    public static final Identifier FILL = AmyMialib.id("aspects/fill");
    public static final Identifier BORDER = AmyMialib.id("aspects/border");
    public static final Identifier THIN = AmyMialib.id("aspects/thin");

    public final @Nullable Screen lastScreen;
    public int safeWidth;
    public int safeHeight;
    public int leftPos;
    public int topPos;

    public final AspectCategoriesTab categories = new AspectCategoriesTab(this);
    public final AspectsTab aspects = new AspectsTab(this);

    public final StringWidget title = new StringWidget(AmyMialibClient.ASPECTS_SCREEN, this.font);
    public final Button done;

    public Rectangle categoryOuter = new Rectangle();
    public Rectangle categoryInner = new Rectangle();
    public Rectangle aspectOuter = new Rectangle();
    public Rectangle aspectInner = new Rectangle();
    public int categorySpaces;
    public int aspectSpaces;

    public float categoryScroll;
    public double categoryVelocity;
    public float aspectScroll;
    public double aspectVelocity;
    public Dragging dragging = Dragging.NONE;
    public AspectWidget dragTarget = null;

    public AspectEditorScreen(final @Nullable Screen lastScreen) {
        super(AmyMialibClient.ASPECTS_SCREEN);
        this.lastScreen = lastScreen;
        done = Button.builder(CommonComponents.GUI_DONE, _ -> this.rebuildWidgets()).size(80, 20).build();
    }

    @Override
    protected void init() {
        this.categories.init();
        this.addRenderableOnly(this.title);
        this.addRenderableWidget(this.done);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.safeWidth = (int) (this.width * .9);
        this.safeHeight = (int) (this.height * .8);
        var remaining = this.safeWidth - 16;
        var categorySize = 20;
        var categoryGap = 2 + 2;
        var aspectSize = 100;
        var aspectGap = 4 + 2;
        this.categorySpaces = 1;
        this.aspectSpaces = 1;
        remaining -= categorySize + aspectSize;
        while (true) {
            var hasCategorySpace = !(this.categorySpaces >= this.aspectSpaces) && this.categorySpaces < 3 && remaining > categorySize + categoryGap;
            var hasAspectSpace = this.aspectSpaces < 4 && this.aspectSpaces <= this.categorySpaces && remaining > aspectSize + aspectGap;
            var hasSpace = hasCategorySpace || hasAspectSpace;
            if (!hasSpace) break;
            if (hasAspectSpace) {
                this.aspectSpaces++;
                remaining -= aspectSize + aspectGap;
            } else {
                this.categorySpaces++;
                remaining -= categorySize + categoryGap;
            }
        }
        this.safeWidth -= remaining;
        this.leftPos = (this.width - this.safeWidth) / 2;
        this.topPos = (this.height - this.safeHeight) / 2;
        var categoryWidth = this.categorySpaces * 24;
        this.categoryOuter = new Rectangle(this.leftPos, this.topPos + 8, categoryWidth, this.safeHeight - 16);
        this.categoryInner = new Rectangle(categoryOuter.x + 4 + 1, categoryOuter.y + 4, categoryOuter.width - 6, categoryOuter.height - 8);
        this.aspectOuter = new Rectangle(this.leftPos + categoryWidth + 1, this.topPos, this.safeWidth - categoryWidth, this.safeHeight);
        this.aspectInner = new Rectangle(aspectOuter.x + 2, aspectOuter.y + 4 + 36, aspectOuter.width - 6, aspectOuter.height - 8 - 36);
        this.categories.repositionElements();
        this.aspects.repositionElements();
        this.title.setPosition((int) (aspectOuter.getCenterX() - AmyMialibClient.ASPECTS_SCREEN.am$width() / 2f), (int) (aspectOuter.getMinY() - 12));
        this.done.setPosition(this.width / 2 - 40, this.topPos + this.safeHeight + 4);
    }

    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        if (event.button() == 0 || event.button() == 1) {
            var x = event.x();
            var y = event.y();
            if (this.categoryInner.contains(x, y)) {
                for (var i = 0; i < this.categories.renderables.size(); i++) {
                    if (this.categories.renderables.get(i) instanceof AspectCategoryWidget widget && widget.contains(x, y)) {
                        this.aspects.category = widget.category;
                        AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
                        this.init();
                        break;
                    }
                }
            }
            if (this.aspectInner.contains(x, y)) for (var renderable : this.aspects.renderables) {
                if (renderable instanceof AspectWidget widget && widget.contains(x, y)) {
                    AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
                    widget.mouseClicked(event, x, y);
                    break;
                }
            }
            if (this.aspectOuter.contains(x, y)) for (var renderable : this.aspects.renderablesHeader) {
                if (renderable instanceof AspectLinkWidget widget && widget.contains(x, y)) {
                    AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
                    widget.mouseClicked(event, x, y);
                    break;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float delta) {
        this.categoryScroll = (float) Mth.clamp(this.categoryScroll + this.categoryVelocity, -getMaxCategoryScroll(), 0);
        this.categoryVelocity *= .96f;
        this.aspectScroll = (float) Mth.clamp(this.aspectScroll + this.aspectVelocity, -getMaxAspectScroll(), 0);
        this.aspectVelocity *= .96f;
        if (this.dragging.is()) graphics.requestCursor(CursorTypes.RESIZE_NS);
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.nextStratum();
        this.categories.fade = Mth.lerp(.01f, this.categories.fade, this.categories.hovered != null || this.aspects.hovered != null || this.aspects.hoveredLink != null ? .6f : 0);
        this.aspects.fade = this.categories.fade;
        this.categories.extractCategories(graphics, mouseX, mouseY, delta);
        this.aspects.extractAspects(graphics, mouseX, mouseY, delta);
        graphics.nextStratum();
        if (this.categories.hovered != null) this.categories.hovered.extractHover(graphics, delta);
        if (this.aspects.hovered != null) this.aspects.hovered.extractHover(graphics, mouseX, mouseY, delta);
        if (this.aspects.hoveredLink != null) this.aspects.hoveredLink.extractHover(graphics, delta);
    }

    public static void border(@NonNull GuiGraphicsExtractor graphics, int x, int y, int width, int height, int colour) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.FILL, x, y, width, height);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.BORDER, x, y, width, height, colour);
    }

    public static void thin(@NonNull GuiGraphicsExtractor graphics, int x, int y, int width, int height, int colour) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.FILL, x, y, width, height);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, AspectEditorScreen.THIN, x, y, width, height, colour);
    }

    @Override
    public void tick() {
        super.tick();
        var mouseX = (int) this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
        var mouseY = (int) this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
        this.categories.tickHovered(mouseX, mouseY);
        this.aspects.tickHovered(mouseX, mouseY);
    }

    private void drag(final @NonNull Dragging dragging, final double scrollY) {
        switch (dragging) {
            case CATEGORIES -> this.categoryScroll = (float) Mth.clamp(this.categoryScroll + scrollY, -getMaxCategoryScroll(), 0);
            case ASPECTS -> this.aspectScroll = (float) Mth.clamp(this.aspectScroll + scrollY, -getMaxAspectScroll(), 0);
        }
    }

    @Override
    public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
        var dragging = Dragging.NONE;
        if (this.categoryOuter.contains(x, y)) {
            dragging = Dragging.CATEGORIES;
        } else if (this.aspectOuter.contains(x, y)) {
            dragging = Dragging.ASPECTS;
        }
        if (dragging != Dragging.NONE) {
            switch (dragging) {
                case CATEGORIES -> this.categoryVelocity += scrollY;
                case ASPECTS -> this.aspectVelocity += scrollY;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(final @NonNull MouseButtonEvent event, final double dx, final double dy) {
        if (event.button() != 0) return false;
        var mouseX = this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
        var mouseY = this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
        if (!this.dragging.is()) {
            if (this.categoryOuter.contains(mouseX, mouseY)) {
                this.dragging = Dragging.CATEGORIES;
            } else if (this.aspectOuter.contains(mouseX, mouseY)) {
                if (this.aspectInner.contains(mouseX, mouseY)) for (var renderable : this.aspects.renderables) {
                    if (!(renderable instanceof AspectWidget widget) || !widget.contains(mouseX, mouseY)) continue;
                    this.dragging = Dragging.ASPECT_WIDGET;
                    widget.mouseDragged(mouseX);
                    return true;
                }
                this.dragging = Dragging.ASPECTS;
            }
        } else {
            if (dragging == Dragging.ASPECT_WIDGET) {
                if (this.dragTarget != null) {
                    this.dragTarget.mouseDragged(mouseX);
                } else {
                    this.dragging = Dragging.NONE;
                }
            } else {
                this.drag(this.dragging, dy);
            }
            this.categoryVelocity = 0;
            this.aspectVelocity = 0;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(final @NonNull MouseButtonEvent event) {
        this.dragging = Dragging.NONE;
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.lastScreen);
    }

    public double getMaxCategoryScroll() {
        return Math.max(0, this.categories.getHeight() - this.categoryInner.getHeight());
    }

    public double getMaxAspectScroll() {
        return Math.max(0, this.aspects.getHeight() - this.aspectInner.getHeight());
    }

    public enum Dragging {
        NONE,
        CATEGORIES,
        ASPECTS,
        ASPECT_WIDGET;

        public boolean is() {
            return this != NONE;
        }
    }
}