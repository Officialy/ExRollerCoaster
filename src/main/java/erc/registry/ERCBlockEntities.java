package erc.registry;

import erc.core.ExRollerCoaster;
import erc.block.tileEntity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ERCBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ExRollerCoaster.MODID);

    public static final RegistryObject<BlockEntityType<TileEntityRailNormal>> NORMAL_RAIL = BLOCK_ENTITIES.register("rail", () ->
            BlockEntityType.Builder.of(TileEntityRailNormal::new, ERCBlocks.RAIL_NORMAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRailRedstoneAccelerator>> ACCELERATOR_RAIL = BLOCK_ENTITIES.register("railredacc", () ->
            BlockEntityType.Builder.of(TileEntityRailRedstoneAccelerator::new, ERCBlocks.RAIL_REDACCEL.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRailConstVelocity>> RAILCONSTVEL = BLOCK_ENTITIES.register("railconstvel", () ->
            BlockEntityType.Builder.of(TileEntityRailConstVelocity::new, ERCBlocks.RAIL_CONST.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRailDetector>> RAILDETECTOR = BLOCK_ENTITIES.register("raildetector", () ->
            BlockEntityType.Builder.of(TileEntityRailDetector::new, ERCBlocks.RAIL_DETECTOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRailDrift>> RAILDRIFT = BLOCK_ENTITIES.register("raildrift", () ->
            BlockEntityType.Builder.of(TileEntityRailDrift::new, ERCBlocks.RAIL_DRIFT.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRailBranch2>> RAILBRANCH = BLOCK_ENTITIES.register("railbranch", () ->
            BlockEntityType.Builder.of(TileEntityRailBranch2::new, ERCBlocks.RAIL_BRANCH.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityRailInvisible>> INVISIBLE = BLOCK_ENTITIES.register("invisible", () ->
            BlockEntityType.Builder.of(TileEntityRailInvisible::new, ERCBlocks.RAIL_INVISIBLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<TileEntityNonGravityRail>> NONGRAVITY = BLOCK_ENTITIES.register("nongravity", () ->
            BlockEntityType.Builder.of(TileEntityNonGravityRail::new, ERCBlocks.RAIL_NONGRAVITY.get()).build(null));

}