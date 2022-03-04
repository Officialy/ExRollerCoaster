package erc.gui.container;

import erc.block.tileEntity.TileEntityRailBase;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import erc.registry.ERCMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class DefMenu extends AbstractContainerMenu {

    Wrap_BlockEntityRail tile;

    public DefMenu(int id, Inventory inv, Wrap_BlockEntityRail rail) {
        super(ERCMenus.RAIL_MENU.get(), id);

        this.tile = rail;
    }

    @Override
    public boolean stillValid(Player player) {
//        return player.distanceToSqr(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64D;
        return true;
    }

}
