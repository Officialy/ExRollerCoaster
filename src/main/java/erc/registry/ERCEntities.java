package erc.registry;

import erc.core.ExRollerCoaster;
import erc.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ERCEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, ExRollerCoaster.MODID);

    public static final RegistryObject<EntityType<ERC_EntityCoaster>> COASTER = registerEntityType("coaster", () -> EntityType.Builder.of((ERC_EntityCoaster::new), MobCategory.MISC));
    public static final RegistryObject<EntityType<ERC_EntityCoasterMonodentate>> COASTERMONO = registerEntityType("coastermono", () -> EntityType.Builder.of((ERC_EntityCoasterMonodentate::new), MobCategory.MISC));
    public static final RegistryObject<EntityType<ERC_EntityCoasterDoubleSeat>> COASTERDOUBLE = registerEntityType("coasterdouble", () -> EntityType.Builder.of((ERC_EntityCoasterDoubleSeat::new), MobCategory.MISC));
    public static final RegistryObject<EntityType<ERC_EntityCoasterSeat>> COASTERSEAT = registerEntityType("coasterseat", () -> EntityType.Builder.of((ERC_EntityCoasterSeat::new), MobCategory.MISC));
    public static final RegistryObject<EntityType<ERC_EntityCoasterConnector>> COASTERCONNECT = registerEntityType("coasterconnect", () -> EntityType.Builder.of((ERC_EntityCoasterConnector::new), MobCategory.MISC));
//		EntityRegistry.registerModEntity(new ResourceLocation(ERC_Core.MODID+":sushi"), entitySUSHI.class, "erc:sushi", eid++, this, 200, 50, true);

    /**
     * Registers an entity type.
     *
     * @param name    The registry name of the entity type
     * @param factory The factory used to create the entity type builder
     * @return A RegistryObject reference to the entity type
     */
    private static <T extends Entity> RegistryObject<EntityType<T>> registerEntityType(final String name, final Supplier<EntityType.Builder<T>> factory) {
        return ENTITIES.register(name, () -> factory.get().build(new ResourceLocation(ExRollerCoaster.MODID, name).toString())
        );
    }
}
