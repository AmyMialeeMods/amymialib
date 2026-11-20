package dev.amymialee.amymialib.util;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface RaycastHelper {
    static Builder of(Entity entity, double distance) {
        return new Builder(entity.level(), entity.getEyePosition(), entity.getEyePosition().add(RaycastHelper.anglesToVector(entity.getViewXRot(1.0f), entity.getViewYRot(1.0f)).scale(distance)));
    }

    static Builder of(Level level, Vec3 start, Vec3 angle, double distance) {
        return new Builder(level, start, start.add(angle.scale(distance)));
    }

    static Builder of(Level level, Vec3 start, Vec3 end) {
        return new Builder(level, start, end);
    }

    class Builder {
        final Level level;
        final Vec3 start;
        final Vec3 end;
        Predicate<Entity> filter = (e) -> true;
        double radius = 0;
        int max = 0;

        public Builder(Level level, Vec3 start, Vec3 end) {
            this.level = level;
            this.start = start;
            this.end = end;
        }

        public Builder predicate(Predicate<Entity> filter) {
            this.filter = filter;
            return this;
        }

        public Builder max(int max) {
            this.max = max;
            return this;
        }

        public Builder radius(double radius) {
            this.radius = radius;
            return this;
        }

        public List<Entity> raycast() {
            List<Entity> hitEntities = new ArrayList<>();
            var box = new AABB(this.start.x, this.start.y, this.start.z, this.end.x, this.end.y, this.end.z);
            for (var target : this.level.getEntities((Entity) null, box, this.filter)) {
                var intersection = RaycastHelper.intersects(this.start, this.end, target, this.radius);
                if (intersection) {
                    var visible = false;
                    for (var pos : new Vec3[]{target.position(), target.position().add(0, target.getBbHeight() / 2, 0), target.position().add(0, target.getBbHeight(), 0)}) {
                        if (this.level.clip(new ClipContext(this.start, pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, target)).getType() == HitResult.Type.MISS) {
                            visible = true;
                            break;
                        }
                    }
                    if (visible) {
                        hitEntities.add(target);
                        if (this.max > 0 && hitEntities.size() >= this.max) break;
                    }
                }
            }
            return hitEntities;
        }
    }

    static Vec3 anglesToVector(double pitch, double yaw) {
        var x = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        var y = -Math.sin(Math.toRadians(pitch));
        var z = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        return new Vec3(x, y, z);
    }

    static boolean intersects(Vec3 start, Vec3 end, Entity entity, double rayRadius) {
        var entityMin = entity.position().subtract(entity.getBbWidth() / 2 + rayRadius, rayRadius, entity.getBbWidth() / 2 + rayRadius);
        var entityMax = entity.position().add(entity.getBbWidth() / 2 + rayRadius, entity.getBbHeight() + rayRadius, entity.getBbWidth() / 2 + rayRadius);
        return intersects(start, end, new AABB(entityMin, entityMax), rayRadius);
    }

    static boolean intersects(Vec3 start, Vec3 end, AABB box, double rayRadius) {
        var direction = end.subtract(start).normalize();
        var boxMin = new Vec3(box.minX - rayRadius, box.minY - rayRadius, box.minZ - rayRadius);
        var boxMax = new Vec3(box.maxX + rayRadius, box.maxY + rayRadius, box.maxZ + rayRadius);
        var tMin = 0d;
        var tMax = Double.MAX_VALUE;
        for (var axis : Direction.Axis.values()) {
            if (Math.abs(direction.get(axis)) < 1e-8) {
                if (start.get(axis) < boxMin.get(axis) || start.get(axis) > boxMax.get(axis)) return false;
            } else {
                var ood = 1.0 / direction.get(axis);
                var t1 = (boxMin.get(axis) - start.get(axis)) * ood;
                var t2 = (boxMax.get(axis) - start.get(axis)) * ood;
                if (t1 > t2) {
                    var temp = t1;
                    t1 = t2;
                    t2 = temp;
                }
                tMin = Math.max(tMin, t1);
                tMax = Math.min(tMax, t2);
                if (tMin > tMax) return false;
            }
        }
        return true;
    }
}