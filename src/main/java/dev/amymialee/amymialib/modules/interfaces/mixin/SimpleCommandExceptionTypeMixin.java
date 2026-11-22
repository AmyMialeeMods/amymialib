package dev.amymialee.amymialib.modules.interfaces.mixin;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.amymialee.amymialib.modules.interfaces.AmyMialibSimpleCommandExceptionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleCommandExceptionType.class)
public class SimpleCommandExceptionTypeMixin implements AmyMialibSimpleCommandExceptionType {
    @Shadow @Final private Message message;

    @Override
    public Message amymialib$getMessage() {
        return this.message;
    }
}