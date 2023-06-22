package org.mfrf.deepdark_remastered.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.mfrf.deepdark_remastered.registry.Blocks;

public class TileBlockTeleporter extends BlockEntity {
    public static final String REG_NAME = "blockentity_teleporter";

    public TileBlockTeleporter(BlockPos pos, BlockState state){
        super(Blocks.BE_LOCAL_TELEPORTER.get(),pos,state);
    }

}
