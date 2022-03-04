package erc.block;

import erc.block.tileEntity.TileEntityRailInvisible;
import erc.block.tileEntity.TileEntityRailNormal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class BlockRailInvisible extends BlockRailBase {

	public BlockRailInvisible(Properties p_49795_) {
		super(p_49795_);
	}

//	@Override
//	public RenderType getBlockLayer()
//	{
//		return RenderType.CUTOUT;
//	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityRailInvisible(pos, state);
	}

}
