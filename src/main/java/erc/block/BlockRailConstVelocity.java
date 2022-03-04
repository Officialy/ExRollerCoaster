package erc.block;

import erc.block.tileEntity.TileEntityRailConstVelocity;
import erc.block.tileEntity.TileEntityRailNormal;
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

public class BlockRailConstVelocity extends BlockRailBase {

	public BlockRailConstVelocity(Properties p_49795_) {
		super(p_49795_);
	}

	public void onBlockAdded(Level world, BlockPos pos, BlockState state)
	{
		if (!world.isClientSide())
		{
			boolean flag = world.getBestNeighborSignal(pos) == 0;

			if (flag)
			{
//        		 TileEntityRailConstVelocity rail = (TileEntityRailRConstVelocity)world.getBlockEntity(x, y, z);
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
		TileEntityRailConstVelocity rail = (TileEntityRailConstVelocity) worldIn.getBlockEntity(pos);
		rail.setToggleFlag(worldIn.getBlockState(pos).getValue(BlockRailBase.DIRECTION) == Direction.EAST || worldIn.getBlockState(pos).getValue(BlockRailBase.DIRECTION) == Direction.WEST);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean p_60514_) {
		if (!level.isClientSide())
		{
			boolean poweredNow = level.getBestNeighborSignal(pos) > 0;
			TileEntityRailConstVelocity rail = (TileEntityRailConstVelocity) level.getBlockEntity(pos);
			boolean poweredBefore = rail.getToggleFlag();
			if (poweredBefore != poweredNow)
			{
				rail.setToggleFlag(poweredNow);
				level.setBlock(pos, state.setValue(BlockRailBase.DIRECTION, state.getValue(BlockRailBase.DIRECTION)), 2);
				ERC_PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ERC_MessageRailMiscStC(rail));
				level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundSource.BLOCKS, 0F, 0F, true); //todo might need to change true to false
			}
		}
    }

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileEntityRailConstVelocity(pos, state);
	}

}
