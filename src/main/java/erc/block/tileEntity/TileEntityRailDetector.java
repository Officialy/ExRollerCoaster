package erc.block.tileEntity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;
import erc.gui.container.DefMenu;
import erc.network.ERC_MessageRailMiscStC;
import erc.network.ERC_MessageRailStC;
import erc.network.ERC_PacketHandler;
import erc.registry.ERCBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;
import org.lwjgl.opengl.GL11;

public class TileEntityRailDetector extends TileEntityRailBase {

    boolean outputFlag;

    public TileEntityRailDetector(BlockPos pos, BlockState state) {
        super(ERCBlockEntities.RAILDETECTOR.get(), pos, state);
        outputFlag = false;
//		RailTexture = new ResourceLocation("textures/blocks/stone.png");
    }

    public void changeOutput() {
        outputFlag = !outputFlag;
    }

    public void setOutput(boolean flag) {
        outputFlag = flag;
    }

    public boolean getFlag() {
        return outputFlag;
    }

    public void SpecialRailProcessing(ERC_EntityCoaster coaster) {
        if (!outputFlag && !(coaster instanceof ERC_EntityCoasterConnector)) {
            // o͊Jn
            Block block = level.getBlockState(this.worldPosition).getBlock();

            outputFlag = true;
			ERC_PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ERC_MessageRailMiscStC(this));
            BlockPos downPos = this.worldPosition.below();
            level.neighborChanged(this.worldPosition, block, this.worldPosition.above());
            level.neighborChanged(this.worldPosition, block, downPos);
            level.neighborChanged(this.worldPosition, block, this.worldPosition.east());
            level.neighborChanged(this.worldPosition, block, this.worldPosition.west());
            level.neighborChanged(this.worldPosition, block, this.worldPosition.south());
            level.neighborChanged(this.worldPosition, block, this.worldPosition.north());
            level.neighborChanged(downPos, block, this.worldPosition);
            level.neighborChanged(downPos, block, downPos.below());
            level.neighborChanged(downPos, block, downPos.east());
            level.neighborChanged(downPos, block, downPos.west());
            level.neighborChanged(downPos, block, downPos.south());
            level.neighborChanged(downPos, block, downPos.north());
            level.playSound(null, this.worldPosition.offset(0.5, 0.5, 0.5), SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 0.3F, 0.6F);
        }
    }

    @Override
    public void onPassedCoaster(ERC_EntityCoaster coaster) {
        if (!(coaster instanceof ERC_EntityCoasterConnector)) {
            stopOutput();
        }
    }

    public void onDeleteCoaster() {
        stopOutput();
    }

    private void stopOutput() {
        // o͒~
        outputFlag = false;
        Block block = level.getBlockState(this.worldPosition).getBlock();
        BlockPos downPos = this.worldPosition.below();
        level.neighborChanged(this.worldPosition, block, this.worldPosition.above());
        level.neighborChanged(this.worldPosition, block, downPos);
        level.neighborChanged(this.worldPosition, block, this.worldPosition.east());
        level.neighborChanged(this.worldPosition, block, this.worldPosition.west());
        level.neighborChanged(this.worldPosition, block, this.worldPosition.south());
        level.neighborChanged(this.worldPosition, block, this.worldPosition.north());
        level.neighborChanged(downPos, block, this.worldPosition);
        level.neighborChanged(downPos, block, downPos.below());
        level.neighborChanged(downPos, block, downPos.east());
        level.neighborChanged(downPos, block, downPos.west());
        level.neighborChanged(downPos, block, downPos.south());
        level.neighborChanged(downPos, block, downPos.north());
        level.playSound(null, this.worldPosition, SoundEvents.UI_BUTTON_CLICK, SoundSource.BLOCKS, 0.3F, 0.6F);

    }

    public void setDataToByteMessage(FriendlyByteBuf buf) {
        buf.writeBoolean(this.outputFlag);
    }

    public void getDataFromByteMessage(FriendlyByteBuf buf) {
        outputFlag = buf.readBoolean();
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
//		GlStateManager.disableLighting();
        float col = 1.0f;
        RenderSystem.setShaderColor(col, col, col, 1.0F);
        super.render(stack, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
//		GlStateManager.enableLighting();
    }

}