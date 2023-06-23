package org.mfrf.deepdark_remastered.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mfrf.deepdark_remastered.DeepdarkRemastered;

@Mod.EventBusSubscriber(modid = DeepdarkRemastered.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeClient(), new DeepDarkBlockStates(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new DeepDarkItemModels(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new DeepDarkLangCN(generator));
        generator.addProvider(event.includeClient(), new DeepDarkLangEN(generator));
    }
}
