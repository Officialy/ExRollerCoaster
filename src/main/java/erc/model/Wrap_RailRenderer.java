package erc.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public abstract class Wrap_RailRenderer extends Model {

	public Wrap_RailRenderer(Function<ResourceLocation, RenderType> p_103110_) {
		super(p_103110_);
	}

	public abstract void setModelNum(int PosNum_org);
	public abstract void construct(int idx, Vec3 Pos, Vec3 Dir, Vec3 Cross, float exParam);
	public abstract void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_);

}
