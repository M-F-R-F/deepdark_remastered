package org.mfrf.deepdark_remastered.events;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mfrf.deepdark_remastered.registry.DimensionsAndBiomes;

import java.util.Optional;

@Mod.EventBusSubscriber
public class EventsHandler {

    @SubscribeEvent
    public static void checkMobSpawn(LivingSpawnEvent.CheckSpawn event) {
        LevelAccessor level = event.getLevel();
        if (level instanceof Level level1 && isInDeepdark(level1)) {
            Mob entity = event.getEntity();
            Optional.ofNullable(entity.getAttribute(Attributes.MAX_HEALTH)).ifPresent(attributeInstance -> attributeInstance.addTransientModifier(new AttributeModifier("deepdark", 2, AttributeModifier.Operation.MULTIPLY_TOTAL)));
            Optional.ofNullable(entity.getAttribute(Attributes.ATTACK_DAMAGE)).ifPresent(attributeInstance -> attributeInstance.addTransientModifier(new AttributeModifier("deepdark", 2, AttributeModifier.Operation.MULTIPLY_TOTAL)));
            Optional.ofNullable(entity.getAttribute(Attributes.MOVEMENT_SPEED)).ifPresent(attributeInstance -> attributeInstance.addTransientModifier(new AttributeModifier("deepdark", 2, AttributeModifier.Operation.MULTIPLY_TOTAL)));
            Optional.ofNullable(entity.getAttribute(Attributes.JUMP_STRENGTH)).ifPresent(attributeInstance -> attributeInstance.addTransientModifier(new AttributeModifier("deepdark", 2, AttributeModifier.Operation.MULTIPLY_TOTAL)));
            Optional.ofNullable(entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).ifPresent(attributeInstance -> attributeInstance.addTransientModifier(new AttributeModifier("deepdark", 2, AttributeModifier.Operation.MULTIPLY_TOTAL)));
            Optional.ofNullable(entity.getAttribute(Attributes.FOLLOW_RANGE)).ifPresent(attributeInstance -> attributeInstance.addTransientModifier(new AttributeModifier("deepdark", 2, AttributeModifier.Operation.MULTIPLY_TOTAL)));
            entity.setHealth(entity.getMaxHealth());
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Level level = player.level;
        if (isInDeepdark(level)) {
            if (level.getLightEmission(player.blockPosition()) < 8) {
                player.hurt(DamageSource.OUT_OF_WORLD, 2 + (player.getMaxHealth() - player.getHealth()));
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 2, 1, true, false));
            }
        }
    }

    private static boolean isInDeepdark(Level level) {
        return level.dimension().equals(DimensionsAndBiomes.DEEPDARK);
    }
}
