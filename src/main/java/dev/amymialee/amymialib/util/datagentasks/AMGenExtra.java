package dev.amymialee.amymialib.util.datagentasks;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public interface AMGenExtra extends AMNameProvider {
    default void addExtraDataProviders(FabricDataGenerator.Pack pack) {}
}