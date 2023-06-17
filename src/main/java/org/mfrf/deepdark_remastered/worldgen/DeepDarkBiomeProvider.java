package org.mfrf.deepdark_remastered.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import org.mfrf.deepdark_remastered.registry.DimensionsAndBiomes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeepDarkBiomeProvider extends BiomeSource {
    public static final Codec<DeepDarkBiomeProvider> CODEC = RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY)
            .xmap(DeepDarkBiomeProvider::new, DeepDarkBiomeProvider::getBiomeRegistry).codec();

    private final Holder<Biome> biome;
    private final Registry<Biome> biomeRegistry;
    private static final List<ResourceKey<Biome>> SPAWN = Collections.singletonList(Biomes.PLAINS);

    public DeepDarkBiomeProvider(Registry<Biome> biomeRegistry) {
        super(getStartBiomes(biomeRegistry));
        this.biomeRegistry = biomeRegistry;
        biome = biomeRegistry.getHolderOrThrow(DimensionsAndBiomes.DEEPDARK_LEVEL_BIOME);
    }

    private static List<Holder<Biome>> getStartBiomes(Registry<Biome> registry) {
        return SPAWN.stream().map(s -> registry.getHolderOrThrow(ResourceKey.create(BuiltinRegistries.BIOME.key(), s.location()))).collect(Collectors.toList());
    }

    public Registry<Biome> getBiomeRegistry() {
        return biomeRegistry;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        return biome;
    }
}
