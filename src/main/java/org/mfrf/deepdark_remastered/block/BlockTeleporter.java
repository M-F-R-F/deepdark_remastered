package org.mfrf.deepdark_remastered.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import org.mfrf.deepdark_remastered.registry.BlocksAndItems;
import org.mfrf.deepdark_remastered.registry.DimensionsAndBiomes;
import org.mfrf.deepdark_remastered.teleporter.TeleportDeepDark;

import javax.annotation.Nullable;

public class BlockTeleporter extends BaseEntityBlock {
    public static String REG_NAME = "deepdark_portal";

    public BlockTeleporter() {
        super(BlockBehaviour.Properties.of(Material.STONE).randomTicks());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState stae) {
        return ItemStack.EMPTY;
    }

    @Deprecated
    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return createTickerHelper(p_153214_, BlocksAndItems.BE_LOCAL_TELEPORTER.get(), (level, pos, state, tile) -> {
            Player player = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 4, null);
            if (!world.isClientSide && player != null && player.isShiftKeyDown() && player.blockPosition().below().equals(pos)) {
                float warpX = pos.getX();
                float warpZ = pos.getZ();
                ServerLevel dest;
                float warpY;
                if (world.dimension().equals(Level.OVERWORLD)) {
                    dest = world.getServer().getLevel(DimensionsAndBiomes.DEEPDARK);
                    warpY = 100;
                } else {
                    dest = world.getServer().getLevel(Level.OVERWORLD);
                    warpY = dest.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) warpX, (int) warpZ);
                }
                actuallyPerformTeleport(player, dest, warpX, warpY, warpZ, 0, 0);
            }
        });
    }

    protected Entity actuallyPerformTeleport(Player player, ServerLevel dim, double x, double y, double z, float destYaw, float destPitch) {
        TeleportDeepDark tele = new TeleportDeepDark(dim);
        tele.setDestPos(x, y, z, destYaw, destPitch);
        player.changeDimension(dim, tele);
        tele.makePortal(player);
        player.getCooldowns().addCooldown(Items.ENDER_PEARL, 10*20);
        return player;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileBlockTeleporter(pos, state);
    }
}
