package erc.block;

import erc.block.tileEntity.TileEntityRailNormal;
import erc.block.tileEntity.TileEntityRailRedstoneAccelerator;
import erc.network.ERC_MessageRailMiscStC;
import erc.network.ERC_PacketHandler;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

/**
 * @author MOTTY
 *
 */
public class BlockRailRedstoneAccelerator extends BlockRailBase {

    public BlockRailRedstoneAccelerator(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public boolean isSignalSource(BlockState p_60571_) {
        return true;
    }

    public void onBlockAdded(Level world, BlockPos pos, BlockState state)
    {
        if (!world.isClientSide())
        {
        	boolean flag = world.getBestNeighborSignal(pos) == 0;
        	
        	if (flag)
            {
//        		 TileEntityRailRedstoneAccelerator rail = (TileEntityRailRedstoneAccelerator)world.getBlockEntity(x, y, z);
//        		 boolean tgle = rail.getToggleFlag();
             	
//        		 if (flag != tgle)
                 {
//                 	rail.changeToggleFlag();
                    //FT Not powered, so use the higher meta values
                 	world.setBlock(pos, state.setValue(BlockRailBase.DIRECTION,state.getValue(BlockRailBase.DIRECTION)), 2);
//                 	ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageRailMiscStC(rail));
                 	world.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0F, 0F); //ʉH
                 }
            } 
        }
    }

    @Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
    {
		super.setPlacedBy(worldIn, pos, state, placer, stack);
		TileEntityRailRedstoneAccelerator rail = (TileEntityRailRedstoneAccelerator) worldIn.getBlockEntity(pos);
		rail.setToggleFlag(worldIn.getBlockState(pos).getValue(BlockRailBase.DIRECTION) == Direction.DOWN); //todo direction
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_60513_, boolean p_60514_) {
        if (!level.isClientSide())
        {
            boolean poweredNow = level.getBestNeighborSignal(pos) > 0;
            TileEntityRailRedstoneAccelerator rail = (TileEntityRailRedstoneAccelerator)level.getBlockEntity(pos);
            boolean poweredBefore = rail.getToggleFlag();
            if (poweredBefore != poweredNow)
            {
                rail.setToggleFlag(poweredNow);
                level.setBlock(pos, state.setValue(BlockRailBase.DIRECTION,state.getValue(BlockRailBase.DIRECTION)), 2);
                ERC_PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ERC_MessageRailMiscStC(rail));
                level.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0F, 0F); //ʉH
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityRailRedstoneAccelerator(pos, state);
    }

}
