package erc.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import erc.entity.ERC_EntityCoasterSeat;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class ERCMovingSoundRiding extends AbstractTickableSoundInstance {
	private final Player player;
	private final ERC_EntityCoasterSeat seat;

	public ERCMovingSoundRiding(Player p_i45106_1_, ERC_EntityCoasterSeat p_i45106_2_)
	{
	    super(SoundEvents.MINECART_RIDING, SoundSource.NEUTRAL);
	    this.player = p_i45106_1_;
	    this.seat = p_i45106_2_;
	    //this.attenuationType = ISound.AttenuationType.NONE;
	    this.looping = true;
	    this.delay = 0;
	    //FT May be wrong
	    this.pitch = 10.7f;
	}

	public void tick()
	{
		if ((!this.seat.isAlive()) && (this.player.isPassenger()) && (this.player.getVehicle() == this.seat))
		{
			float f = seat.parent==null ? 0 : ((float) this.seat.parent.Speed);
			if (f >= 0.01D) 
			{
				this.volume = (Mth.clamp((float) Math.pow(Math.abs(f), 3.0D) * 0.5F, 0.0F, 1.0F));
				this.pitch = 1.3f;
			} 
			else 
			{
				this.volume = 0.0F;
			}
		} 
		else
		{
			this.stop();
		}
	}
}