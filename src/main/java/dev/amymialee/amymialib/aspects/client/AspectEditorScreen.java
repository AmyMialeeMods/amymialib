package dev.amymialee.amymialib.aspects.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import dev.amymialee.amymialib.AmyMialib;
import dev.amymialee.amymialib.aspects.AspectManager;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class AspectEditorScreen extends Screen {
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    private final @Nullable Screen lastScreen;
    private int leftPos;
    private int topPos;
    private final Map<AdvancementHolder, AdvancementTab> tabs = Maps.newLinkedHashMap();
    private @Nullable AdvancementTab selectedTab;
    private boolean isScrolling;
    private static final Identifier WINDOW_LOCATION = Identifier.withDefaultNamespace("textures/gui/advancements/window.png");
    public static final int WINDOW_WIDTH = 252;
    public static final int WINDOW_HEIGHT = 140;
    private static final int WINDOW_INSIDE_X = 9;
    private static final int WINDOW_INSIDE_Y = 18;
    public static final int WINDOW_INSIDE_WIDTH = 234;
    public static final int WINDOW_INSIDE_HEIGHT = 113;
    private static final int WINDOW_TITLE_X = 8;
    private static final int WINDOW_TITLE_Y = 6;
    private static final int BACKGROUND_TEXTURE_WIDTH = 256;
    private static final int BACKGROUND_TEXTURE_HEIGHT = 256;
    public static final int BACKGROUND_TILE_WIDTH = 16;
    public static final int BACKGROUND_TILE_HEIGHT = 16;
    public static final int BACKGROUND_TILE_COUNT_X = 14;
    public static final int BACKGROUND_TILE_COUNT_Y = 7;
    private static final double SCROLL_SPEED = 16.0;
    private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
    private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");
    private static final Component TITLE = Component.literal("Aspect Editor");
    public static final Identifier WINDOW_TEXTURE = AmyMialib.id("textures/gui/mvalue/mvalue_screen.png");
    public static final Identifier DARK_OAK_PLANKS = Identifier.withDefaultNamespace("textures/block/dark_oak_planks.png");
    private static final Identifier LOGO_TEXTURE = AmyMialib.id("textures/gui/logo.png");
    private static final Identifier CROWN_SILVER = AmyMialib.id("textures/gui/crown_silver.png");
    private static final Identifier CROWN_GOLD = AmyMialib.id("textures/gui/crown_gold.png");
    private static final Identifier CROWN_AMETHYST = AmyMialib.id("textures/gui/crown_amethyst.png");
    public static final int WIDTH = 370;
    public static final int HEIGHT = 200;
    private MValueCategory selectedCategory = MValue.DEFAULT_CATEGORY;
    private double categoryScroll;
    private double categoryVelocity;
    private double valueScroll;
    private double valueVelocity;

    public AspectEditorScreen() {
        this(null);
    }

    public AspectEditorScreen(final @Nullable Screen lastScreen) {
        super(Component.empty());
        this.lastScreen = lastScreen;
    }

    @Override
    public void init() {
//        if (this.minecraft == null || this.minecraft.player == null) return;
//        var centreX = this.width / 2;
//        var centreY = this.height / 2;
//        var categories = MValueCategory.CATEGORIES;
//        if (categories.size() > 14) {
//            this.addRenderableWidget(new ScrollButtonWidget(centreX - WIDTH / 2 + 23 + 11, centreY + HEIGHT / 2 - 5, ScrollButtonWidget.Type.NEXT, (a) -> this.categoryVelocity += a));
//            this.addRenderableWidget(new ScrollButtonWidget(centreX - WIDTH / 2 + 23, centreY + HEIGHT / 2 - 5, ScrollButtonWidget.Type.PREVIOUS, (a) -> this.categoryVelocity -= a));
//        }
//        var j = 0;
//        for (var category : categories) {
//            if (MValue.INVISIBLE_CATEGORY == category || category.getValues(this.minecraft.player).isEmpty()) continue;
//            if (this.selectedCategory.getValues(this.minecraft.player).isEmpty()) this.selectedCategory = category;
//            var x = centreX + 7 + 3 - WIDTH / 2 + (j % 2 == 0 ? 0 : 25);
//            var y = centreY + 7 + 3 - HEIGHT / 2 + (j / 2) * 25;
//            this.addRenderableWidget(category.getWidget(x, y, category == this.selectedCategory ? null : button -> {
//                this.selectedCategory = category;
//                this.valueScroll = 0;
//                this.valueVelocity = 0;
//                this.rebuildWidgets();
//            }));
//            j++;
//        }
//        var values = this.selectedCategory.getValues(this.minecraft.player);
//        if (values.size() > 16) {
//            this.addRenderableWidget(new ScrollButtonWidget((int) (centreX - (11f / 2f) - WIDTH / 2f + 209.5f + 11), centreY + HEIGHT / 2 - 5, ScrollButtonWidget.Type.NEXT, (a) -> this.valueVelocity += a));
//            this.addRenderableWidget(new ScrollButtonWidget((int) (centreX - (11f / 2f) - WIDTH / 2f + 209.5f), centreY + HEIGHT / 2 - 5, ScrollButtonWidget.Type.PREVIOUS, (a) -> this.valueVelocity -= a));
//        }
//        for (var i = 0; i < values.size(); i++) {
//            var value = values.get(i);
//            var x = centreX + 66 + 3 - WIDTH / 2 + (i % 2 == 0 ? 0 : 147);
//            var y = centreY + 7 + 3 - HEIGHT / 2 + (i / 2) * 21;
//            this.addRenderableWidget(((GuiEventListener & Renderable & NarratableEntry) value.getWidget(x, y)));
//        }
        var i = 0;
        for (var _ : AspectManager.ASPECTS.values()) {
            var x = this.width / 2 + 66 + 3 + (i % 2 == 0 ? 0 : 147);
            var y = this.height / 2 + 7 + 3 + (i / 2) * 21;
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, _ -> this.onClose()).bounds(x, y, 20, 20).build());
            i++;
        }

        this.layout.addTitleHeader(TITLE, this.font);
        this.tabs.clear();
        this.selectedTab = null;
        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            AdvancementTab firstTab = this.tabs.values().iterator().next();
            this.advancements.setSelectedTab(firstTab.getRootNode().holder(), true);
        } else {
            this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getRootNode().holder(), true);
        }
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose()).width(200).build());
        this.layout.visitWidgets(x$0 -> this.addRenderableWidget(x$0));
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.leftPos = (this.width - 252) / 2;
        this.topPos = (this.height - 140) / 2;
        this.layout.arrangeElements();
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.lastScreen);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.selectedTab != null) {
            int mouseX = (int)this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
            int mouseY = (int)this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
            this.selectedTab.tick(mouseX - this.leftPos - 9, mouseY - this.topPos - 18);
        }
    }

    @Override
    public boolean mouseClicked(final @NonNull MouseButtonEvent event, final boolean doubleClick) {
        if (event.button() == 0) {
            int xo = (this.width - 252) / 2;
            int yo = (this.height - 140) / 2;
            for (AdvancementTab tab : this.tabs.values()) {
                if (tab.isMouseOver(xo, yo, event.x(), event.y())) {
                    this.advancements.setSelectedTab(tab.getRootNode().holder(), true);
                    break;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractRenderState(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.nextStratum();
        this.extractInside(graphics);
        graphics.nextStratum();
        this.extractWindow(graphics, mouseX, mouseY);
        if (this.isScrolling) graphics.requestCursor(CursorTypes.RESIZE_NS);
        this.extractTooltips(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(final @NonNull MouseButtonEvent event, final double dx, final double dy) {
        if (event.button() != 0) {
            this.isScrolling = false;
            return false;
        }
        if (!this.isScrolling) {
            this.isScrolling = true;
        } else if (this.selectedTab != null) {
            this.selectedTab.scroll(dx, dy);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(final @NonNull MouseButtonEvent event) {
        this.isScrolling = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
        if (this.selectedTab != null) {
            this.selectedTab.scroll(scrollX * 16.0, scrollY * 16.0);
            return true;
        } else {
            return false;
        }
//        var left = this.width / 2 - WIDTH / 2 + 63;
//        if (mouseX < left) {
//            this.categoryVelocity -= verticalAmount;
//        } else {
//            this.valueVelocity -= verticalAmount;
//        }
//        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void extractInside(final GuiGraphicsExtractor graphics) {
        AdvancementTab tab = this.selectedTab;
        if (tab == null) {
            graphics.fill(this.leftPos + 9, this.topPos + 18, this.leftPos + 9 + 234, this.topPos + 18 + 113, -16777216);
            int midX = this.leftPos + 9 + 117;
            graphics.centeredText(this.font, NO_ADVANCEMENTS_LABEL, midX, this.topPos + 18 + 56 - 9 / 2, -1);
            graphics.centeredText(this.font, VERY_SAD_LABEL, midX, this.topPos + 18 + 113 - 9, -1);
        } else {
            tab.extractContents(graphics, this.leftPos + 9, this.topPos + 18);
        }
    }

    public void extractWindow(final @NonNull GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, WINDOW_LOCATION, this.leftPos, this.topPos, 0.0F, 0.0F, 252, 140, 256, 256);
        if (this.tabs.size() > 1) {
            for (AdvancementTab tab : this.tabs.values()) {
                tab.extractTab(graphics, this.leftPos, this.topPos, mouseX, mouseY, tab == this.selectedTab);
            }

            for (AdvancementTab tab : this.tabs.values()) {
                tab.extractIcon(graphics, this.leftPos, this.topPos);
            }
        }

        graphics.text(this.font, this.selectedTab != null ? this.selectedTab.getTitle() : TITLE, this.leftPos + 8, this.topPos + 6, -12566464, false);
    }

    private void extractTooltips(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
        if (this.selectedTab != null) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(this.leftPos + 9, this.topPos + 18);
            graphics.nextStratum();
            this.selectedTab.extractTooltips(graphics, this.leftPos, this.topPos);
            graphics.pose().popMatrix();
        }

        if (this.tabs.size() > 1) {
            for (AdvancementTab tab : this.tabs.values()) {
                if (tab.isMouseOver(this.leftPos, this.topPos, mouseX, mouseY)) {
                    graphics.setTooltipForNextFrame(this.font, tab.getTitle(), mouseX, mouseY);
                }
            }
        }
    }




    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.minecraft == null) return;
        this.categoryScroll = Math.clamp(this.categoryScroll + this.categoryVelocity, 0, Math.max(0, (MValueCategory.CATEGORIES.size() / 2f + 1) * 25 - 7.75 * 25));
        this.categoryVelocity *= .96f;
        this.valueScroll = Math.clamp(this.valueScroll + this.valueVelocity, 0, Math.max(0, (this.selectedCategory.getValues(this.minecraft.player).size() / 2f + 1) * 21 - 9 * 21));
        this.valueVelocity *= .96f;
        var right = this.width / 2 + WIDTH / 2;
        var left = this.width / 2 - WIDTH / 2;
        var backWidth = this.selectedCategory.width;
        var backHeight = this.selectedCategory.height;
        {
            var scrollAmount = this.categoryScroll + this.categoryVelocity * delta;
            context.enableScissor(left + 7, this.height / 2 - 93, left + 60, this.height / 2 + 93);
            for (var x = left + 7; x <= left + 60; x += backWidth) {
                for (var y = this.height / 2f - 103 - scrollAmount - backHeight; y <= this.height / 2f + 93; y += backHeight) {
                    context.pose().pushMatrix();
                    context.pose().translate(0, (float) y);
                    context.blit(RenderPipelines.GUI_TEXTURED, DARK_OAK_PLANKS, x, 0, backWidth, backHeight, backWidth, backHeight, backWidth, backHeight);
                    context.pose().popMatrix();
                }
            }
            context.disableScissor();
        }
        {
            var scrollAmount = this.valueScroll + this.valueVelocity * delta;
            context.enableScissor(left + 66, this.height / 2 - 93, right - 7, this.height / 2 + 93);
            for (var x = left + 66; x <= right - 7; x += backWidth) {
                for (var y = this.height / 2f - 103 - scrollAmount - backHeight; y <= this.height / 2f + 93; y += backHeight) {
                    context.pose().pushMatrix();
                    context.pose().translate(0, (float) y);
                    context.blit(RenderPipelines.GUI_TEXTURED, this.selectedCategory.backgroundTexture, x, 0, backWidth, backHeight, backWidth, backHeight, backWidth, backHeight);
                    context.pose().popMatrix();
                }
            }
            context.disableScissor();
        }
        for (var drawable : this.renderables) {
            context.pose().pushMatrix();
            if (drawable instanceof MValueCategory.MValueCategoryWidget widget) {
                context.enableScissor(left + 7, this.height / 2 - 93, right - 7, this.height / 2 + 93);
                widget.scroll = this.categoryScroll;
                widget.velocity = this.categoryVelocity;
                context.pose().translate(0, (float) -(this.categoryScroll + this.categoryVelocity * delta));
            } else if (drawable instanceof MValueWidget<?> widget) {
                context.enableScissor(left + 7, this.height / 2 - 93, right - 7, this.height / 2 + 93);
                widget.scroll = this.valueScroll;
                widget.velocity = this.valueVelocity;
                context.pose().translate(0, (float) -(this.valueScroll + this.valueVelocity * delta));
            } else {
                context.enableScissor(0, 0, this.width, this.height);
            }
            drawable.render(context, mouseX, mouseY, delta);
            context.disableScissor();
            context.pose().popMatrix();
        }
        context.pose().pushMatrix();
        context.blit(RenderPipelines.GUI_TEXTURED, WINDOW_TEXTURE, left, this.height / 2 - 100, 370, 200, 370, 200, 370, 200);
        var categoryTitle = Component.translatable(this.selectedCategory.getTranslationKey());
        context.drawString(this.font, categoryTitle, (int) (this.width / 2f - this.font.width(categoryTitle) / 2f - WIDTH / 2f + 214.5f), this.height / 2 - 110, 0xFFFFFFFF, true);
        context.drawString(this.font, Component.translatable("mialib.screen.mvalues"), this.width / 2 - WIDTH / 2, this.height / 2 - 110, 0xFFFFFFFF, true);
        context.pose().popMatrix();
    }
}
