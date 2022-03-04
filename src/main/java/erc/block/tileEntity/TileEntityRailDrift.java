package erc.block.tileEntity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import erc.entity.ERC_EntityCoaster;
import erc.gui.container.DefMenu;
import erc.network.ERC_MessageRailStC;
import erc.registry.ERCBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.lwjgl.opengl.GL11;

public class TileEntityRailDrift extends TileEntityRailBase {

    public TileEntityRailDrift(BlockPos pos, BlockState state) {
        super(ERCBlockEntities.RAILDRIFT.get(), pos, state);
        RailTexture = new ResourceLocation("textures/blocks/ice.png");
    }

    @Override
    public void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster) {
        EntityCoaster.ERCPosMat.yaw = EntityCoaster.ERCPosMat.prevYaw;
        EntityCoaster.ERCPosMat.viewYaw = EntityCoaster.ERCPosMat.prevYaw;
    }

    @Override
    public Level getWorldObj() {
        return this.level;
    }

    @Override
    public void SetRailDataFromMessage(ERC_MessageRailStC message) {

    }

    @Override
    public void renderToBuffer(PoseStack stack, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_) {
//        GlStateManager.disableLighting();
        float col = 1.0f;
        RenderSystem.setShaderColor(col, col, col, 1.0F);
        super.render(stack, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
//        GlStateManager.enableLighting();
    }
}
