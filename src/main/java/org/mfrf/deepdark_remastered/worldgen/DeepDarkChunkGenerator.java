package org.mfrf.deepdark_remastered.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
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
import net.minecraftforge.common.util.TriPredicate;
import org.apache.commons.lang3.Range;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class DeepDarkChunkGenerator extends ChunkGenerator {

    private static final Codec<Settings> SETTINGS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("sea_level").forGetter(Settings::seaLevel),
                    Codec.INT.fieldOf("top_bedrock_layer").forGetter(Settings::topBedrockLayer),
                    Codec.INT.fieldOf("bottom_bedrock_layer").forGetter(Settings::bottomBedrockLayer)
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

    }

    @Override

    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkprimer) {
        int x = chunkprimer.getPos().x;
        int z = chunkprimer.getPos().z;
        int seaLevel = settings.seaLevel;
        int maxY = settings.topBedrockLayer;
//        int cellingHeight = convertHeightBy256(249);

        Random random = new Random((x >> 2) * 65535 + (z >> 2));

        int spire_x = ((x >> 2) * 64) + (8 + random.nextInt(48)) - (x * 16);
        int spire_z = ((z >> 2) * 64) + (8 + random.nextInt(48)) - (z * 16);

        random.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
        GenStates[] values = GenStates.values();
        for (int dx = 0; dx < 16; ++dx) {
            for (int dz = 0; dz < 16; ++dz) {
                int rs = (spire_x - dx) * (spire_x - dx) + (spire_z - dz) * (spire_z - dz);

                double spire_dist = rs < convertHeightBy256(256) ? Math.sqrt(rs) : Double.MAX_VALUE;

                GenStates curState = GenStates.FLOOR_BEDROCK;
                for (int dy = getMinY(); dy < convertHeightBy256(256); dy++) {
                    BlockState state = curState.state;

                    if (curState == GenStates.AIR) {
                        if (rs < convertHeightBy256(256)) {
                            int m = Math.min(dy - seaLevel, maxY - dy);
                            double t = spire_dist;

                            if (m < convertHeightBy256(9)) {
                                t -= Math.sqrt(convertHeightBy256(9) - m);
                            }

                            if (t <= 4 || t <= 5 && random.nextBoolean()) {
                                state = Blocks.COBBLESTONE.defaultBlockState();
                            }
                        }
                    }

                    if (dy >= settings.topBedrockLayer - 2) {
                        state = Blocks.BEDROCK.defaultBlockState();
                    }
                    BlockPos pos = new BlockPos(dx, dy, dz);
                    chunkprimer.setBlockState(pos, state, true);

//                    boolean advance = switch (curState) {
////                        case FLOOR_BEDROCK -> dy > getMinY() + 5 || (dy > 3 && random.nextBoolean());
////                        case GROUND -> Math.abs(dy - seaLevel) < 8 && random.nextInt(1 + (seaLevel - dy) * (seaLevel - dy)) == 0;
////                        case AIR ->
////                                dy >= cellingHeight - 16 && random.nextInt(1 + 2 * (cellingHeight - dy) * (cellingHeight - dy)) == 0;
////                        case CEILING -> dy >= convertHeight(250) && random.nextInt(10) == 0;
////                        case CEILING_STONE ->
////                                dy >= settings.topBedrockLayer || (dy > settings.topBedrockLayer - 5 && random.nextBoolean());
////                        case CEILING_BEDROCK -> false;
////                        default -> throw new RuntimeException("Invalid State " + curState);
//
//                        case CEILING_BEDROCK -> false;
//                        case CELLING_BASALT -> ;
//                        case CEILING_STONE -> ;
//                        case CEILING -> ;
//                        case AIR -> ;
//                        case GROUND -> ;
//                        case GROUND_DEEPSLATE ->
//                        case FLOOR_BEDROCK -> dy == getMinY() || (dy - getMinY() <= 5 && random.nextBoolean());
//                        default -> throw new RuntimeException("Invalid State " + curState);
//                    };
                    if (settings.determineGenStates(curState, dy, random)) {
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
                for (int dy = getSeaLevel() - 16; dy < seaLevel + 16; dy++) {
                    BlockPos pos = new BlockPos(dx, dy, dz);
                    if (chunkprimer.getBlockState(pos) == Blocks.STONE.defaultBlockState()) {
                        chunkprimer.setBlockState(pos, Blocks.COBBLESTONE.defaultBlockState(), false);
                    }
                }
            }
        }//todo fill stone

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
        int y = getGroundHeight();
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

    private int convertHeightBy256(int in) {
        return (int) (getGenDepth() * (in / 256.0) - getGroundHeight());
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
        return settings.bottomBedrockLayer;
    }

    @Override
    public int getGenDepth() {
        return settings.topBedrockLayer - settings.bottomBedrockLayer;
    }

    @Override
    public int getSeaLevel() {
        return settings.seaLevel;
    }

    public int getGroundHeight() {
        return getSeaLevel() - getMinY();
    }

    private record Settings(int seaLevel, int topBedrockLayer, int bottomBedrockLayer) {
        private float convertHeightByPercentWithOutTopOrBottomLayer(int current) {
            int total = this.topBedrockLayer - this.bottomBedrockLayer;
            return (current - bottomBedrockLayer) / (float) total;
        }

        private boolean determineGenStates(GenStates current, int currentHeight, Random random) {
            float percent = convertHeightByPercentWithOutTopOrBottomLayer(currentHeight);
            boolean b = switch (current) {
                case FLOOR_BEDROCK -> {
                    yield currentHeight - bottomBedrockLayer > 5 || ((currentHeight != bottomBedrockLayer && random.nextBoolean()));
                }
                case GROUND_DEEPSLATE -> {
                    yield current.condition.test(random, percent, () -> 20);
                }
                case GROUND, CEILING, CEILING_STONE -> {
                    yield current.condition.test(random, percent, () -> 1 + (seaLevel - currentHeight) * (seaLevel - currentHeight));
                }
                case AIR -> {
                    yield current.condition.test(random, percent, () -> 1 + 2 * (seaLevel - currentHeight) * (seaLevel - currentHeight));
                }
                case CEILING_BASALT -> {
                    yield currentHeight == topBedrockLayer || (topBedrockLayer - currentHeight <= 5 && random.nextBoolean());
                }
                case CEILING_BEDROCK -> {
                    yield false;
                }
            };

            return b;
        }
    }

    /*
    CELLING_BEDROCK:5 block
    celling_basalt: 15% -> 85%-100%
    celling_stone: 15% -> 70%-85%
    celling: 2% -> 68%-70%
    air: 8% -> 60%-68%
    ground: 30% -> 30%-60%
    ground_deepslate: 30% -> 0%-30%
    floor_BEDROCK: 5 block
     */
    enum GenStates {
        FLOOR_BEDROCK(Blocks.BEDROCK, null),
        GROUND_DEEPSLATE(Blocks.DEEPSLATE, new condition(0.3f, 0.005f)),
        GROUND(Blocks.STONE, new condition(0.6f, 0.005f)),
        AIR(Blocks.AIR, new condition(0.68f, 0.008f)),
        CEILING(Blocks.COBBLESTONE, new condition(0.7f, 0.005f)),
        CEILING_STONE(Blocks.STONE, new condition(0.85f, 0.005f)),

        CEILING_BASALT(Blocks.BASALT, null),
        CEILING_BEDROCK(Blocks.BEDROCK, null);

        final BlockState state;
        private final GenStates.condition condition;

        GenStates(Block state, condition condition) {
            this.state = state.defaultBlockState();
            this.condition = condition;
        }

        record condition(float range, float error) {
            boolean test(Random random, float currentHeight, Supplier<Integer> chance) {
                return currentHeight > range && ((Math.abs(currentHeight - range) <= error && random.nextInt(chance.get()) == 0));
            }
        }
    }
}
