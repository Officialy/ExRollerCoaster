package erc.item;

import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterMonodentate;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import erc.registry.ERCEntities;
import net.minecraft.world.level.Level;

public class ERC_ItemCoasterMonodentate extends ERC_ItemCoaster{

	public ERC_ItemCoasterMonodentate(Properties properties)
	{
		super(properties);
		CoasterType = 2;
	}

}