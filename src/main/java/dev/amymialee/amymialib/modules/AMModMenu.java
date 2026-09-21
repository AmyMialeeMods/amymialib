package dev.amymialee.amymialib.modules;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.amymialee.amymialib.modules.aspects.client.AspectEditorScreen;

public class AMModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AspectEditorScreen::new;
    }
}