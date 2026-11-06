package dev.amymialee.amymialib.modules.iinterfaces.mixin;

import dev.amymialee.amymialib.modules.iinterfaces.AmyMialibTrackable;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayDeque;
import java.util.List;

@Mixin(Entity.class)
public class EntityMixin implements AmyMialibTrackable {
    @Shadow private Level level;

    @Override
    public Iterable<ServerPlayer> amy$getTrackers(Entity... except) {
        var self = (Entity) (Object) this;
        if (!this.level.isClientSide()) {
            var trackers = new ArrayDeque<>(PlayerLookup.tracking(self));
            if (self instanceof ServerPlayer player && player.connection != null) {
                trackers.addFirst(player);
            }
            var excluded = List.of(except);
            trackers.removeIf(excluded::contains);
            return trackers;
        }
        return List.of();
    }
}