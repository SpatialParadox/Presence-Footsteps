package eu.ha3.presencefootsteps.fabric;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.fabricmc.api.ClientModInitializer;

public class PresenceFootstepsFabric implements ClientModInitializer {
    public PresenceFootstepsFabric() {

    }
    @Override
    public void onInitializeClient() {
        PresenceFootsteps.clientLoad();
    }
}
