package org.mfrf.deepdark_remastered.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.mfrf.deepdark_remastered.Deepdark_remastered;
import org.mfrf.deepdark_remastered.worldgen.DeepDarkBiomeProvider;
import org.mfrf.deepdark_remastered.worldgen.DeepDarkChunkGenerator;

public class Dimensions {
    public static final ResourceKey<Level> DEEPDARK = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(Deepdark_remastered.MODID, "deepdark"));

    public static void register() {
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(Deepdark_remastered.MODID, "deepdark_chunkgen"),
                DeepDarkChunkGenerator.CODEC);
        Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(Deepdark_remastered.MODID, "biomes"),
                DeepDarkBiomeProvider.CODEC);
    }
}
