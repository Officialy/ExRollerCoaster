package erc.block.tileEntity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import erc.block.BlockRailBase;
import erc.gui.container.DefMenu;
import erc.network.ERC_MessageRailMiscStC;
import erc.network.ERC_MessageRailStC;
import erc.network.ERC_PacketHandler;
import erc.registry.ERCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.opengl.GL11;

import erc.entity.ERC_EntityCoaster;
import erc.gui.RailScreen;
import erc.gui.RailScreen.editFlag;

import net.minecraft.nbt.CompoundTag;

public class TileEntityRailRedstoneAccelerator extends TileEntityRailBase{
	
	float accelParam = 0.04f;
	boolean toggleflag;
	
	public TileEntityRailRedstoneAccelerator(BlockPos pos, BlockState state)
	{
		super(ERCBlockEntities.ACCELERATOR_RAIL.get(), pos, state);
		toggleflag = false;
		RailTexture = new ResourceLocation("textures/blocks/redstone_block.png");
	}

	public boolean getToggleFlag()
	{
		return toggleflag;
	}
	public void setToggleFlag(boolean flag)
	{
		toggleflag = flag;
	}
	public void changeToggleFlag()
	{
		toggleflag = !toggleflag;
	}
	
	public void setAccelParam(float f)
	{
		accelParam = f;
	}
	public float getAccelBase()
	{
		return accelParam;
	}
	
	public void SpecialRailProcessing(ERC_EntityCoaster coaster)
	{
		// 
		if(toggleflag)
		{
			coaster.Speed += accelParam;
		}
		// 
		else
		{
			coaster.Speed *= 0.8;
			if(coaster.Speed < 0.008)coaster.Speed = 0;
		}
	}

	
	public void setDataToByteMessage(FriendlyByteBuf buf)
	{
		buf.writeBoolean(this.toggleflag);
		buf.writeFloat(accelParam);
	}
	public void getDataFromByteMessage(FriendlyByteBuf buf)
	{
		toggleflag = buf.readBoolean();
		accelParam = buf.readFloat();
	}

	@Override
	public Level getWorldObj() {
		return this.level;
	}

	@Override
	public void SetRailDataFromMessage(ERC_MessageRailStC message) {

	}

	@Override
	public void readFromNBT(CompoundTag nbt) 
	{
		super.readFromNBT(nbt);
		toggleflag = nbt.getBoolean("red:toggleflag");
		accelParam = nbt.getFloat("red:accelparam");
	}

	@Override
	public void saveAdditional(CompoundTag nbt)
	{
		nbt.putBoolean("red:toggleflag", toggleflag);
		nbt.putFloat("red:accelparam", accelParam);
		super.saveAdditional(nbt);
	}
	
    // GUI
    public void SpecialGUIInit(RailScreen gui)
    {
    	gui.addButton4("Accel Param", editFlag.SPECIAL);
    }

    public void SpecialGUISetData(int flag)
    {
    	switch(flag)
    	{
    	case 0 : accelParam += -0.01;   break;
    	case 1 : accelParam += -0.001;  break;
    	case 2 : accelParam +=  0.001;  break;
    	case 3 : accelParam +=  0.01;   break;
    	}
    	if(accelParam > 0.1f)accelParam = 0.1f;
    	else if(accelParam < -0.1f) accelParam = -0.1f;
		ERC_PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ERC_MessageRailMiscStC(this));
    }
    @Override
    public String SpecialGUIDrawString()
    {
    	return String.format("%02.1f", (accelParam * 100f));
    }


	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {
//		GlStateManager.disableLighting();
    	float col = toggleflag?1.0f:0.3f;
		RenderSystem.setShaderColor(col, col, col, 1.0F);
    	super.render(stack, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
//    	GlStateManager.enableLighting();
	}

	/**
	 * Called from Chunk.setBlockIDWithMetadata and Chunk.fillChunk, determines if this tile entity should be re-created when the ID, or Metadata changes.
	 * Use with caution as this will leave straggler TileEntities, or create conflicts with other TileEntities if not used properly.
	 *
	 * @param world Current world
	 * @param pos Tile's world position
	 * @param oldState The old ID of the block
	 * @param newState The new ID of the block (May be the same)
	 * @return true forcing the invalidation of the existing TE, false not to invalidate the existing TE
	 */
	public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newState)
	{
		//return super.shouldRefresh(world, pos, oldState, newState);
		return !(oldState.getBlock() == newState.getBlock() && (oldState.getValue(BlockRailBase.DIRECTION)) == (newState.getValue(BlockRailBase.DIRECTION)));
    }
}
