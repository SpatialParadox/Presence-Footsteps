package eu.ha3.presencefootsteps.mixin;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugHud.class)
public abstract class MDebugHud extends DrawableHelper {

    @Shadow
    private HitResult blockHit;

    @Shadow
    private HitResult fluidHit;

    @Inject(method = "getRightText", at = @At("RETURN"))
    protected void onGetRightText(CallbackInfoReturnable<List<String>> info) {
        PresenceFootsteps.DEBUG_HUD.render(blockHit, fluidHit, info.getReturnValue());
    }
}
