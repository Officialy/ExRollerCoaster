package erc.block.tileEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import erc.gui.RailScreen.editFlag;
import erc.network.ERC_MessageRailStC;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class Wrap_BlockEntityRail extends BlockEntity implements MenuProvider {

	public Wrap_BlockEntityRail(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
		super(p_155228_, p_155229_, p_155230_);
	}

	//	public abstract Wrap_TileEntityRail getOwnRailData();
	public abstract void setDataToByteMessage(FriendlyByteBuf buf);
	public abstract void getDataFromByteMessage(FriendlyByteBuf buf);
	public abstract Level getWorldObj();
	public abstract BlockPos getPos();
	public abstract TileEntityRailBase getRail();

	public abstract void syncData();
	public abstract Wrap_BlockEntityRail getPrevRailTileEntity();
	public abstract Wrap_BlockEntityRail getNextRailTileEntity();
	
//	public abstract void SetBaseRailPosition(int x, int y, int z, Vec3 BaseDir, Vec3 up, float power);
//	public abstract void SetNextRailVectors(Vec3 vecNext, Vec3 vecDir, Vec3 vecUp, 
//							float fUp, float fDirTwist, float Power, int cx, int cy, int cz);
	
	public abstract void connectionFromBack(int x, int y, int z);
	public abstract void connectionToNext(DataTileEntityRail otherrail, int x, int y, int z);

	public abstract void SetRailDataFromMessage(ERC_MessageRailStC message);

	public abstract ResourceLocation getDrawTexture();
	public abstract void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_);

	public abstract void AddControlPoint(int pointnum);
	public abstract void Smoothing();
	public abstract void AddPower(int idx);
	public abstract void UpdateDirection(editFlag flag, int idx);
	public abstract void ResetRot();
	public abstract void SpecialGUISetData(int flag);
	public abstract float CalcRailLength();

	//sideonly client
	public abstract void changeRailModelRenderer(int index);
}
