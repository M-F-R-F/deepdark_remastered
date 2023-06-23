package org.mfrf.deepdark_remastered.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.mfrf.deepdark_remastered.DeepdarkRemastered;
import org.mfrf.deepdark_remastered.registry.BlocksAndItems;

public class DeepDarkBlockStates extends BlockStateProvider {
    public DeepDarkBlockStates(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DeepdarkRemastered.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(BlocksAndItems.DEEPDARK_TELEPORTER.get());
    }
}
