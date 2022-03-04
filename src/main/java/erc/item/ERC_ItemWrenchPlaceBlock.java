package erc.item;

import erc.network.ERC_MessageItemWrenchSync;
import erc.network.ERC_PacketHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.entity.player.Player;

import java.util.function.Predicate;

public class ERC_ItemWrenchPlaceBlock extends Item {

	public ERC_ItemWrenchPlaceBlock(Properties p_41383_) {
		super(p_41383_);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand handIn) {
		if(world.isClientSide())
		{
			Block placedBlock = Blocks.DIRT;
			ItemStack placedBlockItemStack = new ItemStack(Item.BY_BLOCK.get(placedBlock));
			
			boolean iscreative = player.isCreative();
			if(!player.getInventory().contains(placedBlockItemStack) && !iscreative)
				return InteractionResultHolder.fail(player.getMainHandItem());
	
			double pit = Math.cos(Math.toRadians(player.getXRot()));
			int x = (int) Math.floor(player.getX() - Math.sin(Math.toRadians(player.getYRot()))*2*pit);
			int y = (int) Math.floor(player.getY() - Math.sin(Math.toRadians(player.getXRot()))*2);
			int z = (int) Math.floor(player.getZ() + Math.cos(Math.toRadians(player.getYRot()))*2*pit);
			BlockPos pos = new BlockPos(x, y, z);
			
			// ubNݒuł邩`FbN
			Block b = world.getBlockState(pos).getBlock();
			boolean canPlaceBlock = b == Blocks.AIR || b == Blocks.WATER || b == Fluids.FLOWING_WATER.getFlowing().defaultFluidState().createLegacyBlock().getBlock();
			
			if(canPlaceBlock)
			{
//	    		if(!iscreative)player.getInventory().clearMatchingItems(placedBlockItemStack.getItem(), -1, 1, null);
	    		world.playLocalSound((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, SoundEvents.GRASS_STEP, SoundSource.BLOCKS, 1.0F, 1.0F, false);
	    		player.swing(handIn);
//	    		ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(2,x,y,z));
			}
			return InteractionResultHolder.pass(player.getMainHandItem());
			
		}
		return super.use(world, player, handIn);
	}
	
	public boolean placeBlockAt(ItemStack stack, Player player, Level world, BlockPos pos, Block block)
    {
		if( (world.getBlockState(pos).getBlock() != Blocks.AIR) )
			return false;
    	if (!world.setBlock(pos, block.defaultBlockState(), 3))
    	{
    		return false;
    	}
//    	ERC_Logger.info("place block");
    	if (world.getBlockState(pos).getBlock() == block)
    	{
//			block.setPlacedBy(world, pos, block.defaultBlockState(), player, stack);
			world.setBlockAndUpdate(pos, block.defaultBlockState());
    	}
    	return true;
    }

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		Block placedBlock = Blocks.DIRT;
		Item placedBlockItem = Item.BY_BLOCK.get(placedBlock);
		
		boolean iscreative = context.getPlayer().isCreative();
		if(!context.getPlayer().getInventory().contains(new ItemStack(placedBlockItem)) && !iscreative)
			return InteractionResult.FAIL;

		double pit = Math.cos(Math.toRadians(context.getPlayer().getXRot()));
		int x = (int) Math.floor(context.getPlayer().getX() - Math.sin(Math.toRadians(context.getPlayer().getYRot()))*2*pit);
		int y = (int) Math.floor(context.getPlayer().getY() - Math.sin(Math.toRadians(context.getPlayer().getXRot()))*2);
		int z = (int) Math.floor(context.getPlayer().getZ() + Math.cos(Math.toRadians(context.getPlayer().getYRot()))*2*pit);
		BlockPos newPos = new BlockPos(x, y, z);
		
		// ubNݒuł邩`FbN
		Block b = context.getLevel().getBlockState(newPos).getBlock();
		boolean canPlaceBlock = (b == Blocks.AIR) || (b == Blocks.WATER) || (b == Fluids.FLOWING_WATER.getFlowing().defaultFluidState().createLegacyBlock().getBlock());
		
		if(canPlaceBlock)
		{
    		if(!iscreative)context.getPlayer().getInventory().clearOrCountMatchingItems((Predicate<ItemStack>) placedBlockItem, -1, null); //todo 1 was before null, casting might crash too
			context.getLevel().playSound(context.getPlayer(), context.getClickedPos(), SoundEvents.GRASS_STEP, SoundSource.BLOCKS, 1.0F, 1.0F);

    		ERC_PacketHandler.INSTANCE.sendToServer(new ERC_MessageItemWrenchSync(2, new BlockPos(x,y,z)));
		}
        return InteractionResult.SUCCESS;
	}

}
