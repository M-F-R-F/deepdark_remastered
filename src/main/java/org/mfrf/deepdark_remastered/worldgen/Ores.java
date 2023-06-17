package org.mfrf.deepdark_remastered.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Ores {

    @NotNull
    public static Set<PlacedFeature> modifyDeepDarkOreGen() {
        return ForgeRegistries.BIOMES.getValues().stream()
                .flatMap(biome ->
                        biome.getGenerationSettings()
                                .features()
                                .stream()
                                .flatMap(holders ->
                                        holders.stream()
                                                .map(Holder::get)
                                                .filter(placedFeature ->
                                                        placedFeature
                                                                .getFeatures()
                                                                .anyMatch(configuredFeature -> configuredFeature.config() instanceof OreConfiguration))
                                )
                ).collect(Collectors.toSet());
        //todo modify it
    }

    private static <C extends FeatureConfiguration, F extends Feature<C>> PlacedFeature createPlacedFeature(ConfiguredFeature<C, F> feature, PlacementModifier... placementModifiers) {
        return new PlacedFeature(Holder.hackyErase(Holder.direct(feature)), List.copyOf(List.of(placementModifiers)));
    }
}
