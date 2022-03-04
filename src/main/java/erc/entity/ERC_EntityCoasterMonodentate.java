package erc.entity;

import erc.block.tileEntity.TileEntityRailBase;
import erc.block.tileEntity.TileEntityRailBranch2;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import erc.network.ERC_MessageCoasterCtS;
import erc.network.ERC_PacketHandler;
import erc.registry.ERCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ERC_EntityCoasterMonodentate extends ERC_EntityCoaster{

	public ERC_EntityCoasterMonodentate(EntityType<? extends ERC_EntityCoaster> type, Level world)
	{
		super(type, world);
	}
	
	public ERC_EntityCoasterMonodentate(EntityType<? extends ERC_EntityCoaster> type, Level world, TileEntityRailBase tile, double x, double y, double z) {
		super(type, world, tile, x, y, z);
	}
	
	protected boolean canConnectForrowingCoaster()
	{
		return false;
	}
	
	public Item getItem()
    {
    	return ERCItems.COASTERMONO_ITEM.get();
    }

	@Override
	public void setParamFromPacket(float t, double speed, BlockPos pos)
    {
    	if(this.getControllingPassenger() instanceof Player)
    	{
    		if(tlrail==null)
    		{
    			if(checkTileEntity())
				{
    				killCoaster();
					return;
				}
    		}
	    	// send packet to server
			ERC_MessageCoasterCtS packet = new ERC_MessageCoasterCtS(getId(), this.paramT, this.Speed, tlrail.getPos());
		    ERC_PacketHandler.INSTANCE.sendToServer(packet);
    	}
    	else
    	{
    		Wrap_BlockEntityRail rail = (Wrap_BlockEntityRail)level.getBlockEntity(pos);
    		if(rail == null)return;
    		if(rail instanceof TileEntityRailBranch2)return;
    		
    		this.setParamT(t);
    		this.Speed = speed;
    		this.setRail( rail.getRail() );
//    		if(tlrail==null)
//    		{
//    			if(checkTileEntity())
//				{
//    				killCoaster();
//					return;
//				}
//    		}
    	}
		
    }
}
