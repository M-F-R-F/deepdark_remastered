package org.mfrf.deepdark_remastered.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
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
        //try to determine if player is survivor mode
        if (isInDeepdark(level)) {
            if (!canSurvive(player, level)) {
                if (level.getGameTime() % 20 == 0) {
                    boolean onGround = player.isOnGround();
                    if (onGround) {
                        player.hurt(DamageSource.OUT_OF_WORLD, 2 + (player.getMaxHealth() - player.getHealth()));
                    } else {
                        player.hurt(DamageSource.OUT_OF_WORLD, (player.getHealth() - 1) * (player.getMaxHealth() - player.getHealth()) / 2);
                    }
                }
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 5, 1, true, false));
            }
        }

    }

    private static boolean canSurvive(Player player, Level level) {
        if (player.getCooldowns().isOnCooldown(Items.ENDER_PEARL)) return true;
        BlockPos blockPos = player.blockPosition();
        int maxLight = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    maxLight = Math.max(maxLight, level.getLightEngine().getRawBrightness(blockPos.offset(i, j, k), 0));
                }
            }
        }
        return maxLight >= 6;
    }

    private static boolean isInDeepdark(Level level) {
        return level.dimension().equals(DimensionsAndBiomes.DEEPDARK);
    }
}
