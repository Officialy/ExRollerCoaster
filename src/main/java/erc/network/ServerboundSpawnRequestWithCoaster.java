package erc.network;

import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterConnector;
import erc.item.ERC_ItemCoaster;
import erc.item.ERC_ItemCoasterConnector;
import erc.item.ERC_ItemCoasterMonodentate;
import erc.item.Wrap_ItemCoaster;
import erc.manager.ERC_ModelLoadManager.ModelOptions;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import erc.registry.ERCItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class ServerboundSpawnRequestWithCoaster {

    int itemID;
    int modelID;
    ModelOptions ops = new ModelOptions();
    public final BlockPos pos;
    int parentID = -1;

    public ServerboundSpawnRequestWithCoaster(Wrap_ItemCoaster item, int modelid, ModelOptions op, BlockPos pos) {
        if (item instanceof ERC_ItemCoasterConnector) itemID = 2;
        else if (item instanceof ERC_ItemCoasterMonodentate) itemID = 3;
        else if (item instanceof ERC_ItemCoaster) itemID = 1;
        modelID = modelid;
        ops = op;
        this.pos = pos;
    }

    public ServerboundSpawnRequestWithCoaster(Wrap_ItemCoaster item, int modelid, ModelOptions op, BlockPos pos, int parentid) {
        this(item, modelid, op, pos);
        parentID = parentid;
    }

    public ServerboundSpawnRequestWithCoaster(FriendlyByteBuf buf) {
        itemID = buf.readInt();
        modelID = buf.readInt();
        ops.ReadBuf(buf);
        pos = buf.readBlockPos();
        parentID = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(itemID);
        buf.writeInt(modelID);
        ops.WriteBuf(buf);
        buf.writeBlockPos(pos);
        buf.writeInt(parentID);
    }


    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        final var success = new AtomicBoolean(false);
        ctx.get().enqueueWork(() -> {
            // spawn!
            Level world = ctx.get().getSender().getLevel();
            Wrap_BlockEntityRail tile = (Wrap_BlockEntityRail) world.getBlockEntity(new BlockPos(this.pos));
            Wrap_ItemCoaster item = (Wrap_ItemCoaster) (this.itemID == 1 ? ERCItems.COASTER_ITEM.get() : (this.itemID == 2 ? ERCItems.COASTERCONNECTOR_ITEM.get() : ERCItems.COASTERMONO_ITEM.get()));
            ERC_EntityCoaster entitycoaster = item.getItemInstance(world, tile, this.pos.getX() + 0.5F, this.pos.getY() + 0.6F, this.pos.getZ() + 0.5F);
            entitycoaster.setModelOptions(this.modelID, this.ops);

            if (this.parentID > -1) {
                ERC_EntityCoaster parent = (ERC_EntityCoaster) world.getEntity(this.parentID);
                parent.connectionCoaster((ERC_EntityCoasterConnector) entitycoaster);
                ((ERC_EntityCoasterConnector) entitycoaster).setParent(parent);
            }
            world.addFreshEntity(entitycoaster);
            success.set(true);
        });
        ctx.get().setPacketHandled(true);
        return success.get();
    }
}