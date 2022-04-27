package eu.ha3.presencefootsteps.forge;

import dev.architectury.platform.forge.EventBuses;
import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PresenceFootsteps.MOD_ID)
public class PresenceFootstepsForge {
    public PresenceFootstepsForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(PresenceFootsteps.MOD_ID, eventBus);

        eventBus.addListener(this::clientLoad);
    }

    private void clientLoad(FMLClientSetupEvent event) {
        PresenceFootsteps.clientLoad();

        // FMLClientSetupEvent is fired after resources are reloaded, so we have to manually reload them
        PresenceFootsteps.SOUND_ENGINE.reload();
    }
}
