package erc.handler;

import erc.core.ExRollerCoaster;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExRollerCoaster.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ERC_TickEventHandler {
	
	private static int tickcounter = 0;

	@SubscribeEvent
	public void onTickEvent(TickEvent.ServerTickEvent event)
	{
		if (event.phase == TickEvent.Phase.START) 
		{
			setTickcounter(getTickcounter() + 1);
//			ERC_ManagerPrevTickCoasterSeatSetPos_server.update();
		}
		if (event.phase == TickEvent.Phase.END)
		{
			
		}
	}

	public static int getTickcounter() {
		return tickcounter;
	}

	public static void setTickcounter(int tickcounter) {
		ERC_TickEventHandler.tickcounter = tickcounter;
	}
	
}
