package erc.network;

import erc.gui.RailScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import erc.block.tileEntity.TileEntityRailBase;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ERC_MessageRailGUICtS {

    // GUI瑗肽
    public final BlockPos pos;
    public int FLAG;
    public int MiscInt;
//	public int ControlPointNum;
//	public boolean smoothflag;
//	public float pow;
//	public int rotflag;
//	public float rotration;
//	public boolean reset;

    //	public ERC_MessageRailGUICtS(int x, int y, int z, int cpnum, boolean smooth, float pow, int rotflag, float rotratio, boolean reset)
    public ERC_MessageRailGUICtS(BlockPos pos, int flag, int imisc) {
        this.pos = pos;
        this.FLAG = flag;
        this.MiscInt = imisc;
//	    this.ControlPointNum = cpnum;
//	    this.smoothflag = smooth;
//	    this.pow = pow;
//	    this.rotflag = rotflag;
//	    this.rotration = rotratio;
//	    this.reset = reset;
    }

    public ERC_MessageRailGUICtS(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.FLAG = buf.readInt();
        this.MiscInt = buf.readInt();
//	    this.ControlPointNum = buf.readInt();
//	    this.smoothflag = buf.readBoolean();
//	    this.pow = buf.readFloat();
//	    this.rotflag = buf.readInt();
//	    this.rotration = buf.readFloat();
//	    this.reset = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.FLAG);
        buf.writeInt(this.MiscInt);
//        buf.writeInt(this.ControlPointNum);
//        buf.writeBoolean(this.smoothflag);
//        buf.writeFloat(this.pow);
//        buf.writeInt(this.rotflag);
//        buf.writeFloat(this.rotration);
//        buf.writeBoolean(this.reset);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            Level world = ctx.get().getSender().getLevel();
            BlockEntity Wte = world.getBlockEntity(this.pos);
            if ((Wte instanceof Wrap_BlockEntityRail)) {
                Wrap_BlockEntityRail te = ((Wrap_BlockEntityRail) Wte);
                RailScreen.editFlag[] values = RailScreen.editFlag.values();
                RailScreen.editFlag align = values[this.FLAG];
                switch (align) {
                    case CONTROLPOINT -> te.AddControlPoint(this.MiscInt);
                    case SMOOTH ->
//        				if(te.isConnectRail_prev1_next2())
                            te.Smoothing();
                    case POW -> te.AddPower(this.MiscInt);
                    case ROTRED, ROTGREEN, ROTBLUE -> te.UpdateDirection(align, this.MiscInt);
                    case RESET -> te.ResetRot();
                    case SPECIAL -> te.SpecialGUISetData(this.MiscInt);
                    case RailModelIndex -> // modelIndex send to server
                            te.changeRailModelRenderer(this.MiscInt);
                    default -> {
                    }
                }

                te.CalcRailLength();
                te.syncData();
                Wrap_BlockEntityRail prev = ((Wrap_BlockEntityRail) Wte).getPrevRailTileEntity();
                if (prev != null) {
                    TileEntityRailBase r = prev.getRail();
                    r.SetNextRailVectors(te.getRail());
//                    r.CreateNewRailVertexFromControlPoint();
                    r.CalcRailLength();
                    prev.syncData();
                }
            }
        });
        return success.get();
    }
}
