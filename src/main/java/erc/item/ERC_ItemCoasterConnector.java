package erc.item;

import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import erc.registry.ERCEntities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ERC_ItemCoasterConnector extends ERC_ItemCoaster{

	public ERC_ItemCoasterConnector(Properties properties)
	{
		super(properties);
		CoasterType = 1;
	}
	
	public ERC_EntityCoaster getItemInstance(Level world, Wrap_BlockEntityRail tile, double x, double y, double z)
	{
		return new ERC_EntityCoasterConnector(ERCEntities.COASTERCONNECT.get(), world, tile.getRail(), x, y, z);
	}

	public boolean onItemUse(ItemStack itemStack, Player entityPlayer, Level world, int x, int y, int z, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
		return false;
    }
}
