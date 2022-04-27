package eu.ha3.presencefootsteps.mixin;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MClientPlayNetworkHandler implements ClientPlayPacketListener {
    @Inject(method = "onPlaySound(Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    public void onHandleSoundEffect(PlaySoundS2CPacket packet, CallbackInfo info) {
        if (PresenceFootsteps.SOUND_ENGINE.onSoundRecieved(packet.getSound(), packet.getCategory())) {
            info.cancel();
        }
    }
}
