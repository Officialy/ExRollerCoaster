package erc.network;

import erc.core.ERC_Logger;
import erc.entity.ERC_EntityCoaster;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ERC_MessageCoasterCtS {

    public int entityID;
    public float paramT;
    public double speed;
    public final BlockPos pos;
//	public int modelID;
//	public ModelOptions ops;

    public ERC_MessageCoasterCtS(int id, float t, double v, BlockPos pos) {
        this.paramT = t;
        this.entityID = id;
        this.speed = v;
        this.pos = pos;

//	    this.modelID = ID;
//	    this.ops = op;
    }

    public ERC_MessageCoasterCtS(FriendlyByteBuf buf) {
        this.paramT = buf.readFloat();
        this.entityID = buf.readInt();
        this.speed = buf.readDouble();
        this.pos = buf.readBlockPos();

//	    this.modelID = buf.readInt();
//	    ops.ReadBuf(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(this.paramT);
        buf.writeInt(this.entityID);
        buf.writeDouble(this.speed);
        buf.writeBlockPos(this.pos);

//		buf.writeInt(this.modelID);
//		ops.WriteBuf(buf);
    }


    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            Level world = ctx.get().getSender().getLevel();
            ERC_EntityCoaster coaster = (ERC_EntityCoaster) world.getEntity(this.entityID);
            if (coaster != null) {
                if (this.paramT > -50f) {
                    coaster.setParamT(this.paramT);
                    coaster.Speed = this.speed;
                    coaster.setRail(((Wrap_BlockEntityRail) world.getBlockEntity(pos)).getRail());
//					coaster.setModel(this.modelID);
                } else {
                    ERC_Logger.warn("MessageCoasterCtS : this code must not call.");
//					coaster.setModel(this.modelID);
//					coaster.setModelOptions(this.ops);
                }
            }
        });
        return success.get();
    }
}
