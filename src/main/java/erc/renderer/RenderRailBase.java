package erc.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Vector3d;
import erc.core.ERCConstants;
import erc.manager.ERC_CoasterAndRailManager;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class RenderRailBase<T extends Wrap_BlockEntityRail> implements BlockEntityRenderer<T> {

    //	private static final ResourceLocation TEXTURE  ;
    private static final ResourceLocation TEXTUREguiarraw = new ResourceLocation(ERCConstants.DOMAIN, "textures/gui/ringarrow.png");
    //new ResourceLocation(", "textures/blocks/pink.png");

    public RenderRailBase(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(Wrap_BlockEntityRail t, float p_112308_, PoseStack stack, MultiBufferSource bufferSource, int p_112311_, int p_112312_) {
        Tesselator tessellator = Tesselator.getInstance();
        Minecraft.getInstance().getTextureManager().bindForSetup(t.getDrawTexture());
        stack.pushPose();
        RenderSystem.disableCull(); // JOOFF

        stack.translate(t.getPos().getX() + 0.5, t.getPos().getY() + 0.5, t.getPos().getZ() + 0.5);

//        tessellator.setColorOpaque_F(1.0F, 1.0F, 1.0F);
        VertexConsumer vertexconsumer2 = bufferSource.getBuffer(RenderType.lightning());
        t.renderToBuffer(stack, vertexconsumer2, 1, OverlayTexture.NO_OVERLAY,1.0F, 1.0F, 1.0F, 1.0F);

//        stack.translate(t.x, t.y, t.z);
//        stack.translate(0.5, y-t.y, z-t.z);

        if (t == ERC_CoasterAndRailManager.clickedTileForGUI) {
            DrawRotaArrow(tessellator, t);
        }
//        DrawArrow(tessellator, t.vecUp);
        RenderSystem.enableCull(); // JOON
        stack.popPose();
    }

    @SuppressWarnings("unused")
    private void DrawArrow(Tesselator tess, Vector3d vec) {
        BufferBuilder bb = tess.getBuilder();
        bb.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_TEX);
        bb.vertex(0.2d, 0d, 0.2d).uv(0.0f, 0.0f).endVertex();
        bb.vertex(vec.x * 3d, vec.y * 3d, vec.z * 3d).uv(0.0f, 0.0f).endVertex();
        bb.vertex(-0.2d, 0d, -0.2d).uv(0.0f, 0.0f).endVertex();
        tess.end();
    }

    // GUI\̉]`p
    public void DrawRotaArrow(Tesselator tess, Wrap_BlockEntityRail tile) {
        Minecraft.getInstance().getTextureManager().bindForSetup(TEXTUREguiarraw);
        Vec3 d = tile.getRail().BaseRail.vecDir;
        Vec3 u = tile.getRail().BaseRail.vecUp;
        Vec3 p = d.cross(u);

        d = d.normalize();
        u = u.normalize();
        p = p.normalize();

        float s = 2.0f; // s

        // yaw axis
        RenderSystem.setShaderColor(1.0F, 0.0F, 0.0F, 1.0F);
        BufferBuilder bb = tess.getBuilder();
        bb.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX);
        bb.vertex((d.x + p.x) * s, (d.y + p.y) * s, (d.z + p.z) * s).uv(0.0f, 0.0f).endVertex();
        bb.vertex((d.x - p.x) * s, (d.y - p.y) * s, (d.z - p.z) * s).uv(1.0f, 0.0f).endVertex();
        bb.vertex((-d.x + p.x) * s, (-d.y + p.y) * s, (-d.z + p.z) * s).uv(0.0f, 1.0f).endVertex();
        bb.vertex((-d.x - p.x) * s, (-d.y - p.y) * s, (-d.z - p.z) * s).uv(1.0f, 1.0f).endVertex();
        tess.end();
        // pitch axis
        s = 1.5f;
        RenderSystem.setShaderColor(0.0F, 1.0F, 0.0F, 1.0F);
        bb.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX);
        bb.vertex((u.x + d.x) * s, (u.y + d.y) * s, (u.z + d.z) * s).uv(0.0f, 0.0f).endVertex();
        bb.vertex((u.x - d.x) * s, (u.y - d.y) * s, (u.z - d.z) * s).uv(1.0f, 0.0f).endVertex();
        bb.vertex((-u.x + d.x) * s, (-u.y + d.y) * s, (-u.z + d.z) * s).uv(0.0f, 1.0f).endVertex();
        bb.vertex((-u.x - d.x) * s, (-u.y - d.y) * s, (-u.z - d.z) * s).uv(1.0f, 1.0f).endVertex();
        tess.end();
        // roll axis
        s = 1.0f;
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        bb.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX);
        bb.vertex((u.x + p.x) * s, (u.y + p.y) * s, (u.z + p.z) * s).uv(0.0f, 0.0f).endVertex();
        bb.vertex((u.x - p.x) * s, (u.y - p.y) * s, (u.z - p.z) * s).uv(1.0f, 0.0f).endVertex();
        bb.vertex((-u.x + p.x) * s, (-u.y + p.y) * s, (-u.z + p.z) * s).uv(0.0f, 1.0f).endVertex();
        bb.vertex((-u.x - p.x) * s, (-u.y - p.y) * s, (-u.z - p.z) * s).uv(1.0f, 1.0f).endVertex();
        tess.end();
    }

    @Override
    public boolean shouldRender(Wrap_BlockEntityRail p_173568_, Vec3 p_173569_) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(Wrap_BlockEntityRail p_112306_) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 384;
    }
//FT Fixes rails disappearing from certain viewpoints
//    @Override
//    public boolean isGlobalRenderer(Wrap_BlockEntityRail te) {
        //Use this line to only use the fix on fancy graphics, if this turns out to be a performance issue.
        //Though it is probably better to make a renderer for all rails, in a RenderWorldLastEvent handler.
        //return settings.fancyGraphics;
//        return true;
//    }
    
}
