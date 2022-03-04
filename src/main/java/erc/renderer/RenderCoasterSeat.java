package erc.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import erc.core.ERCConstants;
import erc.entity.ERC_EntityCoasterSeat;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;


public class RenderCoasterSeat extends EntityRenderer<ERC_EntityCoasterSeat> {

	private static final ResourceLocation tex = new ResourceLocation(ERCConstants.DOMAIN + "models/coaster.png");

	public RenderCoasterSeat(EntityRendererProvider.Context p_174008_) {
		super(p_174008_);
	}

	@Override
	public void render(ERC_EntityCoasterSeat p_114485_, float p_114486_, float p_114487_, PoseStack p_114488_, MultiBufferSource p_114489_, int p_114490_) {
//					renderOffsetAABB(entity.boundingBox.getOffsetBoundingBox(-entity.getX(), -entity.getY(), -entity.getZ()), x, y, z);
	}

	@Override
	public ResourceLocation getTextureLocation(ERC_EntityCoasterSeat p_114482_) {
		return tex;
	}
}
