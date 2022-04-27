package eu.ha3.presencefootsteps.util;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;

public interface ResourceUtils {

    static void forEach(Identifier id, ResourceManager manager, Consumer<Reader> consumer) {
        try {
            manager.getAllResources(id).forEach(res -> {
                try (Reader stream = new InputStreamReader(res.getInputStream())) {
                    consumer.accept(stream);
                } catch (Exception e) {
                    PresenceFootsteps.LOGGER.error("Error encountered loading resource " + res.getId() + " from pack" + res.getResourcePackName(), e);
                }
            });
        } catch (IOException e) {
            PresenceFootsteps.LOGGER.error("Error encountered opening resources for " + id, e);
        }
    }

    static void forEachReverse(Identifier id, ResourceManager manager, Consumer<Reader> consumer) {
        try {
            List<Resource> resources = manager.getAllResources(id);
            for (int i = resources.size() - 1; i >= 0; i--) {
                Resource res = resources.get(i);
                try (Reader stream = new InputStreamReader(res.getInputStream())) {
                    consumer.accept(stream);
                } catch (Exception e) {
                    PresenceFootsteps.LOGGER.error("Error encountered loading resource " + res.getId() + " from pack" + res.getResourcePackName(), e);
                }
            }
        } catch (IOException e) {
            PresenceFootsteps.LOGGER.error("Error encountered opening resources for " + id, e);
        }
    }
}
