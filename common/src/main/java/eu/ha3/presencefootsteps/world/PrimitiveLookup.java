package eu.ha3.presencefootsteps.world;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class PrimitiveLookup implements Lookup<BlockSoundGroup> {

    private final Map<String, Map<Identifier, String>> substrates = new LinkedHashMap<>();

    @Override
    public String getAssociation(BlockSoundGroup sounds, String substrate) {

        Identifier id = sounds.getStepSound().getId();
        Map<Identifier, String> primitives = substrates.get(substrate);

        if (primitives == null) {
            // Check for break sound
            primitives = substrates.get("break_" + id.getPath());
        }

        if (primitives == null) {
            // Check for default
            primitives = substrates.get(EMPTY_SUBSTRATE);
        }

        if (primitives == null) {
            return Emitter.UNASSIGNED;
        }

        return primitives.getOrDefault(id, Emitter.UNASSIGNED);
    }

    @Override
    public Set<String> getSubstrates() {
        return substrates.keySet();
    }

    @Override
    public void add(String key, String value) {
        String[] split = key.trim().split("@");

        String primitive = split[0];
        String substrate = split.length > 1 ? split[1] : EMPTY_SUBSTRATE;

        substrates
            .computeIfAbsent(substrate, s -> new LinkedHashMap<>())
            .put(new Identifier(primitive), value);
    }

    @Override
    public boolean contains(BlockSoundGroup sounds) {
        Identifier primitive = sounds.getStepSound().getId();

        for (Map<Identifier, String> primitives : substrates.values()) {
            if (primitives.containsKey(primitive)) {
                return true;
            }
        }
        return false;
    }
}
