package eu.ha3.presencefootsteps.mixin;

import eu.ha3.presencefootsteps.sound.SoundEngine;
import eu.ha3.presencefootsteps.sound.StepSoundSource;
import eu.ha3.presencefootsteps.sound.generator.StepSoundGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LivingEntity.class)
abstract class MLivingEntity extends Entity implements StepSoundSource {
    MLivingEntity() {super(null, null);}
    private final StepSoundSource stepSoundSource = new StepSoundSource.Container((LivingEntity)(Object)this);
    @Override
    public StepSoundGenerator getStepGenerator(SoundEngine engine) {
        return stepSoundSource.getStepGenerator(engine);
    }
}
