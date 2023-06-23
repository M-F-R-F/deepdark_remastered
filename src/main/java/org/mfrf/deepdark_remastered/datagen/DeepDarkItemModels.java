package org.mfrf.deepdark_remastered.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.mfrf.deepdark_remastered.DeepdarkRemastered;
import org.mfrf.deepdark_remastered.registry.BlocksAndItems;

public class DeepDarkItemModels extends ItemModelProvider {
    public DeepDarkItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, DeepdarkRemastered.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        withExistingParent(BlocksAndItems.DEEPDARK_TELEPORTER_ITEM.getId().getPath(), modLoc("block/deepdark_portal"));
    }
}
