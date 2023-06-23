package org.mfrf.deepdark_remastered.teleporter;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.mfrf.deepdark_remastered.registry.BlocksAndItems;
import org.mfrf.deepdark_remastered.registry.DimensionsAndBiomes;

import java.util.Random;
import java.util.function.Function;

public class TeleportDeepDark implements ITeleporter {
    protected final ServerLevel world;
    protected final Random random;
    protected Vec3 destPos;
    protected float destYaw;
    protected float destPitch;

    public TeleportDeepDark(ServerLevel world) {
        this.world = world;
        this.random = new Random(world.getSeed());
    }

    public void setDestPos(double x, double y, double z, float yaw, float pitch) {
        destPos = new Vec3(x, y, z);
        destYaw = yaw;
        destPitch = pitch;
    }


    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(true);
    }

    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return new PortalInfo(destPos, Vec3.ZERO, destYaw, destPitch);
    }

    public boolean placeInPortal(Entity entity, float yaw) {
        this.makePortal(entity);
        return false;
    }

    public boolean makePortal(Entity entityIn) {
        BlockPos blockPos = entityIn.blockPosition();
        double x = (int) Math.floor(blockPos.getX()) + 0.5;
        double z = (int) Math.floor(blockPos.getZ()) + 0.5;
        if (entityIn.getLevel().dimension().equals(DimensionsAndBiomes.DEEPDARK)) {
//        entityIn.getLevel().dimensionTypeId().equals(Level.OVERWORLD);
            int y = entityIn.getBlockY();
            BlockPos center = new BlockPos(x, y - 2, z);
            if (world.getBlockState(center).getBlock() == BlocksAndItems.DEEPDARK_TELEPORTER.get()) {
                return false;
            }
            for (int dx = -3; dx <= 3; dx++)
                for (int dz = -3; dz <= 3; dz++)
                    for (int dy = -7; dy <= 4; dy++) {
                        BlockPos pos = new BlockPos(x + dx, y + dy, z + dz);
                        if (dx == 0 && dy == -1 && dz == 0) {
                            BlockState tpb = world.getBlockState(blockPos);
                            this.world.setBlock(pos, tpb, 2);
                        } else if (dx == -3 || dx == 3 || (dy + Math.max(Math.abs(dx), Math.abs(dz))) <= -1 || dy == 4 || dz == -3 || dz == 3) {
                            this.world.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 2);
                        } else if ((dy + Math.max(Math.abs(dx), Math.abs(dz))) == 0 && (dx == 2 || dx == -2 || dz == 2 || dz == -2)) {
                            this.world.setBlock(pos, Blocks.TORCH.defaultBlockState(), 2);
                        } else {
                            this.world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        }
                    }
//            Direction a = entityIn.getMotionDirection();
            entityIn.lerpMotion(0, 0, 0);
            world.setBlock(center, BlocksAndItems.DEEPDARK_TELEPORTER.get().defaultBlockState(), 2);
            return true;
        }
        return false;
    }
}
