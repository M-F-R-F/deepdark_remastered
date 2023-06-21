package org.mfrf.deepdark_remastered.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import org.mfrf.deepdark_remastered.teleporter.TeleportDeepDark;

import javax.annotation.Nullable;

public class BlockTeleporter extends BaseEntityBlock {
    public static String REG_NAME = "deepdark_portal";

    public BlockTeleporter(){
        super(BlockBehaviour.Properties.of(Material.STONE).randomTicks());
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world,BlockPos pos,BlockState stae){
        return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    public boolean useShapeForLightOcclusion(BlockState state){
        return true;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity){
        if(world.isClientSide)return;
        if(!(entity instanceof ServerPlayer))return;

        if(!entity.isPassenger() && !entity.isVehicle() && entity.canChangeDimensions()){
            BlockEntity tile = world.getBlockEntity(pos);
            if(tile != null && tile instanceof TileBlockTeleporter){
                TileBlockTeleporter te = (TileBlockTeleporter) world.getBlockEntity(pos);
                BlockPos destination = te.getDestination();
                float warpX = destination.getX();
                float warpY = destination.getY();
                float warpZ = destination.getZ();
                float newPitch = (float) te.getPitch();
                float newYaw = (float) te.getYaw();
                actuallyPerformTeleport((ServerPlayer) entity, world.getServer().getLevel(entity.getCommandSenderWorld().dimension()), warpX, warpY, warpZ, newYaw, newPitch);
            }
        }
    }

    protected Entity actuallyPerformTeleport(ServerPlayer player, ServerLevel dim, double x, double y, double z, float destYaw, float destPitch){
        TeleportDeepDark tele = new TeleportDeepDark(dim);
        tele.setDestPos(x,y,z,destYaw,destPitch);
        player.changeDimension(dim,tele);
        return player;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos,BlockState state){
        return new TileBlockTeleporter(pos,state);
    }
}
