package dev.amymialee.amymialib.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

public enum AMEnvironmentType {
    SERVER(true, false),
    CLIENT_OFFLINE(false, true),
    CLIENT_SERVER(false, true),
    CLIENT_SINGLEPLAYER(true, true);

    public final boolean isServer;
    public final boolean isClient;

    AMEnvironmentType(boolean isServer, boolean isClient) {
        this.isServer = isServer;
        this.isClient = isClient;
    }

    public static AMEnvironmentType getEnv() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) return getClientEnv();
        return SERVER;
    }

    private static AMEnvironmentType getClientEnv() {
        if (Minecraft.getInstance().getSingleplayerServer() != null) return CLIENT_SINGLEPLAYER;
        if (Minecraft.getInstance().getConnection() != null) return CLIENT_SERVER;
        return CLIENT_OFFLINE;
    }
}