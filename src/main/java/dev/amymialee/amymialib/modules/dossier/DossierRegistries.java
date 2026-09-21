package dev.amymialee.amymialib.modules.dossier;

import net.minecraft.core.RegistrySetBuilder;

@FunctionalInterface
public interface DossierRegistries extends DossierLanyard {
    void commitRegistries(RegistrySetBuilder registryBuilder);

    @Override default String name() { return "Registries"; }
}