package dev.amymialee.amymialib.aspects.client.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import dev.amymialee.amymialib.AmyMialib;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Shadow protected abstract int getHorizontalPosition(int currentButton, int numberOfButtons, int buttonWidth);

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Definition(id = "numberOfButtons", local = @Local(type = int.class, name = "numberOfButtons"))
    @Expression("numberOfButtons = ?")
    @Inject(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private void amymialib$aspects$extraButton(CallbackInfo ci, @Local(name = "numberOfButtons") @NonNull LocalIntRef numberOfButtons) {
        numberOfButtons.set(numberOfButtons.get() + 1);
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;getHorizontalPosition(III)I"))
    private int amymialib$aspects$useTheVariable(TitleScreen instance, int currentButton, int staticNumber, int buttonWidth, @NonNull Operation<Integer> original, @Local(name = "numberOfButtons") int numberOfButtons) {
        return original.call(instance, currentButton, numberOfButtons, buttonWidth);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommonButtons;friends(ILnet/minecraft/client/gui/components/Button$OnPress;Z)Lnet/minecraft/client/gui/components/FriendsButton;"))
    private void amymialib$aspects$addAspects(CallbackInfo ci, @Local(name = "currentButton") @NonNull LocalIntRef currentButton, @Local(name = "numberOfButtons") int numberOfButtons, @Local(name = "topPos") int topPos) {
        currentButton.set(currentButton.get() + 1);
        var aspectsButton = AmyMialib.aspectsButton(this);
        aspectsButton.setPosition(this.getHorizontalPosition(currentButton.get(), numberOfButtons, 20), topPos);
        Screens.getWidgets(this).add(aspectsButton);
    }
}