package erc.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.obj.OBJModel;
import org.lwjgl.opengl.GL11;

import erc.math.ERC_MathHelper;

public class ERC_ModelAddedRail extends Wrap_RailRenderer {

    private final OBJModel.ModelSettings modelRail;
    private final ResourceLocation TextureResource;
    private int ModelNum;
    private Vec3[] pos;
    private Vec3[] rot;
    private float[] Length;

    public ERC_ModelAddedRail(OBJModel.ModelSettings Obj, ResourceLocation Tex) {
        super(null);
        modelRail = Obj;
        TextureResource = Tex;
    }

    private void renderModel() {
        //modelRail.renderAll();
    }

    public void render(PoseStack stack, double x, double y, double z, double yaw, double pitch, double roll, double length) {
        stack.pushPose();
        stack.translate(x, y, z);
// 		if(coaster.ERCPosMat != null)
//		{
// 			GL11.glMultMatrix(coaster.ERCPosMat.rotmat);
//		}
        stack.mulPose(new Quaternion((float) yaw, 0, -1, 0));
        stack.mulPose(new Quaternion((float) pitch, 1, 0, 0));
        stack.mulPose(new Quaternion((float) roll, 0, 0, 1));

        stack.scale(1.0F, 1.0F, (float) length);
        Minecraft.getInstance().getTextureManager().bindForSetup(TextureResource);
        this.renderModel();
        stack.popPose();
    }

    public void setModelNum(int PosNum_org) {
        ModelNum = PosNum_org - 1;
        pos = new Vec3[ModelNum];
        rot = new Vec3[ModelNum];
        Length = new float[ModelNum];
        for (int i = 0; i < ModelNum; ++i) pos[i] = new Vec3(0, 0, 0);
        for (int i = 0; i < ModelNum; ++i) rot[i] = new Vec3(0, 0, 0);
    }

    @Override
    public void construct(int idx, Vec3 Pos, Vec3 Dir, Vec3 Cross, float exParam) {
        if (idx >= ModelNum) return;
        // ʒu
        pos[idx] = Pos;
        // px
        Vec3 crossHorz = new Vec3(0, 1, 0).cross(Dir);
        Vec3 dir_horz = new Vec3(Dir.x, 0, Dir.z);
        rot[idx] = new Vec3(-Math.toDegrees(Math.atan2(Dir.x, Dir.z)),
                Math.toDegrees(ERC_MathHelper.angleTwoVec3(Dir, dir_horz) * (Dir.y > 0 ? -1f : 1f)),
                Math.toDegrees(ERC_MathHelper.angleTwoVec3(Cross, crossHorz) * (Cross.y > 0 ? 1f : -1f)));
        //
        Length[idx] = exParam;
    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {
        for (int i = 0; i < ModelNum; ++i) {
            render(stack, pos[i].x, pos[i].y, pos[i].z, rot[i].x, rot[i].y, rot[i].z, Length[i]);
        }
    }

}