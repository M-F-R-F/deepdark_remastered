package org.mfrf.deepdark_remastered.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import org.mfrf.deepdark_remastered.DeepdarkRemastered;
import org.mfrf.deepdark_remastered.registry.BlocksAndItems;

public class DeepDarkLangEN extends LanguageProvider {
    public DeepDarkLangEN(DataGenerator gen) {
        super(gen, DeepdarkRemastered.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(BlocksAndItems.DEEPDARK_TELEPORTER.get(), "Deep Dark Teleporter");
    }
}
