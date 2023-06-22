package org.mfrf.deepdark_remastered.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.mfrf.deepdark_remastered.DeepdarkRemastered;
import org.mfrf.deepdark_remastered.block.BlockTeleporter;
import org.mfrf.deepdark_remastered.block.TileBlockTeleporter;

public class BlocksAndItems {
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCK = DeferredRegister.create(ForgeRegistries.BLOCKS.getRegistryName(), DeepdarkRemastered.MODID);
    public static final DeferredRegister<Item> ITEM = DeferredRegister.create(ForgeRegistries.ITEMS.getRegistryName(), DeepdarkRemastered.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DeepdarkRemastered.MODID);

    public static final RegistryObject<Block> DEEPDARK_TELEPORTER = BLOCK.register(BlockTeleporter.REG_NAME, BlockTeleporter::new);
    public static final RegistryObject<BlockItem> DEEPDARK_TELEPORTER_ITEM = ITEM.register(BlockTeleporter.REG_NAME, () -> new BlockItem(DEEPDARK_TELEPORTER.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static final RegistryObject<BlockEntityType<?>> BE_LOCAL_TELEPORTER = BLOCK_ENTITIES.register(TileBlockTeleporter.REG_NAME,
            () -> BlockEntityType.Builder.of(TileBlockTeleporter::new, DEEPDARK_TELEPORTER.get()).build(null));
}
