package erc.network;

import erc.entity.ERC_EntityCoasterConnector;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ERC_MessageRequestConnectCtS {

    public int playerEntityID;
    public int CoasterID;

    public ERC_MessageRequestConnectCtS(int playerid, int coasterid) {
        playerEntityID = playerid;
        CoasterID = coasterid;
    }

    public ERC_MessageRequestConnectCtS(FriendlyByteBuf buf) {
        this.playerEntityID = buf.readInt();
        this.CoasterID = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.playerEntityID);
        buf.writeInt(this.CoasterID);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            ERC_EntityCoasterConnector coaster = (ERC_EntityCoasterConnector) ((Level) ctx.get().getSender().getLevel()).getEntity(this.CoasterID);
            if (coaster != null) coaster.receiveConnectionRequestFromClient(this.playerEntityID);
        });
        return success.get();
    }
}