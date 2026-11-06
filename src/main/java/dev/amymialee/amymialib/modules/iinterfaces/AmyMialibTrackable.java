package dev.amymialee.amymialib.modules.iinterfaces;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.List;

public interface AmyMialibTrackable {
    default Iterable<ServerPlayer> amy$getTrackers(Entity... except) {
        return List.of();
    }
}