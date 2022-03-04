package erc.item;

import erc.block.tileEntity.TileEntityRailBase;
import erc.core.ERC_Logger;
import erc.gui.container.DefMenu;
import erc.network.ERC_MessageConnectRailCtS;
import erc.network.ERC_MessageItemWrenchSync;
import erc.network.ERC_PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import erc.block.BlockRailBase;
import erc.manager.ERC_CoasterAndRailManager;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;

public class ERC_ItemWrench extends Item {

    int mode = 0;
    static final int modenum = 2;
    final String[] ModeStr = {"Connection mode", "Adjustment mode"};

    public ERC_ItemWrench(Item.Properties properties) {
        super(properties);
/*todo        this.addPropertyOverride(new ResourceLocation("mode"), new IItemPropertyGetter() {
            public float apply(ItemStack stack, @Nullable Level worldIn, @Nullable LivingEntity entityIn) {
                return (float) mode;
            }
        });
        this.addPropertyOverride(new ResourceLocation("phase"), new IItemPropertyGetter() {
            public float apply(ItemStack stack, @Nullable Level worldIn, @Nullable LivingEntity entityIn) {
                return ERC_CoasterAndRailManager.isPlacedRail() ? 1.0F : 0.0F;
            }
        });*/
    }

    public boolean placeBlockAt(ItemStack stack, Player player, Level world, BlockPos pos, Block block) {
        Block convblock = world.getBlockState(pos).getBlock();
        if ((convblock != Blocks.AIR) && (convblock != Blocks.WATER) && (convblock != Fluids.FLOWING_WATER.getFlowing().defaultFluidState().createLegacyBlock().getBlock()))
            return false;

        if (!world.setBlock(pos, block.defaultBlockState(), 3)) {
            return false;
        }
        ERC_Logger.info("place block");
        if (world.getBlockState(pos).getBlock() == block) {
            block.setPlacedBy(world, pos, block.defaultBlockState(), player, stack);
//            todo world.scheduleBlockUpdate(pos, block, 0, 0);
            world.setBlockAndUpdate(pos, block.defaultBlockState()); //todo check if this is right / works
        }
        return true;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (context.getPlayer().isCrouching()) return InteractionResult.FAIL;

        if (BlockRailBase.isBlockRail(context.getLevel().getBlockState(context.getClickedPos()).getBlock())) {
            if (context.getLevel().isClientSide()) {
                switch (mode) {
                    case 0:
                        if (!ERC_CoasterAndRailManager.isPlacedPrevRail())
                            ERC_CoasterAndRailManager.SetPrevData(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ());
                        else {
                            ERC_MessageConnectRailCtS packet = new ERC_MessageConnectRailCtS(ERC_CoasterAndRailManager.prevX, ERC_CoasterAndRailManager.prevY, ERC_CoasterAndRailManager.prevZ, context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ());
                            ERC_PacketHandler.INSTANCE.sendToServer(packet);
                            ERC_Logger.info("connection : " + "." + ERC_CoasterAndRailManager.prevX + "." + ERC_CoasterAndRailManager.prevY + "." + ERC_CoasterAndRailManager.prevZ + " -> " + context.getClickedPos());
                            ERC_CoasterAndRailManager.ResetData();
                        }
                        break;
                    case 1:
                        Wrap_BlockEntityRail tile = (Wrap_BlockEntityRail) context.getLevel().getBlockEntity(context.getClickedPos());
                        ERC_CoasterAndRailManager.OpenRailGUI(tile.getRail());
                        ERC_Logger.warn("save rail to manager : " + tile.getRail().getClass().getName());
                        break;
                }

                ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(mode, context.getClickedPos()));
                return InteractionResult.FAIL;
            }

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (BlockRailBase.isBlockRail(level.getBlockState(player.eyeBlockPosition()).getBlock())) { //todo check eyeblockposition
            return InteractionResultHolder.success(player.getMainHandItem());
        }
        if (player.isCrouching()) {
            if (level.isClientSide()) {    //client
                mode = (++mode) % modenum;
                ERC_CoasterAndRailManager.ResetData(); // [h`FWŋL
                player.displayClientMessage(new TextComponent(ModeStr[mode]), true);
            }
            return InteractionResultHolder.success(player.getMainHandItem());
        }
        return InteractionResultHolder.fail(player.getMainHandItem());
    }
}
