package org.mfrf.deepdark_remastered.worldgen;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
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
