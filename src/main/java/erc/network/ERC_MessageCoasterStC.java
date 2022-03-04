package erc.network;

import net.minecraft.client.Minecraft;
import erc.entity.ERC_EntityCoaster;
import erc.manager.ERC_ModelLoadManager.ModelOptions;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ERC_MessageCoasterStC {

    public int entityID;
    public float paramT;
    public double speed;
    public final BlockPos pos;
    public int modelID;
    public ModelOptions ops = new ModelOptions();

    public ERC_MessageCoasterStC(int id, float t, double v, BlockPos pos, int modelid, ModelOptions op) {
        this.paramT = t;
        this.entityID = id;
        this.speed = v;
        this.pos = pos;
        this.modelID = modelid;
        this.ops = op;
    }

    public ERC_MessageCoasterStC(FriendlyByteBuf buf) {
        this.paramT = buf.readFloat();
        this.entityID = buf.readInt();
        this.speed = buf.readDouble();
        this.pos = buf.readBlockPos();

        this.modelID = buf.readInt();
        ops.ReadBuf(buf);
//	    this.connectparentID = buf.readInt();
    }

//	public ERC_MessageCoasterStC(int id, float t, double v, int x, int y, int z, int connectparentID)
//	{
//	    this.paramT = t;
//	    this.entityID = id;
//	    this.speed = v;
//	    this.x = x;
//	    this.y = y;
//	    this.z = z;
////	    this.connectparentID = connectparentID;
//  	}

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.paramT);
        buf.writeInt(this.entityID);
        buf.writeDouble(this.speed);
        buf.writeBlockPos(this.pos);

        buf.writeInt(this.modelID);
        ops.WriteBuf(buf);
//		buf.writeInt(this.connectparentID);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            ERC_EntityCoaster coaster = (ERC_EntityCoaster) ctx.get().getSender().getLevel().getEntity(this.entityID);
            if (coaster != null) {
                coaster.setParamFromPacket(this.paramT, this.speed, this.pos);
                coaster.setModelOptions(this.modelID, this.ops);

//				if(message.connectparentID > -1)
//				{
//			 		ERC_EntityCoaster parent = (ERC_EntityCoaster)FMLClientHandler.instance().getClient().theWorld.getEntityByID(message.connectparentID);
//			 		if(parent == null)
//			 		{
//				 		coaster.killCoaster();
//				 		return null;
//					}
//			 		((ERC_EntityCoasterConnector)coaster).setParentPointer(parent);
//			 		parent.connectionCoaster((ERC_EntityCoasterConnector) coaster);
//			 		((ERC_EntityCoasterConnector)coaster).setConnectParentFlag(-1);
//				}
            }
        });
        return success.get();
    }
}
