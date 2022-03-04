package erc.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import erc.core.ERCConstants;
import erc.entity.ERC_EntityCoaster;
import erc.manager.ERC_ModelLoadManager;
import erc.model.ERC_ModelCoaster;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderCoaster extends EntityRenderer<ERC_EntityCoaster> {

    public static final ResourceLocation TEXTURE = new ResourceLocation(ERCConstants.DOMAIN + "models/coaster.png");
    protected final EntityModel<ERC_EntityCoaster> model;

    public RenderCoaster(EntityRendererProvider.Context context) {
        super(context);
        ERC_ModelLoadManager.ModelPack mp;
        mp = ERC_ModelLoadManager.ModelPackList_Connect.get(1);

        this.shadowRadius = 0.5F;
        this.model = new ERC_ModelCoaster<>(mp.Model, mp.Tex);
    }

    public void render(ERC_EntityCoaster coaster, float p_114486_, float p_114487_, PoseStack poseStack, MultiBufferSource bufferSource, int p_114490_) {
        if (coaster.getModelRenderer() == null) return;
        VertexConsumer vertexconsumer = bufferSource.getBuffer(this.model.renderType(TEXTURE));
        coaster.getModelRenderer().renderToBuffer(poseStack, vertexconsumer, 1, 1, 1, 1, 1, 1);
//		Entity[] ea = coaster.getParts();
//		for(int i=0; i<ea.length; i++)
//		{
//			renderOffsetAABB(ea[i].boundingBox.getOffsetBoundingBox(-coaster.getX(), -coaster.getY(), -coaster.getZ()), x, y, z);
//		}
        super.render(coaster, p_114486_, p_114487_, poseStack, bufferSource, p_114490_);
    }

    @Override
    public boolean shouldRender(ERC_EntityCoaster p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(ERC_EntityCoaster entity) {
        return TEXTURE;
    }

}