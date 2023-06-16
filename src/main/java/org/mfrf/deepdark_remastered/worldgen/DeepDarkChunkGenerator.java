package org.mfrf.deepdark_remastered.worldgen;

import com.google.errorprone.annotations.InlineMe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DeepDarkChunkGenerator extends ChunkGenerator {

    private static final Codec<Settings> SETTINGS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("sea_level").forGetter(Settings::seaLevel),
                    Codec.INT.fieldOf("celling").forGetter(Settings::celling),
                    Codec.INT.fieldOf("floor").forGetter(Settings::floor)
            ).apply(instance, Settings::new));

    public static final Codec<DeepDarkChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryOps.retrieveRegistry(Registry.STRUCTURE_SET_REGISTRY).forGetter(DeepDarkChunkGenerator::getStructureSetRegistry),
                    RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(DeepDarkChunkGenerator::getBiomeRegistry),
                    SETTINGS_CODEC.fieldOf("settings").forGetter(DeepDarkChunkGenerator::getSettings)
            ).apply(instance, DeepDarkChunkGenerator::new));
    //todo modify settings entry

    private final Settings settings;

    public DeepDarkChunkGenerator(Registry<StructureSet> structureSetRegistry, Registry<Biome> registry, Settings settings) {
        super(structureSetRegistry, Optional.empty(), new DeepDarkBiomeProvider(registry));//todo fixit
        this.settings = settings;
    }

