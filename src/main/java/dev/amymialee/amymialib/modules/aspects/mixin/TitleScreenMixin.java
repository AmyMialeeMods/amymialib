package dev.amymialee.amymialib.modules.aspects.mixin;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    @Shadow protected abstract int getHorizontalPosition(int currentButton, int numberOfButtons, int buttonWidth);

    protected TitleScreenMixin(Component title) {
        super(title);
    }

//    @Definition(id = "numberOfButtons", local = @Local(type = int.class, name = "numberOfButtons"))
//    @Expression("numberOfButtons = ?")
//    @Inject(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
//    private void amymialib$aspects$extraButton(CallbackInfo ci, @Local(name = "numberOfButtons") LocalIntRef numberOfButtons) {
//        numberOfButtons.set(numberOfButtons.get() + 1);
//    }
//
//    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;getHorizontalPosition(III)I"))
//    private int amymialib$aspects$useTheVariable(TitleScreen instance, int currentButton, int staticNumber, int buttonWidth, Operation<Integer> original, @Local(name = "numberOfButtons") int numberOfButtons) {
//        return original.call(instance, currentButton, numberOfButtons, buttonWidth);
//    }
//
//    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommonButtons;friends(ILnet/minecraft/client/gui/components/Button$OnPress;Z)Lnet/minecraft/client/gui/components/FriendsButton;"))
//    private void amymialib$aspects$addAspects(CallbackInfo ci, @Local(name = "currentButton") LocalIntRef currentButton, @Local(name = "numberOfButtons") int numberOfButtons, @Local(name = "topPos") int topPos) {
//        currentButton.set(currentButton.get() + 1);
//        var aspectsButton = AmyMialibClient.aspectsButton(this);
//        aspectsButton.setPosition(this.getHorizontalPosition(currentButton.get(), numberOfButtons, 20), topPos);
//        Screens.getWidgets(this).add(aspectsButton);
//    }
}