package erc.network;

import erc.block.tileEntity.Wrap_BlockEntityRail;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ERC_MessageRailMiscStC {

	public final BlockPos pos;
	Wrap_BlockEntityRail rail;

	public ERC_MessageRailMiscStC(Wrap_BlockEntityRail r)
	{
	    this.rail = r;
	    this.pos = rail.getPos();
  	}

    public ERC_MessageRailMiscStC(FriendlyByteBuf buf)
    {
		pos = buf.readBlockPos();
		Wrap_BlockEntityRail rail = (Wrap_BlockEntityRail) this.rail.getLevel().getBlockEntity(pos); //todo this might crash
		if(rail==null)return;
		rail.getDataFromByteMessage(buf);
    }

	public void encode(FriendlyByteBuf buf)
	{
		buf.writeBlockPos(pos);
		rail.setDataToByteMessage(buf);
	}

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        final var success = new AtomicBoolean(false);
        return success.get();
    }

}