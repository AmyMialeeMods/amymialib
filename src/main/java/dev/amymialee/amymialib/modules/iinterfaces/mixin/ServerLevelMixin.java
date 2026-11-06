package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTrackable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements AmyMialibTrackable {
    @Shadow public abstract List<ServerPlayer> players();

    @Override
    public Iterable<ServerPlayer> amy$getTrackers(Entity... except) {
        var trackers = this.players();
        var excluded = List.of(except);
        trackers.removeIf(excluded::contains);
        return trackers;
    }
}