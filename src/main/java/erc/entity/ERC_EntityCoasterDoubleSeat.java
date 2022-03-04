package erc.entity;

import erc.block.tileEntity.TileEntityRailBase;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

public class ERC_EntityCoasterDoubleSeat extends ERC_EntityCoaster {

	ERC_EntityCoasterSeat secondseat;

	public ERC_EntityCoasterDoubleSeat(EntityType<? extends ERC_EntityCoaster> type, Level world)
	{
		super(type, world);
		this.setSize(1.4f, 0.6f);
	}
	
	public ERC_EntityCoasterDoubleSeat(EntityType<? extends ERC_EntityCoaster> type, Level world, TileEntityRailBase tile, double x, double y, double z) {
		super(type, world, tile, x, y, z);
	}
	
	@Override
	public boolean canBeRidden()
    {
        return true; // true : 
    }

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
    	if(!canBeRidden())return InteractionResult.FAIL;
    	Entity THIS = this;
    	Entity riddenbyentity = this.getControllingPassenger();
    	if(riddenbyentity != null)
    	{
    		THIS = secondseat;
    		riddenbyentity = secondseat.getControllingPassenger();
    	}

        if (riddenbyentity != null && riddenbyentity instanceof Player && riddenbyentity != player)
        {
            return InteractionResult.SUCCESS;
        }

        else if (riddenbyentity != null && riddenbyentity != player)
        {
            return InteractionResult.FAIL;
        }
        else
        {
            if (!this.level.isClientSide())
            {
                player.startRiding(THIS);
            }

            return InteractionResult.SUCCESS;
        }
    }
    
    @Override
    public void tick()
    {
    	super.tick();
    }

	@Nullable
	@Override
	public PartEntity<?>[] getParts() {
		PartEntity<?>[] ret = new PartEntity[1];
//		todo ret[0] = secondseat = new ERC_EntityCoasterSeat(this.level);
		return ret;
	}


	public double getYOffset() {
		return (this.getEyeHeight() / 2.0F) - 0.3F; //todo was height
	}
}
