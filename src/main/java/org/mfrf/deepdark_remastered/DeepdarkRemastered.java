package org.mfrf.deepdark_remastered;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.mfrf.deepdark_remastered.registry.BlocksAndItems;
import org.mfrf.deepdark_remastered.registry.DimensionsAndBiomes;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DeepdarkRemastered.MODID)
public class DeepdarkRemastered {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "deepdark_remastered";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // Create a Deferred Register to hold Blocks which will all be registered under the "deepdark_remastered" namespace
    public DeepdarkRemastered() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        BlocksAndItems.BLOCK.register(modEventBus);
        BlocksAndItems.ITEM.register(modEventBus);
        BlocksAndItems.BLOCK_ENTITIES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        DimensionsAndBiomes.register();
    }

}
