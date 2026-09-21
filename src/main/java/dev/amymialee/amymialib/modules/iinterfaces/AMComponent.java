package dev.amymialee.amymialib.modules.iinterfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public interface AMComponent {
    @Environment(EnvType.CLIENT)
    default int am$width() {
        return Minecraft.getInstance().font.width((Component) this);
    }

    @Environment(EnvType.CLIENT)
    default int am$center(int x) {
        return x - this.am$width() / 2;
    }
}