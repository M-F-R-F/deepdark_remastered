package org.mfrf.deepdark_remastered.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileBlockTeleporter extends BlockEntity {
    public static final String REG_NAME = "blockentity_teleporter";
    private double destX,destY,destZ;
    private double destYaw;
    private double destPitch;

    public TileBlockTeleporter(BlockPos pos, BlockState state){
        super(Blocks.BE_LOCAL_TELEPORTER.get(),pos,state);
    }

    @Override
    public void load(CompoundTag compoundTag){
        super.load(compoundTag);
        if(compoundTag.contains("destX")&&compoundTag.contains("destY")&&compoundTag.contains("destZ")&&compoundTag.contains("destPitch") && compoundTag.contains("destYaw")){
            this.destX = compoundTag.getDouble("destX");
            this.destY = compoundTag.getDouble("destY");
            this.destZ = compoundTag.getDouble("destZ");
            this.destPitch = compoundTag.getDouble("destPitch");
            this.destYaw = compoundTag.getDouble("destYaw");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compound)
    {
        compound.putDouble("destX", this.destX);
        compound.putDouble("destY", this.destY);
        compound.putDouble("destZ", this.destZ);
        compound.putDouble("destPitch", this.destPitch);
        compound.putDouble("destYaw", this.destYaw);
    }

    public void setDestination(double posX, double posY, double posZ, double pitch, double yaw)
    {
        this.destX = posX;
        this.destY = posY;
        this.destZ = posZ;
        this.destPitch = pitch;
        this.destYaw = yaw;
    }

    public BlockPos getDestination()
    {
        return new BlockPos(destX, destY, destZ);
    }

    public double getPitch()
    {
        return destPitch;
    }

    public double getYaw()
    {
        return destYaw;
    }
}
