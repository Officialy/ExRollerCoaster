package erc.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class itemSUSHI extends Item {

	public itemSUSHI(Properties p_41383_) {
		super(p_41383_);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    	if (!world.isClientSide())
    	{
//    	todo	Entity e = new entitySUSHI(world, player.getX()+0.5, player.getY()+0.8, player.getZ()+0.5);
//    		Entity e = new entityPartsTestBase(world,x+0.5,y+1.5,z+0.5);
//    	todo	world.addFreshEntity(e);
    	}
    	player.getMainHandItem().grow(-1);
    	return InteractionResultHolder.pass(player.getItemInHand(hand));
    }
}
	