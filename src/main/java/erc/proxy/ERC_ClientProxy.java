package erc.proxy;

import erc.block.tileEntity.*;
import erc.core.ExRollerCoaster;
import erc.entity.ERC_EntityCoasterDoubleSeat;
import erc.entity.ERC_EntityCoasterMonodentate;
import erc.model.ModelLoader;
import erc.registry.ERCBlockEntities;
import erc.registry.ERCEntities;
import erc.renderer.*;
import erc.core.ERCConstants;
import erc.handler.ERC_ClientTickEventHandler;
import erc.handler.ERC_InputEventHandler;
import erc.handler.ERC_RenderEventHandler;
import erc.handler.ERC_TickEventHandler;
import erc.manager.ERC_ModelLoadManager;
import erc.manager.ERC_ModelLoadPlan;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;

public class ERC_ClientProxy {

	public static void init()
	{
//		FMLClientHandler.instance().addModAsResource( (new customResourceLoader() ).get());

//		OBJLoader.INSTANCE.addDomain(ERC_CONST.DOMAIN);
//		OBJLoader.INSTANCE.addDomain(ERC_CONST.D_AM);
//		ModelLoaderRegistry.registerLoader(new ModelLoader());

		// ftHR[X^[o^
		String defaultModel = ERCConstants.DOMAIN+":models/coaster.obj";
		String defaultModel_c = ERCConstants.DOMAIN+":models/coaster_connect.obj";
		String defaultTex = ERCConstants.DOMAIN+":models/coaster.png";
		String defaultIcon_c = "erc:coaster_c";
		String defaultIcon = "erc:coaster";
		
		ERC_ModelLoadPlan main = new ERC_ModelLoadPlan(defaultModel, defaultTex, defaultIcon);
		ERC_ModelLoadPlan connect = new ERC_ModelLoadPlan(defaultModel_c, defaultTex, defaultIcon_c);
		ERC_ModelLoadPlan mono = new ERC_ModelLoadPlan(defaultModel, defaultTex, defaultIcon);
		ERC_ModelLoadPlan inv = new ERC_ModelLoadPlan(ERCConstants.DOMAIN+":models/coaster_inv.obj", defaultTex, "erc:coaster_inv");
		main.setSeatOffset(0, 0f, 0.4f, 0f);
		connect.setSeatOffset(0, 0f, 0.4f, 0f);
		mono.setSeatOffset(0, 0f, 0.4f, 0f);
		
		main.setCoasterMainData(1.5f, 2.2f, 2.0f, true);
		connect.setCoasterMainData(1.5f, 1.0f, 3.0f, true);
		mono.setCoasterMainData(1.5f, 2.2f, 2.0f, true);
		
		main.setSeatSize(0, 0.8f);
		connect.setSeatSize(0, 0.8f);
		mono.setSeatSize(0, 0.8f);
		
		ERC_ModelLoadManager.registerCoaster(main);
		ERC_ModelLoadManager.registerConnectionCoaster(connect);
		ERC_ModelLoadManager.registerMonoCoaster(mono);
		
		inv.setCoasterMainData(1.6f, 1.0f, 2.0f, true);
		inv.setSeatOffset(0, 0, 2.0f, 0.6f);
		inv.setSeatRotation(0, 0, 0f, (float)Math.PI);
		ERC_ModelLoadManager.registerCoaster(inv);
		ERC_ModelLoadManager.registerConnectionCoaster(inv);
		
		///// 
		ERC_ModelLoadPlan head = new ERC_ModelLoadPlan("erc:models/coaster_d.obj", defaultTex, "erc:coaster_d");
		ERC_ModelLoadPlan sub = new ERC_ModelLoadPlan("erc:models/coaster_d_c.obj", defaultTex, "erc:coaster_dc");
		head.setSeatNum(2);
		head.setCoasterMainData(1.9f, 3f, 3f, true);
		head.setSeatSize(0, 0.9f);
		head.setSeatSize(1, 0.9f);
		head.setSeatOffset(0, -0.5f, 0.5f, 0);
		head.setSeatOffset(1, 0.5f, 0.5f, 0);
		ERC_ModelLoadManager.registerCoaster(head);
		
		sub.setSeatNum(2);
		sub.setCoasterMainData(1.9f, 3f, 3f, true);
		sub.setSeatSize(0, 0.9f);
		sub.setSeatSize(1, 0.9f);
		sub.setSeatOffset(0, -0.5f, 0.5f, 0);
		sub.setSeatOffset(1, 0.5f, 0.5f, 0);
		ERC_ModelLoadManager.registerConnectionCoaster(sub);

		// XṼfƂ
//		entitySUSHI.clientInitSUSHI();

		ERC_ModelLoadManager.init();
	}

	public static void registerModels()
	{
		//Specify item models
//		ModelLoader.setCustomModelResourceLocation(ItemBasePipe, 0, new ModelResourceLocation(ERC_Core.MODID+":railpipe", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemWrench, 0, new ModelResourceLocation(ERC_Core.MODID+":wrench_c1", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemStick, 0, new ModelResourceLocation(ERC_Core.MODID+":itemwrenchplaceblock", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemCoaster, 0, new ModelResourceLocation(ERC_Core.MODID+":coaster", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemCoasterConnector, 0, new ModelResourceLocation(ERC_Core.MODID+":coasterconnector", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemCoasterMono, 0, new ModelResourceLocation(ERC_Core.MODID+":coastermono", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemSwitchRail, 0, new ModelResourceLocation(ERC_Core.MODID+":switchrailmodel", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemSUSHI, 0, new ModelResourceLocation(ERC_Core.MODID+":sushi", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemSmoothAll, 0, new ModelResourceLocation(ERC_Core.MODID+":itemsmoothall", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemRailBranch, 0, new ModelResourceLocation(ERC_Core.MODID+":railbranch", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemRailConst, 0, new ModelResourceLocation(ERC_Core.MODID+":railconst", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemRailDetect, 0, new ModelResourceLocation(ERC_Core.MODID+":raildetector", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemRailDrift, 0, new ModelResourceLocation(ERC_Core.MODID+":raildrift", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemRailInvisible, 0, new ModelResourceLocation(ERC_Core.MODID+":railinvisible", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemRailNonGravity, 0, new ModelResourceLocation(ERC_Core.MODID+":railnongravity", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemRailNormal, 0, new ModelResourceLocation(ERC_Core.MODID+":railnormal", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemRailRedAccel, 0, new ModelResourceLocation(ERC_Core.MODID+":railredaccel", "inventory"));
//		ModelLoader.setCustomModelResourceLocation(ItemEntryTicket, 0, new ModelResourceLocation(ERC_Core.MODID+":entryticket", "inventory"));
	}
}