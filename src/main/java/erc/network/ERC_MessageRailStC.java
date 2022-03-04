package erc.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import com.mojang.math.Vector3d;
import erc.core.ERC_Logger;
import net.minecraft.client.Minecraft;
import erc.block.tileEntity.DataTileEntityRail;
import erc.block.tileEntity.Wrap_BlockEntityRail;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

public final class ERC_MessageRailStC {

    // GUI瑗肽
    public final BlockPos pos;
    int railnum;
    public int posnum;
    public List<DataTileEntityRail> raillist;
    public int modelIndex;

    public ERC_MessageRailStC(BlockPos pos, int cpnum, int modelidx) {
        this.pos = pos;
        this.posnum = cpnum;
        railnum = 0;
        raillist = new ArrayList<>();
        modelIndex = modelidx;
    }

    public ERC_MessageRailStC(FriendlyByteBuf buf) {
        this.modelIndex = buf.readInt();
        this.pos = buf.readBlockPos();
        this.posnum = buf.readInt();
        int railnum = buf.readInt();
        raillist = new ArrayList<DataTileEntityRail>();
        for (int i = 0; i < railnum; ++i) {
            DataTileEntityRail r = new DataTileEntityRail();
            r.cx = buf.readInt();
            r.cy = buf.readInt();
            r.cz = buf.readInt();
            r.vecPos = readVec(buf);
            r.vecDir = readVec(buf);
            r.vecUp = readVec(buf);
            r.Power = buf.readFloat();
            r.fUp = buf.readFloat();
            r.fDirTwist = buf.readFloat();
            raillist.add(r);
        }
    }

    public void addRail(DataTileEntityRail rail) {
        railnum++;
        raillist.add(rail);
    }

    private Vec3 readVec(FriendlyByteBuf buf) {
        return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    private void writeVec(FriendlyByteBuf buf, Vec3 vec) {
        buf.writeDouble(vec.x);
        buf.writeDouble(vec.y);
        buf.writeDouble(vec.z);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(modelIndex);
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.posnum);
        buf.writeInt(raillist.size());
        for (DataTileEntityRail r : raillist) {
            buf.writeInt(r.cx);
            buf.writeInt(r.cy);
            buf.writeInt(r.cz);
            writeVec(buf, r.vecPos);
            writeVec(buf, r.vecDir);
            writeVec(buf, r.vecUp);
            buf.writeFloat(r.Power);
            buf.writeFloat(r.fUp);
            buf.writeFloat(r.fDirTwist);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            BlockEntity te = ctx.get().getSender().getLevel().getBlockEntity(pos);
            if (!(te instanceof Wrap_BlockEntityRail)) {
                ERC_Logger.info("MessageRailStC::handle, blockentity is not tilerail. pos:" + pos);
                if (te == null) ERC_Logger.info("EEEBlockEntity is null.");
            } else {
                Wrap_BlockEntityRail ter = ((Wrap_BlockEntityRail) te);
                ter.SetRailDataFromMessage(this);
                ter.changeRailModelRenderer(this.modelIndex);
                //        	ERC_TileEntityRailBase ter = ((Wrap_TileEntityRail)te).getRail();
                //    		ter.SetPosNum(message.posnum);
                //    		ter.SetRailDataFromRailListMessage(message.raillist);
                //    		ter.CreateNewRailVertexFromControlPoint();
                //    		ter.CalcRailPosition();
            }
        });
        return success.get();
    }

}
