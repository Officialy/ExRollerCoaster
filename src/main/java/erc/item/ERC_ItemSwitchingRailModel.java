package erc.item;

import erc.core.ERC_Logger;
import erc.gui.RailScreen;
import erc.network.ERC_MessageRailGUICtS;
import erc.network.ERC_PacketHandler;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import erc.block.BlockRailBase;
import erc.manager.ERC_ModelLoadManager;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class ERC_ItemSwitchingRailModel extends Item {

    private int modelCount = 0;

    public ERC_ItemSwitchingRailModel(Properties p_41383_) {
        super(p_41383_);
    }

    public int getModelCount() {
        return modelCount;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (context.getLevel().isClientSide()) {
            if (!BlockRailBase.isBlockRail(context.getLevel().getBlockState(context.getClickedPos()).getBlock()))
                return InteractionResult.FAIL;

            // Wrap_TileEntityRailOK
            Wrap_BlockEntityRail tile = (Wrap_BlockEntityRail) context.getLevel().getBlockEntity(context.getClickedPos());

            tile.changeRailModelRenderer(modelCount);

	    	ERC_MessageRailGUICtS packet = new ERC_MessageRailGUICtS(context.getClickedPos(), RailScreen.editFlag.RailModelIndex.ordinal(), modelCount);
	    	ERC_PacketHandler.INSTANCE.sendToServer(packet);
        }
        return InteractionResult.SUCCESS;
    }

    public void ScrollMouseHweel(double dhweel) {
    	ERC_Logger.info("wrap_itemcoaster : dhweel:"+dhweel);
        modelCount += dhweel > 0 ? 1 : -1;
        if (modelCount >= ERC_ModelLoadManager.getRailPackNum() + 1) modelCount = 0;
        else if (modelCount < 0) modelCount = ERC_ModelLoadManager.getRailPackNum();
    	ERC_Logger.info("modelcount:"+modelCount);
    }
}
