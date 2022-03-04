package erc.model;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Quaternion;
import erc.core.ERC_Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.function.Function;

public class ERC_ModelDefaultRail extends Wrap_RailRenderer {

    Vec3[] posArray;
    Vec3[] normalArray;
    private int PosNum;

    public ERC_ModelDefaultRail(Function<ResourceLocation, RenderType> p_103110_) {
        super(p_103110_);
    }

//	public void render(PoseStack stack, double x, double y, double z, double yaw, double pitch, double roll, double length)
//	{
//        stack.pushPose();
//        stack.translate(x, y, z);
//// 		if(coaster.ERCPosMat != null)
////		{
//// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
////		}
//        stack.mulPose(new Quaternion((float)yaw, 0, -1, 0);
//        stack.mulPose(new Quaternion((float)pitch,1, 0, 0);
//        stack.mulPose(new Quaternion((float)roll, 0, 0, 1);
//
//        stack.scale(1.0F, 1.0F, (float) length);
//        Minecraft.getInstance().getTextureManager().bindForSetup(TextureResource);
//		this.renderModel();
//        stack.popPose();
//	}

    public void setModelNum(int PosNum_org) {
        PosNum = PosNum_org;
        posArray = new Vec3[PosNum_org * 4];
        normalArray = new Vec3[PosNum_org];
        for (int i = 0; i < PosNum * 4; ++i) posArray[i] = new Vec3(0, 0, 0);
        for (int i = 0; i < PosNum; ++i) normalArray[i] = new Vec3(0, 0, 0);
    }

    @Override
    public void construct(int idx, Vec3 Pos, Vec3 Dir, Vec3 Cross, float exParam) {
        int j = idx * 4;
        double t1 = 0.4 + 0.1;
        double t2 = 0.4 - 0.1;

        if (j >= posArray.length) {
            ERC_Logger.warn("ERC_DefaultRailModel : index exception");
            return;
        }

        // 
        posArray[j] = new Vec3(Pos.x - Cross.x * t1,
                Pos.y - Cross.y * t1,
                Pos.z - Cross.z * t1);
        posArray[j + 1] = new Vec3(Pos.x - Cross.x * t2,
                Pos.y - Cross.y * t2,
                Pos.z - Cross.z * t2);
        // E
        posArray[j + 2] = new Vec3(Pos.x + Cross.x * t2,
                Pos.y + Cross.y * t2,
                Pos.z + Cross.z * t2);
        posArray[j + 3] = new Vec3(Pos.x + Cross.x * t1,
                Pos.y + Cross.y * t1,
                Pos.z + Cross.z * t1);

        normalArray[idx] = Dir.cross(Cross).normalize();
    }


    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {
        float turnflag = 0f;
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder bb = tess.getBuilder();
        bb.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < PosNum; ++i) {
            int index = i * 4;
            bb.vertex(posArray[index].x, posArray[index].y, posArray[index].z).uv(0.0f, turnflag).endVertex();
            bb.vertex(posArray[index + 1].x, posArray[index + 1].y, posArray[index + 1].z).uv(1.0f, turnflag).endVertex();
            turnflag = turnflag > 0 ? 0f : 1f;
            //FT Original method call was tess.setNormal. How to replace this?
            //bb.putNormal((float)normalArray[i].x, (float)normalArray[i].y, (float)normalArray[i].z);
        }
        tess.end();
        turnflag = 0f;
        bb.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX);
        for (int i = 0; i < PosNum; ++i) {
            int index = i * 4 + 2;
            bb.vertex(posArray[index].x, posArray[index].y, posArray[index].z).uv(0.0f, turnflag).endVertex();
            bb.vertex(posArray[index + 1].x, posArray[index + 1].y, posArray[index + 1].z).uv(1.0f, turnflag).endVertex();
            turnflag = turnflag > 0 ? 0f : 1f;
            //FT Original method call was tess.setNormal. How to replace this?
            //bb.putNormal((float)normalArray[i].x, (float)normalArray[i].y, (float)normalArray[i].z);
        }
        tess.end();
    }

}