package dev.amymialee.amymialib.modules.interfaces;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public interface ScrawledComponent {
    @Environment(EnvType.CLIENT) default int amy$width() {
        return Minecraft.getInstance().font.width((Component) this);
    }

    @Environment(EnvType.CLIENT) default int amy$center(int x) {
        return x - this.amy$width() / 2;
    }
}