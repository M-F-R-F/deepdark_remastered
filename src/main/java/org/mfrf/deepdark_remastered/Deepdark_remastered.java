package org.mfrf.deepdark_remastered;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.mfrf.deepdark_remastered.registry.Blocks;
import org.mfrf.deepdark_remastered.registry.DimensionsAndBiomes;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Deepdark_remastered.MODID)
public class Deepdark_remastered {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "deepdark_remastered";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // Create a Deferred Register to hold Blocks which will all be registered under the "deepdark_remastered" namespace
    public Deepdark_remastered() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        Blocks.BLOCK.register(modEventBus);
        Blocks.BLOCK_ENTITIES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        DimensionsAndBiomes.register();
    }

}
