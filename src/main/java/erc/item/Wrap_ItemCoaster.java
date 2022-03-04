package erc.item;

import net.minecraft.world.item.Item;
import erc.entity.ERC_EntityCoaster;
import erc.manager.ERC_ModelLoadManager;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import net.minecraft.world.level.Level;

public abstract class Wrap_ItemCoaster extends Item {

    protected int modelCount = 0;
    protected int CoasterType = 0;

    public Wrap_ItemCoaster(Properties p_41383_) {
        super(p_41383_);
    }

    public int getModelCount() {
        return modelCount;
    }

    public int getCoasterType() {
        return CoasterType;
    }

    public abstract ERC_EntityCoaster getItemInstance(Level world, Wrap_BlockEntityRail tile, double x, double y, double z);

    public void ScrollMouseHweel(double dhweel) {
//    	ERC_Logger.info("wrap_itemcoaster : dhweel:"+dhweel);
        modelCount += dhweel > 0 ? 1 : -1;
        if (modelCount >= ERC_ModelLoadManager.getModelPackNum(CoasterType)) modelCount = 0;
        else if (modelCount < 0) modelCount = ERC_ModelLoadManager.getModelPackNum(CoasterType) - 1;
//    	ERC_Logger.info("modelcount:"+modelCount);
    }
}
