package org.mfrf.deepdark_remastered.registry;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.mfrf.deepdark_remastered.Deepdark_remastered;
import org.mfrf.deepdark_remastered.teleporter.TeleportDeepDark;

public class Blocks {
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCK = DeferredRegister.create(ForgeRegistries.BLOCKS.getRegistryName(), Deepdark_remastered.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Deepdark_remastered.MODID);
    public static final RegistryObject<Block> deepdarkPortal = BLOCK.register("deepdark_portal", () -> new Block(BlockBehaviour.Properties.of(Material.STONE).randomTicks()) {

        @Override
        public boolean isRandomlyTicking(BlockState p_49921_) {
            return true;
        }

        @Override
        public void randomTick(BlockState p_222954_, ServerLevel p_222955_, BlockPos p_222956_, RandomSource p_222957_) {
            tick(p_222954_,p_222955_,p_222956_,p_222957_);
        }

        @Override
        public void tick(BlockState p_222945_, ServerLevel p_222946_, BlockPos p_222947_, RandomSource p_222948_) {
            super.tick(p_222945_, p_222946_, p_222947_, p_222948_);
//            if (p_222946_.getGameTime() % 60 == 0) {
                ServerLevel level = p_222946_.getLevel();
                Player player = level.getNearestPlayer(TargetingConditions.DEFAULT, p_222947_.getX(), p_222947_.getY(), p_222947_.getZ());
                if (player != null && player.blockPosition().below().asLong() == p_222947_.asLong()) {
                    TeleportDeepDark tel = new TeleportDeepDark(level);
                    tel.setDestPos(p_222947_.getX(), p_222947_.getY(), p_222947_.getZ(), 0, 0);
                    player.changeDimension(level.getServer().getLevel(DimensionsAndBiomes.DEEPDARK), tel);
                }
//            }
        }
    });

    public static final RegistryObject<Block> deepdarkteleporter = BLOCK.register(BlockTeleporter.REG_NAME, () -> new BlockTeleporter());
    public static final RegistryObject<BlockEntityType<?>> BE_LOCAL_TELEPORTER = BLOCK_ENTITIES.register(TileBlockTeleporter.REG_NAME,
            () -> BlockEntityType.Builder.of(TileBlockTeleporter::new, deepdarkteleporter.get()).build(null));
}
