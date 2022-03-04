package erc.network;

import erc.core.ExRollerCoaster;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ERC_PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ExRollerCoaster.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private ERC_PacketHandler() {
    }

    public static void init() {
        int index = 0;
		INSTANCE.messageBuilder(ERC_MessageRailGUICtS.class, index++, NetworkDirection.PLAY_TO_SERVER).encoder(ERC_MessageRailGUICtS::encode).decoder(ERC_MessageRailGUICtS::new).consumer(ERC_MessageRailGUICtS::handle).add();
		INSTANCE.messageBuilder(ERC_MessageRailStC.class, index++, NetworkDirection.PLAY_TO_CLIENT).encoder(ERC_MessageRailStC::encode).decoder(ERC_MessageRailStC::new).consumer(ERC_MessageRailStC::handle).add();
		INSTANCE.messageBuilder(ERC_MessageConnectRailCtS.class, index++, NetworkDirection.PLAY_TO_SERVER).encoder(ERC_MessageConnectRailCtS::encode).decoder(ERC_MessageConnectRailCtS::new).consumer(ERC_MessageConnectRailCtS::handle).add();
		INSTANCE.messageBuilder(ERC_MessageCoasterCtS.class, index++, NetworkDirection.PLAY_TO_SERVER).encoder(ERC_MessageCoasterCtS::encode).decoder(ERC_MessageCoasterCtS::new).consumer(ERC_MessageCoasterCtS::handle).add();
		INSTANCE.messageBuilder(ERC_MessageCoasterStC.class, index++, NetworkDirection.PLAY_TO_CLIENT).encoder(ERC_MessageCoasterStC::encode).decoder(ERC_MessageCoasterStC::new).consumer(ERC_MessageCoasterStC::handle).add();
		INSTANCE.messageBuilder(ERC_MessageRailMiscStC.class, index++, NetworkDirection.PLAY_TO_CLIENT).encoder(ERC_MessageRailMiscStC::encode).decoder(ERC_MessageRailMiscStC::new).consumer(ERC_MessageRailMiscStC::handle).add();
		INSTANCE.messageBuilder(ERC_MessageItemWrenchSync.class, index++, NetworkDirection.PLAY_TO_SERVER).encoder(ERC_MessageItemWrenchSync::encode).decoder(ERC_MessageItemWrenchSync::new).consumer(ERC_MessageItemWrenchSync::handle).add();
		INSTANCE.messageBuilder(ERC_MessageCoasterMisc.class, index++, NetworkDirection.PLAY_TO_CLIENT).encoder(ERC_MessageCoasterMisc::encode).decoder(ERC_MessageCoasterMisc::new).consumer(ERC_MessageCoasterMisc::handle).add();
		INSTANCE.messageBuilder(ERC_MessageCoasterMisc.class, index++, NetworkDirection.PLAY_TO_SERVER).encoder(ERC_MessageCoasterMisc::encode).decoder(ERC_MessageCoasterMisc::new).consumer(ERC_MessageCoasterMisc::handle).add();
		INSTANCE.messageBuilder(ERC_MessageRequestConnectCtS.class, index++, NetworkDirection.PLAY_TO_SERVER).encoder(ERC_MessageRequestConnectCtS::encode).decoder(ERC_MessageRequestConnectCtS::new).consumer(ERC_MessageRequestConnectCtS::handle).add();
        INSTANCE.messageBuilder(ServerboundSpawnRequestWithCoaster.class, index++, NetworkDirection.PLAY_TO_SERVER).encoder(ServerboundSpawnRequestWithCoaster::encode).decoder(ServerboundSpawnRequestWithCoaster::new).consumer(ServerboundSpawnRequestWithCoaster::handle).add();
    }
}
