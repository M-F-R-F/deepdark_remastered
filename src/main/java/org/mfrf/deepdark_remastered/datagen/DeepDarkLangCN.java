package org.mfrf.deepdark_remastered.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import org.mfrf.deepdark_remastered.DeepdarkRemastered;
import org.mfrf.deepdark_remastered.registry.BlocksAndItems;
import org.spongepowered.asm.mixin.injection.modify.LocalVariableDiscriminator;

import java.util.Locale;

public class DeepDarkLangCN extends LanguageProvider {
    public DeepDarkLangCN(DataGenerator gen) {
        super(gen, DeepdarkRemastered.MODID,"zh_cn");
    }

    @Override
    protected void addTranslations() {
        add(BlocksAndItems.DEEPDARK_TELEPORTER.get(), "漆黑传送门");
    }
}
