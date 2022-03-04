package erc.block.tileEntity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import erc.gui.container.DefMenu;
import erc.network.ERC_MessageRailStC;
import erc.registry.ERCBlockEntities;
import net.minecraft.core.BlockPos;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

import erc.core.ERC_Logger;
import erc.core.ERC_ReturnCoasterRot;
import erc.entity.ERC_EntityCoaster;
import erc.gui.RailScreen;
import erc.gui.RailScreen.editFlag;
import erc.math.ERC_MathHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class TileEntityRailBranch2 extends /*ERC_TileEntityRailBase*/Wrap_BlockEntityRail {
    boolean toggleflag;
    int branchflag;
    //	float branchLens[] = new float[2];
    TileEntityRailNormal[] rails;

    public TileEntityRailBranch2(BlockPos pos, BlockState state) {
        super(ERCBlockEntities.RAILBRANCH.get(), pos, state);

        toggleflag = false;
        branchflag = 0;
        rails = new TileEntityRailNormal[2];
        rails[0] = new TileEntityRailNormal(pos, state);
        rails[1] = new TileEntityRailNormal(pos, state);

        //branch̃f[^Railsɔf
        rails[0].setLevel(this.level);
        rails[1].setLevel(this.level);
    }

    public boolean getToggleFlag() {
        return toggleflag;
    }

    public void changeToggleFlag() {
        toggleflag = !toggleflag;
    }


    public void changeRail() {
        changeRail(branchflag == 0 ? 1 : 0);
    }

    public void changeRail(int flag) {
        if (flag > 1) {
            ERC_Logger.warn("railbranch2 : out of index for railsflag");
            return;
        }

        branchflag = flag;
        setPosToRails();
    }

    public int getNowRailFlag() {
        return branchflag;
    }

    public Level getWorldObj() {
        return level;
    }

    public BlockPos getPos() {
        return this.worldPosition;
    }

    public TileEntityRailBase getRail() {
        rails[branchflag].setLevel(this.level);
//        todo check rails[branchflag].setPos(this.worldPosition);
        return rails[branchflag];
    }

    public Wrap_BlockEntityRail getPrevRailTileEntity() {
        return (Wrap_BlockEntityRail) level.getBlockEntity(new BlockPos(rails[branchflag].BaseRail.cx, rails[branchflag].BaseRail.cy, rails[branchflag].BaseRail.cz));
    }

    public Wrap_BlockEntityRail getNextRailTileEntity() {
        return ((Wrap_BlockEntityRail) level.getBlockEntity(new BlockPos(rails[branchflag].NextRail.cx, rails[branchflag].NextRail.cy, rails[branchflag].NextRail.cz)));
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

//	@Override
//	public void SetPrevRailPosition(int x, int y, int z) {
//		rails[0].SetPrevRailPosition(x, y, z);
//		rails[1].SetPrevRailPosition(x, y, z);
//	}
//
//	@Override
//	public void SetNextRailPosition(int x, int y, int z) {
//		rails[branchflag].SetNextRailPosition(x, y, z);
//	}

//	@Override
//	public ERC_TileEntityRailBase getPrevRailTileEntity() {
//		return rails[branchflag].getPrevRailTileEntity();
//	}
//
//	@Override
//	public ERC_TileEntityRailBase getNextRailTileEntity() {
//		return rails[branchflag].getNextRailTileEntity();
//	}

    public void SetPosNum(int num) {
        rails[branchflag].SetPosNum(num);
    }

    public int GetPosNum() {
        return rails[branchflag].GetPosNum();
    }

    public void SetPrevRailPosition(int x, int y, int z) {
        rails[0].BaseRail.SetPos(x, y, z);
        rails[1].BaseRail.SetPos(x, y, z);
    }

    public void SetBaseRailPosition(int x, int y, int z, Vec3 BaseDir, Vec3 up, float power) {
        rails[0].SetBaseRailPosition(x, y, z, BaseDir, up, power);
        rails[1].SetBaseRailPosition(x, y, z, BaseDir, up, power);
    }

    // GUI
    public void AddControlPoint(int pointnum) {
        rails[0].AddControlPoint(pointnum);
        rails[1].AddControlPoint(pointnum);
    }

    public void Smoothing() {
        rails[0].setLevel(level);
        rails[1].setLevel(level);
        rails[0].Smoothing();
        rails[1].Smoothing();
    }

    public void AddPower(int flag) {
        rails[0].AddPower(flag);
        rails[1].AddPower(flag);
    }

    public void UpdateDirection(editFlag flag, int idx) {
        rails[branchflag].setLevel(level);
        rails[branchflag].UpdateDirection(flag, idx);
        rails[branchflag == 0 ? 1 : 0].BaseRail.SetData(rails[branchflag].BaseRail);
    }

    public void ResetRot() {
        rails[0].ResetRot();
        rails[1].ResetRot();
    }

    public void SetNextRailVectors(Vec3 vecNext, Vec3 vecDir, Vec3 vecUp, float fUp, float fDirTwist, float Power,
                                   int cx, int cy, int cz) {
        rails[branchflag].SetNextRailVectors(vecNext, vecDir, vecUp, fUp, fDirTwist, Power, cx, cy, cz);
    }


    public double CalcRailPosition2(float t, ERC_ReturnCoasterRot ret, float viewyaw, float viewpitch, boolean riddenflag) {
        return rails[branchflag].CalcRailPosition2(t, ret, viewyaw, viewpitch, riddenflag);
    }

    public float CalcRailLength() {
        rails[branchflag == 0 ? 1 : 0].CalcRailLength();
        return rails[branchflag].CalcRailLength();
    }

    public void CalcPrevRailPosition() {
        rails[branchflag].CalcPrevRailPosition();
    }

    public void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster) {
    }

    public void onPassedCoaster() {
    }

    public void onApproachingCoaster() {
    }

    public void SpecialGUIInit(RailScreen gui) {
    }

    public void SpecialGUISetData(int flag) {
    }

    ////////////////////////////////////////////////////////////////////////////
    // Draw
    ////////////////////////////////////////////////////////////////////////////
    public ResourceLocation getDrawTexture() {
        return this.rails[branchflag].RailTexture;
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        rails[branchflag].render(stack, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
//        todo GlStateManager.translate(0.0, -0.00005, 0.0);
        RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 1.0F);
        rails[branchflag == 0 ? 1 : 0].render(stack, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
    }

    ////////////////////////////////////////////////////////////////////////////
    // save, sync
    ////////////////////////////////////////////////////////////////////////////
/*todo    public void SetRailDataFromMessage(ERC_MessageRailStC msg) {
        //branch2
        /////\\\\\
        Iterator<DataTileEntityRail> it = msg.raillist.iterator();
        // 0Base
        DataTileEntityRail e = it.next();
        rails[0].SetPosNum(msg.posnum);
        rails[0].SetBaseRailVectors(e.vecPos, e.vecDir, e.vecUp, e.Power);
        rails[0].SetBaseRailfUpTwist(e.fUp, e.fDirTwist);
        rails[0].BaseRail.SetPos(e.cx, e.cy, e.cz);
        // 1Base
        rails[1].SetPosNum(msg.posnum);
        rails[1].SetBaseRailVectors(e.vecPos, e.vecDir, e.vecUp, e.Power);
        rails[1].SetBaseRailfUpTwist(e.fUp, e.fDirTwist);
        rails[1].BaseRail.SetPos(e.cx, e.cy, e.cz);
        // 0Next
        e = it.next();
        rails[0].SetNextRailVectors(e.vecPos, e.vecDir, e.vecUp, e.fUp, e.fDirTwist, e.Power, e.cx, e.cy, e.cz);
        // 1Next
        e = it.next();
        rails[1].SetNextRailVectors(e.vecPos, e.vecDir, e.vecUp, e.fUp, e.fDirTwist, e.Power, e.cx, e.cy, e.cz);
        /////\\\\\
        *//*
        todo this.setPos(new BlockPos(msg.x, msg.y, msg.z));
        rails[1].setPos(this.pos);
        rails[0].setPos(this.pos);
        *//*
//		rails[0].CreateNewRailVertexFromControlPoint();
//		rails[1].CreateNewRailVertexFromControlPoint();
        rails[0].CalcRailPosition();
        rails[1].CalcRailPosition();
    }*/

    public void setDataToByteMessage(FriendlyByteBuf buf) {
        buf.writeBoolean(this.toggleflag);
        buf.writeInt(branchflag);
    }

    public void getDataFromByteMessage(FriendlyByteBuf buf) {
        toggleflag = buf.readBoolean();
        branchflag = buf.readInt();
    }

    @Override
    public void load(CompoundTag nbt) {
        rails[0].loadFromNBT(nbt, "b0");
        rails[1].loadFromNBT(nbt, "b1");
        branchflag = nbt.getInt("branch2flag");
        setPosToRails(0);
        setPosToRails(1);
        super.load(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        rails[0].saveToNBT(nbt, "b0");
        rails[1].saveToNBT(nbt, "b1");
        nbt.putInt("branch2flag", branchflag);
        super.saveAdditional(nbt);
    }

   /* @Override
    todo public SPacketUpdateTileEntity getUpdatePacket() {
        CompoundTag nbtTagCompound = new CompoundTag();
        this.writeToNBT(nbtTagCompound);
        return new SPacketUpdateTileEntity(this.pos, 1, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
        setPosToRails(0);
        setPosToRails(1);
    }*/

//	public void syncData(ServerPlayer player) {
//		setPosToRails(0);
//		setPosToRails(1);
//		rails[0].syncData(player);
//		rails[1].syncData(player);
//	}

    public void syncData() {
//		setPosToRails(0);
//		setPosToRails(1);
//		rails[0].syncData();
//		rails[1].syncData();
//		ERC_MessageRailStC packet = new ERC_MessageRailStC(xCoord, yCoord, zCoord, 0);
//		ERC_PacketHandler.INSTANCE.sendToAll(packet);

//      todo  ERC_MessageRailStC packet = new ERC_MessageRailStC(this.getXcoord(), this.getYcoord(), this.getZcoord(), rails[0].PosNum, rails[0].modelrailindex);
//        packet.addRail(rails[branchflag].BaseRail);
//        packet.addRail(rails[0].NextRail);
//        packet.addRail(rails[1].NextRail);
//       ERC_PacketHandler.INSTANCE.sendToAll(packet);
    }

    public void connectionFromBack(int x, int y, int z) {
        this.SetPrevRailPosition(x, y, z);
        this.syncData();
    }

    public void connectionToNext(DataTileEntityRail next, int x, int y, int z) {
        float power = ERC_MathHelper.CalcSmoothRailPower(rails[branchflag].BaseRail.vecDir, next.vecDir, rails[branchflag].BaseRail.vecPos, next.vecPos);
        rails[branchflag].BaseRail.Power = power;
        rails[branchflag].SetNextRailVectors(next, x, y, z);
//    	rails[branchflag].CreateNewRailVertexFromControlPoint();
        rails[branchflag].CalcRailLength();
        this.syncData();

        /////////////////////////rails[branchflag].syncData();
    }

    @Override
    public void SetRailDataFromMessage(ERC_MessageRailStC message) {

    }

//	public void onTileSetToWorld_Init()
//	{
//		setPosToRails();
//		changeRail();
//		setPosToRails();
//		changeRail();
//	}

    private void setPosToRails() {
        setPosToRails(branchflag);
    }

    private void setPosToRails(int flag) {
//     todo   rails[flag].setPos(this.pos);
        rails[flag].setLevel(this.level);
    }

    @Override
    public void changeRailModelRenderer(int index) {
        rails[0].setLevel(this.level);
        rails[1].setLevel(this.level);
        rails[0].changeRailModelRenderer(index);
        rails[1].changeRailModelRenderer(index);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return BaseContainerBlockEntity.canUnlock(player, LockCode.NO_LOCK, this.getDisplayName()) ? new DefMenu(id, inv, this) : null;
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container.rail");
    }
}
