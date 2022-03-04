package erc.block.tileEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import erc.block.BlockRailInvisible;
import erc.entity.ERC_EntityCoaster;
import erc.gui.container.DefMenu;
import erc.item.ERC_ItemWrench;
import erc.network.ERC_MessageRailStC;
import erc.registry.ERCBlockEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class TileEntityRailInvisible extends TileEntityRailBase{

	public TileEntityRailInvisible(BlockPos pos, BlockState state)
	{
		super(ERCBlockEntities.INVISIBLE.get(), pos, state);
		RailTexture = new ResourceLocation("textures/blocks/glass.png");
	}
	
	@Override
	public void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster) {}

	@Override
	public Level getWorldObj() {
		return this.level;
	}

	@Override
	public void SetRailDataFromMessage(ERC_MessageRailStC message) {

	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {
		ItemStack is = Minecraft.getInstance().player.getMainHandItem();
		if(!is.isEmpty()) {
			Item heldItem = is.getItem();
			if (Block.byItem(heldItem) instanceof BlockRailInvisible || heldItem instanceof ERC_ItemWrench)
				super.render(stack, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
		}
	}

}
