package erc.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraft.client.model.Model;

public class ERC_ModelCoaster<T extends Entity> extends EntityModel<T> {

	private final OBJModel.ModelSettings modelCoaster;
	private final ResourceLocation TextureResource;

	public ERC_ModelCoaster(OBJModel.ModelSettings Obj, ResourceLocation Tex) {
		modelCoaster = Obj;
		TextureResource = Tex;
	}

	@Override
	public void renderToBuffer(PoseStack stack, VertexConsumer consumer, int x, int y, float z, float x2, float y2, float z2) {
		stack.pushPose();
		stack.translate((float)x, (float)y, z);
// 		if(coaster.ERCPosMat != null)
//		{
// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
//		}
//		stack.mulPose(new Quaternion(180.0F - coaster.ERCPosMat.getFixedYaw(t), 0f, 1f, 0f));
//		stack.mulPose(new Quaternion(coaster.ERCPosMat.getFixedPitch(t),-1f, 0f, 0f));
//		stack.mulPose(new Quaternion(-coaster.ERCPosMat.getFixedRoll(t), 0f, 0f, 1f));
		//FTMakes the coaster look like it's going in reverse
		//FTGL11.glRotatef(coaster.ERCPosMat.getFixedYaw(t), 0f, -1f, 0f);
		//FTGL11.glRotatef(coaster.ERCPosMat.getFixedPitch(t),1f, 0f, 0f);
		//FTGL11.glRotatef(-coaster.ERCPosMat.getFixedRoll(t), 0f, 0f, 1f);

		stack.scale(1.0f, 1.0f, 1.0f);
		Minecraft.getInstance().getTextureManager().bindForSetup(TextureResource);
		OBJLoader.INSTANCE.loadModel(this.modelCoaster);
		stack.popPose();
	}

	@Override
	public void setupAnim(T p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_) {

	}
}