package dev.amymialee.amymialib.aspects.client;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.amymialee.amymialib.AmyMialib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.awt.*;

public class AspectEditorScreen extends Screen {
    private static final Component TITLE = Component.translatable(AmyMialib.REGISTRY.createTranslation("aspects.title", "AmyMialib Aspects"));

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final @Nullable Screen lastScreen;
    public int safeWidth;
    public int safeHeight;
    public int leftPos;
    public int topPos;

    public final AspectCategoriesTab categories = new AspectCategoriesTab(this);
    public final AspectsTab aspects = new AspectsTab(this);

    public Rectangle categoryOuter = new Rectangle();
    public Rectangle categoryInner = new Rectangle();
    public Rectangle aspectOuter = new Rectangle();
    public Rectangle aspectInner = new Rectangle();
    public int categorySpaces;

    public float categoryScroll;
    public double categoryVelocity;
    public float aspectScroll;
    public double aspectVelocity;
    public Dragging dragging = Dragging.NONE;
    public AspectWidget dragTarget = null;

    public AspectEditorScreen(final @Nullable Screen lastScreen) {
        super(TITLE);
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        this.categories.init();
        this.layout.addTitleHeader(TITLE, this.font);
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, _ -> this.rebuildWidgets()).width(40).build());
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.safeWidth = (int) (this.width * .8);
        this.safeHeight = (int) (this.height * .8);

        this.leftPos = (this.width - this.safeWidth) / 2;
        this.topPos = (this.height - this.safeHeight) / 2;
        this.layout.arrangeElements();

        this.categorySpaces = (int) Math.min(4, (this.safeWidth * .3f - 6) / 23);
        var categoryWidth = this.categorySpaces * 24;
        this.categoryOuter = new Rectangle(this.leftPos, this.topPos, categoryWidth, this.safeHeight);
        this.aspectOuter = new Rectangle(this.leftPos + categoryWidth, this.topPos, this.safeWidth - categoryWidth, this.safeHeight);
        this.categoryInner = new Rectangle(this.leftPos + 4, this.topPos + 4, categoryWidth - 6, this.safeHeight - 8);
        this.aspectInner = new Rectangle(this.leftPos + categoryWidth + 3, this.topPos + 4, this.safeWidth - categoryWidth - 8, this.safeHeight - 8);
        this.categories.repositionElements();
        this.aspects.repositionElements();
    }

    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        if (event.button() == 0) {
            var x = event.x();
            var y = event.y();
            if (this.categoryOuter.contains(x, y)) {
                for (var i = 0; i < this.categories.renderables.size(); i++) {
                    if (this.categories.renderables.get(i) instanceof AspectCategoryWidget widget && widget.contains(x, y)) {
                        this.aspects.category = widget.category;
                        AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
                        this.repositionElements();
                        break;
                    }
                }
            }
            if (this.aspectOuter.contains(x, y)) for (var renderable : this.aspects.renderables) {
                if (renderable instanceof AspectWidget widget && widget.contains(x, y)) {
                    widget.mouseClicked(x, y);
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
        graphics.nextStratum();
        this.categories.extractCategories(graphics, mouseX, mouseY, delta);
        this.aspects.extractAspects(graphics, mouseX, mouseY, delta);
        if (this.categories.hovered != null) this.categories.hovered.extractHover(graphics, delta);
        if (this.aspects.hovered != null) this.aspects.hovered.extractHover(graphics, delta);
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
                for (var renderable : this.aspects.renderables) {
                    if (!(renderable instanceof AspectWidget widget) || !widget.contains(mouseX, mouseY)) continue;
                    this.dragging = Dragging.ASPECT_WIDGET;
                    widget.mouseDragged(mouseX, mouseY, dx, dy);
                    return true;
                }
                this.dragging = Dragging.ASPECTS;
            }
        } else {
            if (dragging == Dragging.ASPECT_WIDGET) {
                if (this.dragTarget != null) {
                    this.dragTarget.mouseDragged(mouseX, mouseY, dx, dy);
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