//    private static Optional<HolderSet<StructureSet>> getSet(Registry<StructureSet> structureSetRegistry) {
//        HolderSet.Named<StructureSet> structureSet = structureSetRegistry.getOrCreateTag(TagKey.create(Registry.STRUCTURE_SET_REGISTRY,
//                Constants.DEEP_DARK_STRUCTURE_SET));
//        return Optional.of(structureSet);
//    }

    public Settings getSettings() {
        return settings;
    }

    public Registry<Biome> getBiomeRegistry() {
        return ((DeepDarkBiomeProvider) biomeSource).getBiomeRegistry();
    }

    public Registry<StructureSet> getStructureSetRegistry() {
        return structureSets;
    }

    @Override
    public void buildSurface(WorldGenRegion world, StructureManager structureManager, RandomState randomState, ChunkAccess chunkprimer) {
        int x = chunkprimer.getPos().x;
        int z = chunkprimer.getPos().z;
        int minY = settings.floor;
        int maxY = settings.celling;
        Random random = new Random((x >> 2) * 65535 + (z >> 2));

        int spire_x = ((x >> 2) * 64) + (8 + random.nextInt(48)) - (x * 16);
        int spire_z = ((z >> 2) * 64) + (8 + random.nextInt(48)) - (z * 16);

        random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        GenStates[] values = GenStates.values();
        for (int dx = 0; dx < 16; ++dx) {
            for (int dz = 0; dz < 16; ++dz) {
                int rs = (spire_x - dx) * (spire_x - dx) + (spire_z - dz) * (spire_z - dz);

                double spire_dist = rs < convertHeight(256) ? Math.sqrt(rs) : Double.MAX_VALUE;

                GenStates curState = GenStates.FLOOR_BEDROCK;
                for (int dy = 0; dy < convertHeight(256); dy++) {
                    BlockState state = curState.state;

                    if (curState == GenStates.AIR) {
                        if (rs < convertHeight(256)) {
                            int m = Math.min(dy - minY, maxY - dy);
                            double t = spire_dist;

                            if (m < convertHeight(9)) {
                                t -= Math.sqrt(convertHeight(9) - m);
                            }

                            if (t <= 4 || t <= 5 && random.nextBoolean()) {
                                state = Blocks.COBBLESTONE.defaultBlockState();
                            }
                        }
                    }

                    if (dy >= convertHeight(253)) {
                        state = Blocks.BEDROCK.defaultBlockState();
                    }
                    BlockPos pos = new BlockPos(dx, dy, dz);
                    chunkprimer.setBlockState(pos, state, true);

                    boolean advance;
                    switch (curState) {
                        case FLOOR_BEDROCK:
                            advance = dy > convertHeight(2) || dy > convertHeight(0) && random.nextBoolean();
                            break;
                        case GROUND:
                            advance = dy >= (minY + convertHeight(2)) || dy >= minY && random.nextInt(4) != 0;
                            break;
                        case AIR:
                            advance = dy >= convertHeight(90) && (dy >= maxY || random.nextInt(1 + 2 * (maxY - dy) * (maxY - dy)) == 0);
                            break;
                        case CEILING:
                            advance = dy >= maxY && random.nextInt(40) == 0;
                            break;
                        case CEILING_STONE:
                            advance = dy >= convertHeight(253);

                            break;
                        case CEILING_BEDROCK:
                            advance = false;
                            break;
                        default:
                            throw new RuntimeException("Invalid State " + curState);
                    }
                    if (advance) {
                        curState = values[curState.ordinal() + 1];
                    }
                }
            }
        }
//
//        for (MapGenBase generator : generators) {
//            generator.generate(world, x, z, chunkprimer);
//        }
//
//        for (MapGenBase mapgenbase : this.structureGenerators) {
//            mapgenbase.generate(world, x, z, chunkprimer);
//        }
//
        for (int dx = 0; dx < 16; ++dx) {
            for (int dz = 0; dz < 16; ++dz) {
                for (int dy = minY; dy < minY + 3; dy++) {
                    BlockPos pos = new BlockPos(dx, dy, dz);
                    if (chunkprimer.getBlockState(pos) == Blocks.STONE.defaultBlockState()) {
                        chunkprimer.setBlockState(pos, Blocks.COBBLESTONE.defaultBlockState(), false);
                    }
                }
            }
        }
//
//        ChunkGenerator chunk = new ChunkAccess(new ChunkPos(x,z),world, chunkprimer, x, z);
//
//        biomeSource = world.getBiomeManager().getNoiseBiomeAtPosition(16, x * 16, z * 16);
//        byte[] biomeIDs = chunk.getBiomeArray();
//
//        for (int l = 0; l < biomeIDs.length; ++l) {
//            biomeIDs[l] = (byte) Biome.getIdForBiome(biomes[l]);
//        }
//
//        chunk.generateSkylightMap();
//

    }

    @Override

    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkprimer) {
        int x = chunkprimer.getPos().x;
        int z = chunkprimer.getPos().z;
        int minY = settings.floor;
        int maxY = settings.celling;
        Random random = new Random((x >> 2) * 65535 + (z >> 2));

        int spire_x = ((x >> 2) * 64) + (8 + random.nextInt(48)) - (x * 16);
        int spire_z = ((z >> 2) * 64) + (8 + random.nextInt(48)) - (z * 16);

        random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        GenStates[] values = GenStates.values();
        for (int dx = 0; dx < 16; ++dx) {
            for (int dz = 0; dz < 16; ++dz) {
                int rs = (spire_x - dx) * (spire_x - dx) + (spire_z - dz) * (spire_z - dz);

                double spire_dist = rs < convertHeight(256) ? Math.sqrt(rs) : Double.MAX_VALUE;

                GenStates curState = GenStates.FLOOR_BEDROCK;
                for (int dy = 0; dy < convertHeight(256); dy++) {
                    BlockState state = curState.state;

                    if (curState == GenStates.AIR) {
                        if (rs < convertHeight(256)) {
                            int m = Math.min(dy - minY, maxY - dy);
                            double t = spire_dist;

                            if (m < convertHeight(9)) {
                                t -= Math.sqrt(convertHeight(9) - m);
                            }

                            if (t <= 4 || t <= 5 && random.nextBoolean()) {
                                state = Blocks.COBBLESTONE.defaultBlockState();
                            }
                        }
                    }

                    if (dy >= convertHeight(253)) {
                        state = Blocks.BEDROCK.defaultBlockState();
                    }
                    BlockPos pos = new BlockPos(dx, dy, dz);
                    chunkprimer.setBlockState(pos, state, true);

                    boolean advance;
                    switch (curState) {
                        case FLOOR_BEDROCK:
                            advance = dy > convertHeight(2) || dy > convertHeight(0) && random.nextBoolean();
                            break;
                        case GROUND:
                            advance = dy >= (minY + convertHeight(2)) || dy >= minY && random.nextInt(4) != 0;
                            break;
                        case AIR:
                            advance = dy >= getSeaLevel() && (dy >= maxY || random.nextInt(1 + 2 * (maxY - dy) * (maxY - dy)) == 0);
                            break;
                        case CEILING:
                            advance = dy >= maxY && random.nextInt(40) == 0;
                            break;
                        case CEILING_STONE:
                            advance = dy >= convertHeight(253);

                            break;
                        case CEILING_BEDROCK:
                            advance = false;
                            break;
                        default:
                            throw new RuntimeException("Invalid State " + curState);
                    }
                    if (advance) {
                        curState = values[curState.ordinal() + 1];
                    }
                }
            }
        }
//
//        for (MapGenBase generator : generators) {
//            generator.generate(world, x, z, chunkprimer);
//        }
//
//        for (MapGenBase mapgenbase : this.structureGenerators) {
//            mapgenbase.generate(world, x, z, chunkprimer);
//        }
//
        for (int dx = 0; dx < 16; ++dx) {
            for (int dz = 0; dz < 16; ++dz) {
                for (int dy = minY; dy < minY + 3; dy++) {
                    BlockPos pos = new BlockPos(dx, dy, dz);
                    if (chunkprimer.getBlockState(pos) == Blocks.STONE.defaultBlockState()) {
                        chunkprimer.setBlockState(pos, Blocks.COBBLESTONE.defaultBlockState(), false);
                    }
                }
            }
        }//todo fill stone
//
//        ChunkGenerator chunk = new ChunkAccess(new ChunkPos(x,z),world, chunkprimer, x, z);
//
//        biomeSource = world.getBiomeManager().getNoiseBiomeAtPosition(16, x * 16, z * 16);
//        byte[] biomeIDs = chunk.getBiomeArray();
//
//        for (int l = 0; l < biomeIDs.length; ++l) {
//            biomeIDs[l] = (byte) Biome.getIdForBiome(biomes[l]);
//        }
//
//        chunk.generateSkylightMap();
//
//
        return CompletableFuture.completedFuture(chunkprimer);
    }

    // Make sure this is correctly implemented so that structures and features can use this
    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return settings.seaLevel;
    }

    // Make sure this is correctly implemented so that structures and features can use this
    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        int y = getGenDepth();
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState[] states = new BlockState[y];
        states[0] = Blocks.BEDROCK.defaultBlockState();
        for (int i = 1; i < y; i++) {
            states[i] = stone;
        }
        return new NoiseColumn(levelHeightAccessor.getMinBuildHeight(), states);
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunkAccess, GenerationStep.Carving carving) {
    }

    private int convertHeight(int in) {
        return (int) (getGenDepth() * (in / 256.0) - (settings.seaLevel - settings.floor));
    }


    // This makes sure passive mob spawning works for generated chunks. i.e. mobs that spawn during the creation of chunks themselves
    @Override
    public void spawnOriginalMobs(WorldGenRegion level) {
        ChunkPos chunkpos = level.getCenter();
        Holder<Biome> biome = level.getBiome(chunkpos.getWorldPosition().atY(level.getMaxBuildHeight() - 1));
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        worldgenrandom.setDecorationSeed(level.getSeed(), chunkpos.getMinBlockX(), chunkpos.getMinBlockZ());
        NaturalSpawner.spawnMobsForChunkGeneration(level, biome, chunkpos, worldgenrandom);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos pos) {
    }

    @Override
    public int getMinY() {
        return settings.celling;
    }

    @Override
    public int getGenDepth() {
        return settings.celling - settings.floor;
    }

    @Override
    public int getSeaLevel() {
        return settings.seaLevel;
    }

    private record Settings(int seaLevel, int celling, int floor) {
    }

    enum GenStates {
        FLOOR_BEDROCK(Blocks.BEDROCK),
        GROUND(Blocks.STONE),
        AIR(Blocks.AIR),
        CEILING(Blocks.COBBLESTONE),
        CEILING_STONE(Blocks.STONE),
        CEILING_BEDROCK(Blocks.BEDROCK);

        final BlockState state;

        GenStates(Block state) {
            this.state = state.defaultBlockState();
        }
    }
}
