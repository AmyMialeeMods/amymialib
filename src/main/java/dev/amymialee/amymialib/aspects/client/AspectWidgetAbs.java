//package dev.amymialee.amymialib.aspects.client;
//
//import dev.amymialee.amymialib.aspects.Aspect;
//import net.fabricmc.api.EnvType;
//import net.fabricmc.api.Environment;
//import net.fabricmc.loader.api.FabricLoader;
//import net.minecraft.ChatFormatting;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.components.AbstractWidget;
//import net.minecraft.client.gui.components.Tooltip;
//import net.minecraft.client.gui.narration.NarrationElementOutput;
//import net.minecraft.client.input.MouseButtonEvent;
//import net.minecraft.client.renderer.RenderPipelines;
//import net.minecraft.network.chat.Component;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//import org.jspecify.annotations.NonNull;
//
//@Environment(EnvType.CLIENT)
//public abstract class AspectWidgetAbs<T> extends AbstractWidget {
//    public final Aspect<T> aspect;
//    public double scroll;
//    public double velocity;
//    public boolean scissorContains;
//
//    public AspectWidgetAbs(int x, int y, @NotNull Aspect<T> aspect) {
//        super(x, y, 90, 18, aspect.name);
//        this.aspect = aspect;
//        this.setTooltip(Tooltip.create(this.aspect.description));
//    }
//
//    @Override
//    public void setTooltip(@Nullable Tooltip tooltip) {
//        if (tooltip != null) {
//            var name = this.aspect.id.getNamespace();
//            var container = FabricLoader.getInstance().getModContainer(this.aspect.id.getNamespace());
//            if (container.isPresent()) {
//                name = container.get().getMetadata().getName();
//            }
//            var content = tooltip.message;
//            content = content.copy().append("\n").append(Component.literal(name).withStyle(ChatFormatting.BLUE));
//            tooltip = Tooltip.create(content, tooltip.narration);
//        }
//        super.setTooltip(tooltip);
//    }
//
//    @Override
//    protected void renderWidget(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
//        var client = Minecraft.getInstance();
//        if (client == null || client.level == null) return;
//        var scroll = this.scroll + this.velocity * delta;
//        this.scissorContains = context.containsPointInScissor(mouseX, mouseY);
//        this.isHovered = this.scissorContains
//                && mouseX >= this.getX()
//                && mouseY >= this.getY() - scroll
//                && mouseX < this.getX() + this.width
//                && mouseY < this.getY() + this.height - scroll;
//        var textures = this.aspect.clientSide ? CLIENT_BUTTON_TEXTURES : BUTTON_TEXTURES;
//        context.blitSprite(RenderPipelines.GUI_TEXTURED, textures.get(true, this.isHovered), this.getX(), this.getY(), 18, this.height, 0xFFFFFFFF);
//        context.blitSprite(RenderPipelines.GUI_TEXTURED, textures.get(true, this.isHovered), this.getX() + 18, this.getY(), this.width - 18, this.height, 0xFFFFFFFF);
//        context.renderItem(this.aspect.getStack(), this.getX() + 1, this.getY() + 1);
//        final Consumer3<Float, Float, Float> moveAndScale = (x, y, s) -> {
//            context.pose().pushMatrix();
//            context.pose().translate(x, y);
//            context.pose().scale(s);
//        };
//        var scale = 0.7f;
//        moveAndScale.accept(this.getX() + this.getWidth() - 3f, this.getY() + this.getHeight() / 2f, scale);
//        var valueText = Component.literal(this.aspect.type.getValueAsString(this.aspect));
//        context.drawString(client.font, valueText, -client.font.width(valueText), -client.font.lineHeight / 2, 0xFFFFFFFF, true);
//        context.pose().popMatrix();
//        moveAndScale.accept(this.getX() + 21f, this.getY() + this.getHeight() / 2f, scale);
//        var wrapLines = client.font.split(this.getMessage(), 165 - client.font.width(valueText));
//        for (var i = 0; i < wrapLines.size(); i++) {
//            var text = wrapLines.get(i);
//            var y = -(wrapLines.size()) * client.font.lineHeight * .5 + client.font.lineHeight * i;
//            context.drawString(client.font, text, 0, (int) y, 0xFFFFFFFF, true);
//        }
//        context.pose().popMatrix();
//    }
//
//    @Override
//    public void onClick(@NonNull MouseButtonEvent click, boolean doubled) {
//        if (click.x() < this.getX() + 18) {
//            this.aspect.send(this.aspect.type.defaultValue);
//        } else {
//            this.aspect.send(!this.aspect.get());
//        }
//    }
//
//    @Override
//    public boolean isMouseOver(double mouseX, double mouseY) {
//        return this.scissorContains && this.active && this.visible
//                && mouseX >= this.getX()
//                && mouseY >= this.getY() - this.scroll
//                && mouseX < this.getX() + this.width
//                && mouseY < this.getY() + this.height - this.scroll;
//    }
//
//    @Override
//    public void updateWidgetNarration(@NonNull NarrationElementOutput builder) {
//        this.defaultButtonNarrationText(builder);
//    }
//}
