package erc.registry;

import erc.block.tileEntity.Wrap_BlockEntityRail;
import erc.core.ExRollerCoaster;
import erc.gui.container.DefMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public interface ERCMenus {

    DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, ExRollerCoaster.MODID);

    RegistryObject<MenuType<DefMenu>> RAIL_MENU = register("mail_box", (IContainerFactory<DefMenu>) (windowId, playerInventory, data) -> {
        Wrap_BlockEntityRail mailBoxBlockEntity = (Wrap_BlockEntityRail) playerInventory.player.level.getBlockEntity(data.readBlockPos());
        return new DefMenu(windowId, playerInventory, mailBoxBlockEntity);
    });

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String key, MenuType.MenuSupplier<T> supplier) {
        MenuType<T> type = new MenuType<>(supplier);
        return CONTAINERS.register(key, () -> type);
    }

}