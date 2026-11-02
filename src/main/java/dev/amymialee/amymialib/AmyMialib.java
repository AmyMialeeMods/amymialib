package dev.amymialee.amymialib;

import dev.amymialee.amymialib.modules.aspects.Aspect;
import dev.amymialee.amymialib.modules.aspects.AspectCategory;
import dev.amymialee.amymialib.modules.aspects.AspectLink;
import dev.amymialee.amymialib.modules.aspects.AspectType;
import dev.amymialee.amymialib.modules.scribbler.Scribbler;
import dev.amymialee.amymialib.modules.scribbler.auto.Decipher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmyMialib implements ModInitializer {
    public static final String MOD_ID = "amymialib";
    public static final Logger LOGGER = LoggerFactory.getLogger(FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getName());
    @Decipher("AmyMialib")
    public static final Scribbler SCRIBBLER = new Scribbler(MOD_ID).auto("dev.amymialee.amymialib");

    @Decipher({"AmyMialee", "AmyMialee Website"})
    public static final AspectLink p = SCRIBBLER.link("website", "https://amymialee.dev").icon(Items.DIAMOND).build();
    @Decipher({"AmyMialib", "AmyMialib Aspects"})
    public static final AspectCategory AMYMIALIB = SCRIBBLER.category("amymialib").link(p).icon(Items.DIAMOND).backgroundSprite(id("aspects/background_2")).build();
    @Decipher({"Disable End Portals", "Should end portals be disabled?"})
    public static final Aspect<Boolean> DISABLE_END_PORTALS = SCRIBBLER.config("disable_end_portals", false, AspectType.BOOLEAN).icon(Items.END_PORTAL_FRAME).category(AMYMIALIB).build();
    //TODO: Disable MovedTooQuickly

    @Override public void onInitialize() {}

    public static Identifier id(String path) { return Identifier.fromNamespaceAndPath(MOD_ID, path); }
}