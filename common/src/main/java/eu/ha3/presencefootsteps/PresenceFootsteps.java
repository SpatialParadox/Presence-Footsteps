package eu.ha3.presencefootsteps;

import dev.architectury.event.EventHandler;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.ReloadListenerRegistry;
import eu.ha3.presencefootsteps.config.ModConfig;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PresenceFootsteps {
    public static final String MOD_ID = "presencefootsteps";
    public static final Logger LOGGER = LogManager.getLogger("PFSolver");
    public static ModConfig CONFIG;
    public static SoundEngine SOUND_ENGINE;
    public static DebugHUD DEBUG_HUD;

    public static void clientLoad() {
        // Load config & sound engine
        CONFIG = new ModConfig(Platform.getConfigFolder().resolve(MOD_ID + ".json"));
        CONFIG.load();

        SOUND_ENGINE = new SoundEngine(CONFIG);
        DEBUG_HUD = new DebugHUD(SOUND_ENGINE);
        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, SOUND_ENGINE);

        ClientTickEvent.CLIENT_POST.register(PresenceFootsteps::onTick);
        EventHandler.init();
    }

    private static void onTick(MinecraftClient client) {
        if (client.getCameraEntity() == null || client.getCameraEntity().isRemoved()) {
            return;
        }

        SOUND_ENGINE.onFrame(client, client.getCameraEntity());
    }
}
