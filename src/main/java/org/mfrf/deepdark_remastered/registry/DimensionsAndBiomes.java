package org.mfrf.deepdark_remastered.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.mfrf.deepdark_remastered.DeepdarkRemastered;
import org.mfrf.deepdark_remastered.worldgen.DeepDarkBiome;
import org.mfrf.deepdark_remastered.worldgen.DeepDarkChunkGenerator;

public class DimensionsAndBiomes {
    public static final ResourceKey<Level> DEEPDARK = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(DeepdarkRemastered.MODID, "deepdark"));
    public static final ResourceKey<Biome> DEEPDARK_LEVEL_BIOME = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(DeepdarkRemastered.MODID, "deepdark_level_biome"));

    public static void register() {
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(DeepdarkRemastered.MODID, "deepdark_chunkgen"),
                DeepDarkChunkGenerator.CODEC);
    }
}
