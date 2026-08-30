package dev.amymialee.amymialib.util.iinterfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public interface AMComponent {
    /**
     * Only works on Client
     */
    default int am$width() {
        return Minecraft.getInstance().font.width((Component) this);
    }

    /**
     * Only works on Client
     */
    default int am$center(int x) {
        return x - this.am$width() / 2;
    }
}