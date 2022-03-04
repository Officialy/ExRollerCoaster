package erc.item;

import erc.block.tileEntity.TileEntityRailBase;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import erc.core.ERC_Logger;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ERC_ItemSmoothAll extends Item {

	public ERC_ItemSmoothAll(Properties p_41383_) {
		super(p_41383_);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		if(!context.getLevel().isClientSide())
		{
			BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
			if(te instanceof Wrap_BlockEntityRail)
			{
				TileEntityRailBase rail = ((Wrap_BlockEntityRail) te).getRail();
				if(rail == null)return InteractionResult.SUCCESS;
				ERC_Logger.info("start");
				smoothrail(0, rail, (Wrap_BlockEntityRail) te, context.getLevel(), 1);
				smoothrail(0, rail, (Wrap_BlockEntityRail) te, context.getLevel(), -1);
			}
		}
		return super.onItemUseFirst(stack, context);
	}

	private void smoothrail(int num, TileEntityRailBase root, Wrap_BlockEntityRail rail, Level world, int v)
	{
		ERC_Logger.info("num"+num);
		if(num>=100)return;
		if(num<=-100)return;
		if(num != 0 && root == rail)return;
		if(rail == null)return;
		
		rail.Smoothing();
		rail.CalcRailLength();
		rail.syncData();
    	Wrap_BlockEntityRail prev = rail.getPrevRailTileEntity();
    	if(prev!=null)
    	{
    		TileEntityRailBase r = prev.getRail();
    		r.SetNextRailVectors(rail.getRail());
    		r.CalcRailLength();
    		prev.syncData();
    	}
		smoothrail(num+v, root, (v==1?rail.getNextRailTileEntity():rail.getPrevRailTileEntity()), world, v);
	}
}
