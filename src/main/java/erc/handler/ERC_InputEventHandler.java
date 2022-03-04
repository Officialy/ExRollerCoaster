package erc.handler;

import com.mojang.blaze3d.platform.InputConstants;
import erc.core.ExRollerCoaster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import erc.core.ERC_Reflection;
import erc.item.ERC_ItemSwitchingRailModel;
import erc.item.Wrap_ItemCoaster;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ExRollerCoaster.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ERC_InputEventHandler {
	
	Minecraft mc;
	
	double wheelsum;
	
	public ERC_InputEventHandler(Minecraft minecraft)
	{
	    mc = minecraft;
	    wheelsum = 0;
	}
	
	@SubscribeEvent
	public void interceptMouseInput(InputEvent.MouseScrollEvent event)
	{
	    if(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342))
	    {
	    	wheelsum = event.getScrollDelta();
	    	ERC_Reflection.setMouseDHweel(0);
	    
		    if(wheelsum != 0)
		    {
				if(mc.player == null)return;
				ItemStack heldItemstack = mc.player.getMainHandItem();
				if(heldItemstack == null)return;
				Item heldItem = heldItemstack.getItem();
				if(heldItem == null)return;
				if(heldItem instanceof Wrap_ItemCoaster)
				{
					((Wrap_ItemCoaster)heldItem).ScrollMouseHweel(wheelsum);
				}
				else if(heldItem instanceof ERC_ItemSwitchingRailModel)
				{
					((ERC_ItemSwitchingRailModel)heldItem).ScrollMouseHweel(wheelsum);
				}
		    }
	    }
	}
}
