package eu.ha3.presencefootsteps.sound.player;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import eu.ha3.presencefootsteps.sound.Options;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import eu.ha3.presencefootsteps.util.PlayerUtil;
import eu.ha3.presencefootsteps.world.Association;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.Random;

/**
 * A Library that can also play sounds and default footsteps.
 *
 * @author Hurry
 */
public class ImmediateSoundPlayer implements SoundPlayer, StepSoundPlayer {

    private final Random random = new Random();

    private final DelayedSoundPlayer delayedPlayer = new DelayedSoundPlayer(this);

    private final SoundEngine engine;

    public ImmediateSoundPlayer(SoundEngine engine) {
        this.engine = engine;
    }

    @Override
    public Random getRNG() {
        return random;
    }

    @Override
    public void playStep(Association assos) {
        BlockSoundGroup soundType = assos.getSoundGroup();

        if (!assos.getMaterial().isLiquid() && soundType != null) {
            BlockState beside = assos.getSource().world.getBlockState(assos.getPos().up());

            if (beside.getBlock() == Blocks.SNOW) {
                soundType = Blocks.SNOW.getSoundGroup(beside);
            }

            playAttenuatedSound(assos.getSource(), soundType.getStepSound().getId().toString(), soundType.getVolume() * 0.15F, soundType.getPitch());
        }
    }

    @Override
    public void playSound(Entity location, String soundName, float volume, float pitch, Options options) {

        if (options.containsKey("delay_min") && options.containsKey("delay_max")) {
            delayedPlayer.playSound(location, soundName, volume, pitch, options);

            return;
        }

        playAttenuatedSound(location, soundName, volume, pitch);
    }

    private void playAttenuatedSound(Entity location, String soundName, float volume, float pitch) {
        MinecraftClient mc = MinecraftClient.getInstance();
        double distance = mc.gameRenderer.getCamera().getPos().squaredDistanceTo(location.getPos());

        volume *= engine.getGlobalVolume();
        volume *= (100 - distance) / 100F;

        PositionedSoundInstance sound = createSound(getSoundId(soundName, location), volume, pitch, location);

        if (distance > 100) {
            mc.getSoundManager().play(sound, (int) Math.floor(Math.sqrt(distance) / 2));
        } else {
            mc.getSoundManager().play(sound);
        }
    }

    @Override
    public void think() {
        delayedPlayer.think();
    }

    private PositionedSoundInstance createSound(Identifier id, float volume, float pitch, Entity entity) {
        return new PositionedSoundInstance(id,
                entity.getSoundCategory(),
                volume, pitch, false, 0,
                SoundInstance.AttenuationType.LINEAR,
                (float) entity.getX(),
                (float) entity.getY(),
                (float) entity.getZ(),
                false);
    }

    private Identifier getSoundId(String name, Entity location) {
        if (name.indexOf(':') >= 0) {
            return new Identifier(name);
        }

        String domain = PresenceFootsteps.MOD_ID;

        if (!PlayerUtil.isClientPlayer(location)) {
            domain += "mono"; // Switch to mono if playing another player
        }

        return new Identifier(domain, name);
    }
}
