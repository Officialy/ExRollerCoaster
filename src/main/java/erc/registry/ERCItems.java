package erc.registry;

import erc.core.ExRollerCoaster;
import erc.item.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ERCItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExRollerCoaster.MODID);

    public static RegistryObject<Item> BASEPIPE_ITEM = ITEMS.register("railpipe", () -> new Item(new Item.Properties().tab(ExRollerCoaster.ERC_Tab)));
    public static RegistryObject<Item> WRENCH_ITEM = ITEMS.register("wrench", () -> new ERC_ItemWrench(new Item.Properties().tab(ExRollerCoaster.ERC_Tab)));
    public static RegistryObject<Item> COASTER_ITEM = ITEMS.register("coaster", () -> new ERC_ItemCoaster(new Item.Properties().tab(ExRollerCoaster.ERC_Tab)));
    public static RegistryObject<Item> COASTERCONNECTOR_ITEM = ITEMS.register("coasterconnector", () -> new ERC_ItemCoasterConnector(new Item.Properties().tab(ExRollerCoaster.ERC_Tab)));
    public static RegistryObject<Item> COASTERMONO_ITEM = ITEMS.register("coastermono", () -> new ERC_ItemCoasterMonodentate(new Item.Properties().tab(ExRollerCoaster.ERC_Tab)));
    public static RegistryObject<Item> SWITCHRAIL = ITEMS.register("switchrail", () -> new ERC_ItemSwitchingRailModel(new Item.Properties().tab(ExRollerCoaster.ERC_Tab)));
    public static RegistryObject<Item> SUSHI_ITEM = ITEMS.register("itemsushi", () -> new itemSUSHI(new Item.Properties().tab(ExRollerCoaster.ERC_Tab)));
    public static RegistryObject<Item> ENTRYTICKET_ITEM = ITEMS.register("entryticket", () -> new itemEntryTicket(new Item.Properties().tab(ExRollerCoaster.ERC_Tab)));
    public static RegistryObject<Item> PLACE_BLOCK = ITEMS.register("wrenchplaceblock", () -> new ERC_ItemWrenchPlaceBlock(new Item.Properties().tab(ExRollerCoaster.ERC_Tab)));
    public static RegistryObject<Item> SMOOTHALL_ITEM = ITEMS.register("itemsmoothall", () -> new ERC_ItemSmoothAll(new Item.Properties().tab(ExRollerCoaster.ERC_Tab)));

}