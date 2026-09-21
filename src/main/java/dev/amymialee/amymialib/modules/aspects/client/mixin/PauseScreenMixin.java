package dev.amymialee.amymialib.modules.aspects.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.amymialee.amymialib.AmyMialibClient;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PauseScreen.class)
public class PauseScreenMixin extends Screen {
    protected PauseScreenMixin(Component title) {
        super(title);
    }

    @WrapOperation(method = "createPauseMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/layouts/LinearLayout;spacing(I)Lnet/minecraft/client/gui/layouts/LinearLayout;", ordinal = 0))
    private LinearLayout amymialib$aspects$addAspects(LinearLayout instance, int spacing, Operation<LinearLayout> original) {
        var result = original.call(instance, spacing);
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) result.addChild(AmyMialibClient.aspectsButton(this));
        return result;
    }
}