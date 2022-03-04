package erc.registry;

import erc.core.ExRollerCoaster;
import erc.block.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ERCBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExRollerCoaster.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExRollerCoaster.MODID);

    public static final RegistryObject<Block> RAIL_NORMAL = register("railnormal", () -> new BlockRailNormal(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 2.0F)));
    public static final RegistryObject<Block> RAIL_BRANCH = register("railbranch", () -> new BlockRailBranch(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 2.0F)));
    public static final RegistryObject<Block> RAIL_CONST = register("railconst", () -> new BlockRailConstVelocity(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 2.0F)));
    public static final RegistryObject<Block> RAIL_DETECTOR = register("raildetector", () -> new BlockRailDetector(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 2.0F)));
    public static final RegistryObject<Block> RAIL_DRIFT = register("raildrift", () -> new BlockDriftRail(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 2.0F)));
    public static final RegistryObject<Block> RAIL_INVISIBLE = register("railinvisible", () -> new BlockRailInvisible(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 2.0F)));
    public static final RegistryObject<Block> RAIL_NONGRAVITY = register("railnogravity", () -> new BlockNonGravityRail(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 2.0F)));
    public static final RegistryObject<Block> RAIL_REDACCEL = register("railaccelerator", () -> new BlockRailRedstoneAccelerator(BlockBehaviour.Properties.of(Material.WOOD).strength(2.0F, 2.0F)));

    private static <BLOCK extends Block> RegistryObject<BLOCK> register(final String name, final Supplier<BLOCK> blockFactory) {
        return registerBlock(name, blockFactory, block -> new BlockItem(block, defaultItemProperties()));
    }

    private static <BLOCK extends Block> RegistryObject<BLOCK> registerBlock(final String name, final Supplier<BLOCK> blockFactory, final IBlockItemFactory<BLOCK> itemFactory) {
        final RegistryObject<BLOCK> block = BLOCKS.register(name, blockFactory);

        ITEMS.register(name, () -> itemFactory.create(block.get()));

        return block;
    }

    private static Item.Properties defaultItemProperties() {
        return new Item.Properties().tab(ExRollerCoaster.ERC_Tab);
    }

    @FunctionalInterface
    private interface IBlockItemFactory<BLOCK extends Block> {
        Item create(BLOCK block);
    }
}
