package erc.handler;

import com.mojang.math.Quaternion;
import erc.core.ExRollerCoaster;
import net.minecraft.client.Timer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import erc.core.ERC_Logger;
import erc.entity.ERC_EntityCoasterSeat;
import erc.sound.ERCMovingSound;
import erc.sound.ERCMovingSoundRiding;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;

@Mod.EventBusSubscriber(modid = ExRollerCoaster.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ERC_RenderEventHandler {
	

	ERC_EntityCoasterSeat getCoaster(LivingEntity target)
	{
		if (target.getVehicle() == null) {
			return null;
		}
		if (!(target.getVehicle() instanceof ERC_EntityCoasterSeat)) {
			return null;
		}
		return (ERC_EntityCoasterSeat)target.getVehicle();
	}

  	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void renderPre(RenderLivingEvent.Pre event)
	{
		if (event.isCanceled()) {
			return;
		}
		ERC_EntityCoasterSeat coaster;
		if ((coaster = getCoaster(event.getEntity())) == null) {
			return;
		}
		event.getPoseStack().pushPose();

		Timer timer = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getInstance(), "f_1728");
	    float partialTicks = timer.partialTick;
	    
	    float yaw = coaster.parent.ERCPosMat.getFixedYaw(partialTicks);
	    float pitch = coaster.parent.ERCPosMat.getFixedPitch(partialTicks);
	    float roll = coaster.parent.ERCPosMat.getFixedRoll(partialTicks) + (float)Math.toDegrees(coaster.getRotZ());
	    
//        event.entity.yBodyRot = yaw;
//        event.entity.yHeadRot = yaw;
	    Entity theplayer = Minecraft.getInstance().player;
	    Entity e = event.getEntity();
	    double x = theplayer.xo+(theplayer.getX()-theplayer.xo)*partialTicks - (e.xo+(e.getX()-e.xo)*partialTicks);
	    double y = theplayer.yo+(theplayer.getY()-theplayer.yo)*partialTicks - (e.yo+(e.getY()-e.yo)*partialTicks);
	    double z = theplayer.zo+(theplayer.getZ()-theplayer.zo)*partialTicks - (e.zo+(e.getZ()-e.zo)*partialTicks);
//	    double x = event.entity.xo+(event.entity.getX()-event.entity.xo)*partialTicks;
//	    double y = event.entity.yo+(event.entity.getY()-event.entity.yo)*partialTicks;
//	    double z = event.entity.zo+(event.entity.getZ()-event.entity.zo)*partialTicks;
		event.getPoseStack().translate(-x,-y,-z);
		event.getPoseStack().mulPose(new Quaternion(yaw, 0.0F, -1.0F, 0.0F));
		event.getPoseStack().mulPose(new Quaternion(pitch, 1.0F, 0.0F, 0.0F));
		event.getPoseStack().mulPose(new Quaternion(roll, 0.0F, 0.0F, 1.0F));
		event.getPoseStack().mulPose(new Quaternion(yaw, 0.0F, 1.0F, 0.0F));
		event.getPoseStack().translate(x,y,z);
	}

	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void renderPost(RenderLivingEvent.Post event)
	{
		@SuppressWarnings("unused")
		ERC_EntityCoasterSeat coaster;
		if ((coaster = getCoaster(event.getEntity())) == null) {
			return;
		}
		event.getPoseStack().popPose();
	}


	@SubscribeEvent
	public void ridingErc(LivingEvent.LivingUpdateEvent event)
	{
		Minecraft mc = Minecraft.getInstance();
	    if (!(event.getEntity() instanceof Player)) return;

		Player player = (Player)event.getEntity();
		Entity ridingEntity = player.getVehicle();
	    String key = "RideERC";
	    if (player.getPersistentData().contains(key))
	    {
	    	if ((ridingEntity == null) || (!(ridingEntity instanceof ERC_EntityCoasterSeat)))
	    	{
	    		player.getPersistentData().remove(key);
	    	}
	    }
	    else if ((ridingEntity != null) && ((ridingEntity instanceof ERC_EntityCoasterSeat)))
	    {
	    	mc.getSoundManager().play(new ERCMovingSoundRiding(player, (ERC_EntityCoasterSeat)ridingEntity));
	    	mc.getSoundManager().play(new ERCMovingSound(player, (ERC_EntityCoasterSeat)ridingEntity));
	    	player.getPersistentData().putBoolean(key, true);
	    	ERC_Logger.debugInfo("sound update");
	    }
	}
}
