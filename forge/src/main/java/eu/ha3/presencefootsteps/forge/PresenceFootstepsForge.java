package eu.ha3.presencefootsteps.forge;

import dev.architectury.platform.forge.EventBuses;
import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkConstants;

@Mod(PresenceFootsteps.MOD_ID)
public class PresenceFootstepsForge {
    public PresenceFootstepsForge() {
        // Ignore server version of mod
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

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
