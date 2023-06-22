package org.mfrf.deepdark_remastered.registry;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.mfrf.deepdark_remastered.DeepdarkRemastered;
import org.mfrf.deepdark_remastered.datagen.DeepDarkBiomeTags;

@Mod.EventBusSubscriber(modid = DeepdarkRemastered.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new DeepDarkBiomeTags(generator, event.getExistingFileHelper()));
    }
}
