package org.mfrf.deepdark_remastered.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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

        Random random = new Random(randomState.legacyLevelSeed() + (x >> 2) * 65535L + (z >> 2));

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
        int l = settings.convertBackByPercentWithOutTopOrBottomLayerWithAbs(GenStates.GROUND.condition.range) + 4;
        for (int dx = 0; dx < 16; ++dx) {
            outer:
            for (int dz = 0; dz < 16; ++dz) {
                for (int dy = l; dy > l - 16; dy--) {
                    BlockPos pos = new BlockPos(dx, dy, dz);
                    if (chunkprimer.getBlockState(pos).is(BlockTags.BASE_STONE_OVERWORLD) && random.nextBoolean()) {
                        chunkprimer.setBlockState(pos.above(), Blocks.COBBLESTONE.defaultBlockState(), false);
                        continue outer;
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
        BiomeManager biomemanager = biomeManager.withDifferentSource((p_224281_, p_224282_, p_224283_) -> {
            return this.biomeSource.getNoiseBiome(p_224281_, p_224282_, p_224283_, randomState.sampler());
        });
        WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        int i = 8;
        ChunkPos chunkpos = chunkAccess.getPos();
        NoiseChunk noisechunk = chunkAccess.getOrCreateNoiseChunk((p_224250_) -> {
            return this.biomeSource.createNoiseChunk(p_224250_, p_224228_, Blender.of(p_224224_), randomState);
        });
        Aquifer aquifer = noisechunk.aquifer();
        CarvingContext carvingcontext = new CarvingContext(this, p_224224_.registryAccess(), chunkAccess.getHeightAccessorForGeneration(), noisechunk, randomState, this.settings.value().surfaceRule());
        CarvingMask carvingmask = ((ProtoChunk)chunkAccess).getOrCreateCarvingMask(p_224230_);

        for(int j = -8; j <= 8; ++j) {
            for(int k = -8; k <= 8; ++k) {
                ChunkPos chunkpos1 = new ChunkPos(chunkpos.x + j, chunkpos.z + k);
                ChunkAccess chunkaccess = p_224224_.getChunk(chunkpos1.x, chunkpos1.z);
                BiomeGenerationSettings biomegenerationsettings = chunkaccess.carverBiome(() -> {
                    return this.getBiomeGenerationSettings(this.biomeSource.getNoiseBiome(QuartPos.fromBlock(chunkpos1.getMinBlockX()), 0, QuartPos.fromBlock(chunkpos1.getMinBlockZ()), randomState.sampler()));
                });
                Iterable<Holder<ConfiguredWorldCarver<?>>> iterable = biomegenerationsettings.getCarvers(p_224230_);
                int l = 0;

                for(Holder<ConfiguredWorldCarver<?>> holder : iterable) {
                    ConfiguredWorldCarver<?> configuredworldcarver = holder.value();
                    worldgenrandom.setLargeFeatureSeed(p_224225_ + (long)l, chunkpos1.x, chunkpos1.z);
                    if (configuredworldcarver.isStartChunk(worldgenrandom)) {
                        configuredworldcarver.carve(carvingcontext, chunkAccess, biomemanager::getBiome, worldgenrandom, aquifer, chunkpos1, carvingmask);
                    }

                    ++l;
                }
            }
        }

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

        private int convertBackByPercentWithOutTopOrBottomLayerWithAbs(float percent) {
            int total = this.topBedrockLayer - this.bottomBedrockLayer;
            return Math.abs((int) (percent * total + bottomBedrockLayer));
        }

        private boolean determineGenStates(GenStates current, int currentHeight, Random random) {
            float percent = convertHeightByPercentWithOutTopOrBottomLayer(currentHeight);
            boolean b = switch (current) {
                case FLOOR_BEDROCK -> {
                    yield currentHeight - bottomBedrockLayer > 5 || ((currentHeight != bottomBedrockLayer && random.nextBoolean()));
                }
                case GROUND_DEEPSLATE -> {
                    yield current.condition.test(random, percent, () -> 8);
                }
                case GROUND, CEILING, CEILING_STONE, AIR
//                        , GROUND_COBBLESTONE
                        -> {
                    yield current.condition.test(random, percent, () -> (int) (1 + convertBackByPercentWithOutTopOrBottomLayerWithAbs(current.condition.range - percent)) / 16);
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
        GROUND(Blocks.STONE, new condition(0.59f, 0.0001f)),
//        GROUND_COBBLESTONE(Blocks.COBBLESTONE, new condition(0.6f, 0.001f)),
        AIR(Blocks.AIR, new condition(0.78f, 0.1f)),
        CEILING(Blocks.COBBLESTONE, new condition(0.8f, 0.005f)),
        CEILING_STONE(Blocks.STONE, new condition(0.95f, 0.005f)),

        CEILING_BASALT(Blocks.DEEPSLATE, null),
        CEILING_BEDROCK(Blocks.BEDROCK, null);

        final BlockState state;
        private final GenStates.condition condition;

        GenStates(Block state, condition condition) {
            this.state = state.defaultBlockState();
            this.condition = condition;
        }

        record condition(float range, float error) {
            boolean test(Random random, float currentHeight, Supplier<Integer> chance) {
                return ((Math.abs(currentHeight - range) < error && random.nextInt(chance.get()) == 0)) || currentHeight > range;
            }
        }
    }
}
