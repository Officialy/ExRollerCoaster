package erc.network;

import erc.entity.ERC_EntityCoasterSeat;
import erc.entity.Wrap_EntityCoaster;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ERC_MessageCoasterMisc {

	public Wrap_EntityCoaster coaster;
	public int EntityID;
	public int flag;

	public ERC_MessageCoasterMisc(Wrap_EntityCoaster c, int flag) {
		this.coaster = c;
		this.EntityID = c.getId();
		this.flag = flag;
	}

	public ERC_MessageCoasterMisc(FriendlyByteBuf buf) {
		this.EntityID = buf.readInt();
		this.flag = buf.readInt();
		/*todo Wrap_EntityCoaster coaster = (Wrap_EntityCoaster) this.coaster.getLevel().getEntity(EntityID);
		   if (coaster != null) coaster.SyncCoasterMisc_Receive(buf, flag);*/
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.EntityID);
		buf.writeInt(this.flag);
		coaster.SyncCoasterMisc_Send(buf, flag);
	}

	public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
		return success.get();
	}
}