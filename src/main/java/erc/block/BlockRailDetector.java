package erc.block;

import erc.block.tileEntity.TileEntityRailDetector;
import erc.block.tileEntity.TileEntityRailNormal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlockRailDetector extends BlockRailBase {

	public BlockRailDetector(Properties p_49795_) {
		super(p_49795_);
	}

	@Override
	public boolean isSignalSource(BlockState p_60571_) {
		return true;
	}

	public int getWeakPower(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
    	TileEntityRailDetector rail = (TileEntityRailDetector)blockAccess.getBlockEntity(pos);
        return rail.getFlag() ? 15 : 0;
    }

    public int getStrongPower(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side)
    {
//        return (p_149748_1_.getBlockMetadata(p_149748_2_, p_149748_3_, p_149748_4_) & 8) == 0 ? 0 : (p_149748_5_ == 1 ? 15 : 0);
    	TileEntityRailDetector rail = (TileEntityRailDetector)blockAccess.getBlockEntity(pos);
        return rail.getFlag() ? 15 : 0;
    }

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityRailDetector(pos, state);
	}

}
