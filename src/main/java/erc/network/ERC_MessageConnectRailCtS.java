package erc.network;

import erc.block.tileEntity.Wrap_BlockEntityRail;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ERC_MessageConnectRailCtS {

	public int bx, by, bz;
	public int nx, ny, nz;

	public ERC_MessageConnectRailCtS(int bx, int by, int bz, int nx, int ny, int nz)
	{
	    this.bx = bx;
	    this.by = by;
	    this.bz = bz;
	    this.nx = nx;
	    this.ny = ny;
	    this.nz = nz;
  	}

	public ERC_MessageConnectRailCtS(FriendlyByteBuf buf)
	{
		this.bx = buf.readInt();
		this.by = buf.readInt();
		this.bz = buf.readInt();
		this.nx = buf.readInt();
		this.ny = buf.readInt();
		this.nz = buf.readInt();
	}

	public void encode(FriendlyByteBuf buf)
	{
		buf.writeInt(this.bx);
		buf.writeInt(this.by);
		buf.writeInt(this.bz);
		buf.writeInt(this.nx);
		buf.writeInt(this.ny);
		buf.writeInt(this.nz);
	}

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        final var success = new AtomicBoolean(false);
    	final ServerPlayer player = ctx.get().getSender();
    	ctx.get().enqueueWork(() -> {
			Wrap_BlockEntityRail Wbase = (Wrap_BlockEntityRail)ctx.get().getSender().getLevel().getBlockEntity(new BlockPos(this.bx, this.by, this.bz));
			Wrap_BlockEntityRail Wnext = (Wrap_BlockEntityRail)ctx.get().getSender().getLevel().getBlockEntity(new BlockPos(this.nx, this.ny, this.nz));

			if ((Wbase != null && Wnext != null))
			{
//        		ERC_TileEntityRailBase base = Wbase.getRail();
//        		ERC_TileEntityRailBase next = Wnext.getRail();


//        		next.SetPrevRailPosition(message.bx, message.by, message.bz);
//        		next.CreateNewRailVertexFromControlPoint();
//        		next.CalcRailLength();
//        		next.syncData();
				Wnext.connectionFromBack(this.bx, this.by, this.bz);

//        		base.BaseRail.Power = power; // TODO check
//        		base.SetNextRailPosition(message.nx, message.ny, message.nz);
//        		base.SetNextRailVectors(next);
//        		base.CreateNewRailVertexFromControlPoint();
//        		base.CalcRailLength();
//        		base.syncData();
				Wbase.connectionToNext(Wnext.getRail().BaseRail, this.nx, this.ny, this.nz);
			}
		});
        return success.get();
    }
}
