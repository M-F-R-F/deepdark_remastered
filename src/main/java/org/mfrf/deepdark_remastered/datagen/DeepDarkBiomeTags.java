package org.mfrf.deepdark_remastered.datagen;

import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.mfrf.deepdark_remastered.DeepdarkRemastered;

public class DeepDarkBiomeTags extends TagsProvider<Biome> {
    public DeepDarkBiomeTags(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, BuiltinRegistries.BIOME, DeepdarkRemastered.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {

    }
}
