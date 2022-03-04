/**
 * TODO
 * COASTER GRAVITY?
 * Coaster spams the server when rails are not connected. Not great but Motty's decision.
 * * Commented out for now (2, ERC_EntityCoaster:745)
 * Lots of keys and colours not supported in .mtls
 * Slight sushi texture bug; probably because of the out-of-range UVs in the original
 * Coaster texture doesn't look like the original
 */
package erc.core;

import erc.gui.RailScreen;
import erc.handler.ERC_ClientTickEventHandler;
import erc.handler.ERC_InputEventHandler;
import erc.handler.ERC_RenderEventHandler;
import erc.handler.ERC_TickEventHandler;
import erc.model.ModelLoader;
import erc.network.ERC_PacketHandler;
import erc.proxy.ERC_ClientProxy;
import erc.registry.*;
import erc.renderer.RenderCoaster;
import erc.renderer.RenderCoasterSeat;
import erc.renderer.RenderRailBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ExRollerCoaster.MODID)
@Mod.EventBusSubscriber(modid = ExRollerCoaster.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExRollerCoaster {
    public static final String MODID = ERCConstants.DOMAIN;
    public static final String VERSION = "1.5.1";

    //proxy////////////////////////////////////////
//	@SidedProxy(clientSide = "erc.proxy.ERC_ClientProxy", serverSide = "erc.proxy.ERC_ServerProxy")

//	public static int blockFerrisSupporterRenderID;

    //GUI/////////////////////////////////////////
    public static ExRollerCoaster INSTANCE;
    //    public static Item sampleGuiItem;

//    public static final int GUIID_FerrisConstructor = 1;
//    public static final int GUIID_FerrisBasketConstructor = 2;
//    public static final int GUIID_FerrisCore = 3;

    ////////////////////////////////////////////////////////////////
    //Creative Tab
    public static CreativeModeTab ERC_Tab = new CreativeModeTab("ExRC") {
        @Override
        public ItemStack makeIcon() {
            return ERCItems.COASTER_ITEM.get().getDefaultInstance();
        }
    };

    ////////////////////////////////////////////////////////////////
    // TickEventProxy
    public static ERC_TickEventHandler tickEventHandler = null;

    ////////////////////////////////////////////////////////////////
    public ExRollerCoaster() {
        ERC_Logger.info("Start preInit");
        Minecraft mc = Minecraft.getInstance();
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        //Register Stuff
        ERCBlocks.BLOCKS.register(modEventBus);
        ERCBlocks.ITEMS.register(modEventBus);
        ERCItems.ITEMS.register(modEventBus);
        ERCEntities.ENTITIES.register(modEventBus);
        ERCBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ERCMenus.CONTAINERS.register(modEventBus);

        ExRollerCoaster.tickEventHandler = new ERC_ClientTickEventHandler(mc);
        MinecraftForge.EVENT_BUS.register(new ERC_TickEventHandler());
        MinecraftForge.EVENT_BUS.register(ExRollerCoaster.tickEventHandler);
        MinecraftForge.EVENT_BUS.register(new ERC_InputEventHandler(mc));
        MinecraftForge.EVENT_BUS.register(new ERC_RenderEventHandler());

        ERC_Logger.info("End preInit");
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        ERC_Logger.info("Start Init");

        ExRollerCoaster.tickEventHandler = new ERC_TickEventHandler();
        MinecraftForge.EVENT_BUS.register(ExRollerCoaster.tickEventHandler);
        event.enqueueWork(ERC_PacketHandler::init);
        ERC_Logger.info("End Init");
    }

    public void clientSetup(final FMLClientSetupEvent event) {
//        ERC_ClientProxy.init();
        ERC_ClientProxy.init();

        MenuScreens.register(ERCMenus.RAIL_MENU.get(), RailScreen::new);

        ItemBlockRenderTypes.setRenderLayer(ERCBlocks.RAIL_NORMAL.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ERCBlocks.RAIL_BRANCH.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ERCBlocks.RAIL_CONST.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ERCBlocks.RAIL_DETECTOR.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ERCBlocks.RAIL_DRIFT.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ERCBlocks.RAIL_INVISIBLE.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ERCBlocks.RAIL_NONGRAVITY.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ERCBlocks.RAIL_REDACCEL.get(), RenderType.cutout());
    }


    @SubscribeEvent
    public static void onRegisterRenderer(EntityRenderersEvent.RegisterRenderers event) {
        // TileEntityRender̓o^
//		RenderRailBase tileRenderer = new RenderRailBase();
        event.registerBlockEntityRenderer(ERCBlockEntities.NORMAL_RAIL.get(), RenderRailBase::new);
        event.registerBlockEntityRenderer(ERCBlockEntities.ACCELERATOR_RAIL.get(), RenderRailBase::new);
        event.registerBlockEntityRenderer(ERCBlockEntities.RAILCONSTVEL.get(), RenderRailBase::new);
        event.registerBlockEntityRenderer(ERCBlockEntities.RAILDETECTOR.get(), RenderRailBase::new);
        event.registerBlockEntityRenderer(ERCBlockEntities.RAILDRIFT.get(), RenderRailBase::new);
        event.registerBlockEntityRenderer(ERCBlockEntities.RAILBRANCH.get(), RenderRailBase::new);
        event.registerBlockEntityRenderer(ERCBlockEntities.INVISIBLE.get(), RenderRailBase::new);
        event.registerBlockEntityRenderer(ERCBlockEntities.NONGRAVITY.get(), RenderRailBase::new);

//		ERC_RenderEntityCoasterFactory renderer = new ERC_RenderEntityCoasterFactory();

        // EntityRender
        event.registerEntityRenderer(ERCEntities.COASTER.get(), RenderCoaster::new);
//		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterMonodentate.class, renderer);
        event.registerEntityRenderer(ERCEntities.COASTERSEAT.get(), RenderCoasterSeat::new);
//		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterDoubleSeat.class, renderer);
//		RenderingRegistry.registerEntityRenderingHandler(ERC_EntityCoasterConnector.class, renderer);
//		RenderingRegistry.registerEntityRenderingHandler(entitySUSHI.class, new renderEntitySUSIHIFactory());

    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(new ResourceLocation(MODID, "obj"), new ModelLoader());

        ERC_ClientProxy.registerModels();
    }

    public static ExRollerCoaster getInstance() {
        return INSTANCE;
    }

}
