package dev.amymialee.amymialib.modules.dossier;

/**
 * Class that provides a name for itself.
 */
public interface DossierLanyard {
    default String name() { throw new IllegalStateException("Lanyard name not implemented"); }
}