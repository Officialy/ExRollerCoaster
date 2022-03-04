package erc.item;

import erc.block.BlockRailBase;
import erc.core.ERC_Logger;
import erc.entity.ERC_EntityCoaster;
import erc.manager.ERC_CoasterAndRailManager;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import erc.manager.ERC_ModelLoadManager;
import erc.network.ServerboundSpawnRequestWithCoaster;
import erc.network.ERC_PacketHandler;
import erc.registry.ERCEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ERC_ItemCoaster extends Wrap_ItemCoaster {
    public ERC_ItemCoaster(Properties properties) {
        super(properties);
        CoasterType = 0;
    }

    @Override
    public ERC_EntityCoaster getItemInstance(Level world, Wrap_BlockEntityRail tile, double x, double y, double z) {
        return new ERC_EntityCoaster(ERCEntities.COASTER.get(), world, tile.getRail(), x, y, z);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!BlockRailBase.isBlockRail(context.getLevel().getBlockState(context.getClickedPos()).getBlock())) {
            ERC_Logger.info("not rail");
            return InteractionResult.FAIL;
        }

        if (context.getLevel().isClientSide()) {
            ERC_Logger.info("placing coaster");
            setCoaster(context.getClickedPos(), -1);
            return InteractionResult.SUCCESS;
        }

        context.getPlayer().getMainHandItem().grow(-1);
        return InteractionResult.PASS;
    }

    public void setCoaster(BlockPos pos, int parentID) {
        ERC_CoasterAndRailManager.SetCoasterPos(pos);
        ERC_CoasterAndRailManager.saveModelID = modelCount;
        ServerboundSpawnRequestWithCoaster packet = new ServerboundSpawnRequestWithCoaster(this, modelCount, ERC_ModelLoadManager.getModelOP(modelCount, CoasterType), pos, parentID);
        ERC_PacketHandler.INSTANCE.sendToServer(packet);
    }
}