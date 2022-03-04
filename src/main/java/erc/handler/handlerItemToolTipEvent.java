package erc.handler;

import erc.core.ExRollerCoaster;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

@Mod.EventBusSubscriber(modid = ExRollerCoaster.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class handlerItemToolTipEvent {
	
	@SubscribeEvent
	public void onRenderItemText(ItemTooltipEvent event)
	{
			event.getToolTip().add((Component) event.getItemStack().getItem().getRegistryName());
	}
}
