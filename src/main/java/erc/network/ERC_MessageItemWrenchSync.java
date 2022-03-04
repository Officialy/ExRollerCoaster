package erc.network;

import erc.block.tileEntity.TileEntityRailBase;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import erc.core.ExRollerCoaster;
import erc.gui.container.DefMenu;
import erc.item.ERC_ItemWrench;
import erc.registry.ERCItems;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ERC_MessageItemWrenchSync {

	public Item classItem;
	public int mode=-1;
	public BlockPos pos;

	public ERC_MessageItemWrenchSync(int mode, BlockPos pos)
	{
		this.mode = mode;
		this.pos = pos;
	}

	public ERC_MessageItemWrenchSync(FriendlyByteBuf buf)
	{
		mode = buf.readInt();
		pos = buf.readBlockPos();
	}

	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(mode);
		buf.writeBlockPos(pos);
	}

	public boolean handle(Supplier<NetworkEvent.Context> ctx)
	{
        final var success = new AtomicBoolean(false);
//		((ERC_ItemWrench)ERC_Core.ItemWrench).receivePacket(message.mode);
		final ServerPlayer player = ctx.get().getSender();
		ctx.get().enqueueWork(() -> {
			switch (this.mode) {
				case 0:
					break;// connect ɂ
				case 1: //gui
//					player.openMenu(ExRollerCoaster.INSTANCE, ExRollerCoaster.GUIID_RailBase, player.getLevel(), pos);
					NetworkHooks.openGui(player, new MenuProvider() {
						@Override
						public Component getDisplayName() {
							return new TextComponent("rail_menu");
						}

						@Nullable
						@Override
						public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
							return new DefMenu(id, inv, (Wrap_BlockEntityRail) player.level.getBlockEntity(pos));
						}
					}, buf -> buf.writeBlockPos(pos));
					break;
				case 2:
					((ERC_ItemWrench) ERCItems.WRENCH_ITEM.get()).placeBlockAt(new ItemStack(ERCItems.WRENCH_ITEM.get()), player, player.getLevel(), this.pos, Blocks.DIRT);
					break;
				default:
					break;
			}
		});
	    return success.get();
	}
}
