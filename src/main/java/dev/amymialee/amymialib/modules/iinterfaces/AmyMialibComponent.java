package dev.amymialee.amymialib.modules.iinterfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public interface AmyMialibComponent {
    @Environment(EnvType.CLIENT) default int amy$width() {
        return Minecraft.getInstance().font.width((Component) this);
    }

    @Environment(EnvType.CLIENT) default int amy$center(int x) {
        return x - this.amy$width() / 2;
    }
}