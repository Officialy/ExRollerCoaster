package erc.network;

import net.minecraft.client.Minecraft;
import erc.manager.ERC_CoasterAndRailManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ERC_MessageSaveBreakRailStC {

	public int bx, by, bz;
	public int nx, ny, nz;

	public ERC_MessageSaveBreakRailStC(int bx, int by, int bz, int nx, int ny, int nz)
	{
	    this.bx = bx;
	    this.by = by;
	    this.bz = bz;
	    this.nx = nx;
	    this.ny = ny;
	    this.nz = nz;
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

    public void fromBytes(FriendlyByteBuf buf)
    {
	    this.bx = buf.readInt();
	    this.by = buf.readInt();
	    this.bz = buf.readInt();
	    this.nx = buf.readInt();
	    this.ny = buf.readInt();
	    this.nz = buf.readInt();
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
		ctx.get().enqueueWork(() -> {
			ERC_CoasterAndRailManager.SetPrevData(this.bx, this.by, this.bz);
			ERC_CoasterAndRailManager.SetNextData(this.nx, this.ny, this.nz);
		});
        return success.get();
    }

}