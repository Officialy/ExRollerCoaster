package erc.entity;

import java.util.UUID;

import erc.core.ERC_Logger;
import erc.item.ERC_ItemCoasterConnector;
import erc.manager.ERC_CoasterAndRailManager;
import erc.manager.ERC_ManagerCoasterLoad;
import erc.block.tileEntity.TileEntityRailBase;
import erc.network.ERC_MessageCoasterStC;
import erc.network.ERC_MessageRequestConnectCtS;
import erc.network.ERC_PacketHandler;
import erc.registry.ERCItems;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class ERC_EntityCoasterConnector extends ERC_EntityCoaster {

    // private int isnotConnectParentFlag = -1;
    private float distanceToParent = -1;
    public int connectIndex;
    UUID parentuuid;
    ERC_EntityCoaster parent = null;

    // ERC_EntityCoasterConnector next = null;

    public ERC_EntityCoasterConnector(EntityType<? extends ERC_EntityCoaster> type, Level world) {
        super(type, world);
        CoasterType = 1;
        if (world.isClientSide()) {
            setModel(ModelID);
            setModelOptions();
        }
        parent = ERC_CoasterAndRailManager.client_getParentCoaster(world);
        if (parent != null)
            parent.connectionCoaster(this);
    }

    public ERC_EntityCoasterConnector(EntityType<? extends ERC_EntityCoaster> type, Level world, TileEntityRailBase rail, double x, double y, double z) {
        super(type, world, rail, x, y, z);
    }

    @Override
    public Item getItem() {
        return ERCItems.COASTERCONNECTOR_ITEM.get();
    }

    // public void InitConnectorCoaster(int
    // isnotConnectParentFlag_ParentEntityID)
    // {
    // isnotConnectParentFlag = isnotConnectParentFlag_ParentEntityID;
    // }

    public double getSpeed() {
        return Speed;
    }

    public void setSumSpeed(double speed) {
        sumSpeed = speed;
    }

    // public void setConnectParentFlag(int num){isnotConnectParentFlag = num;}
    public void setParent(ERC_EntityCoaster parent) {
        this.parent = parent;
    }

    // return true to prevent any further processing.
    protected boolean requestConnectCoaster(Player player) {
        // cancel if player dont has a connective coaster
        if (player.getMainHandItem() == null) return false;
        if (!(player.getMainHandItem().getItem() instanceof ERC_ItemCoasterConnector)) return false;

        //proxy connection to parent
        if (!canConnectForrowingCoaster()) {
            if (parent != null) {
                if (parent.canConnectForrowingCoaster()) {
                    parent.AnswerRequestConnect(player);
                    return true;
                }
            }
            return false;
        }

        AnswerRequestConnect(player);
        return true;
    }

    protected boolean canConnectForrowingCoaster() {
        return false;
    }

    public void initConnectParamT(float t, float distance) {
        if (parent != null && parent.tlrail != null)
            paramT = t - distance / parent.tlrail.Length;
        if (distanceToParent < 0)
            distanceToParent = distance;
        tlrail = parent.tlrail;
        AdjustParamT();
    }

    @Override
    public void killCoaster(boolean dropflag) {
        // if(worldObj.isClientSide()) return;
        super.killCoaster(dropflag);
        // if(next!=null)next.killCoaster(dropflag);
        if (parent != null)
            parent.killPrevCoasters(dropflag, connectIndex);
        if (parent != null)
            parent.deleteConnectCoaster(this);
    }

    @Override
    public void tick() {
        syncToClient();
        if (updateInit())
            return;

        savePrevData();

        updateParamT();

        if (AdjustParamT())
            return;

        // AdjustParamT();

        // if(seats!=null)for(int i=0; i<seats.length; ++i)seats[i].onUpdate();

        // if(seats!=null)for(int i=0; i<seats.length;
        // ++i)if(seats[i]!=null)seats[i].updateRiderPosition2();
        updateSpeedAndRot();


        // 4
        if (seats != null) {
            for (int i = 0; i < seats.length; ++i)
                if (seats[i] != null) {
                    if (!seats[i].isAddedToWorld() && !level.isClientSide()) level.addFreshEntity(seats[i]);
                    if (seats[i].updateFlag != this.updateFlag) seats[i]._onUpdate();
                }
//    		if(worldObj.isClientSide())ERC_Logger.debugInfo("end coaster onUpdate");
        }

        // 5
        updateFlag = !updateFlag;
    }

    @Override
    protected void syncToClient() {
        if (this.UpdatePacketCounter-- <= 0) {
            UpdatePacketCounter = 100;
            if (!level.isClientSide()) {
                if (tlrail != null) {
                    if (tlrail == null)
                        return;
                    ERC_MessageCoasterStC packet = new ERC_MessageCoasterStC(getId(), this.paramT, this.Speed, tlrail.getPos(), ModelID, CoasterOptions);
                    ERC_PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
                }
            } else {
                if (parent == null) {
                    ERC_MessageRequestConnectCtS packet = new ERC_MessageRequestConnectCtS(Minecraft.getInstance().player.getId(), this.getId());
                    ERC_PacketHandler.INSTANCE.sendToServer(packet);

                    ERC_Logger.info("request sync from client");
                }
            }
        }
    }

    @Override
    protected boolean updateInit() {
        if (parent == null) {
            if (!level.isClientSide()) {
                ERC_ManagerCoasterLoad.searchParent(this.getId(), connectIndex, parentuuid);
            }
            return true;

        }
        if (parent.isRemoved()) {
            killCoaster();
            return true;
        }
        if (tlrail == null) {
            return true;
        }

        return super.updateInit();
    }

    protected void updateParamT() {
        parent.updateParamTFirstOnTick();
        Speed = parent.sumSpeed;
        // paramT += Speed / tlrail.Length;

        tlrail = parent.tlrail;

        if (tlrail == null)
            return;
        paramT = parent.paramT - distanceToParent / tlrail.Length;
        // if(worldObj.isClientSide())ERC_Logger.info("paramT:"+paramT+"..."+this.getClass().getName());
    }

    @Override
    protected void updateSpeedAndRot() {
//		if (AdjustParamT())
//			return;
        super.updateSpeedAndRot();
        // Speed *= 0.9985;
        // Speed += 0.027 * tlrail.CalcRailPosition2(paramT, ERCPosMat,
        // rotationViewYaw, rotationViewPitch,
        // (riddenByEntity instanceof ServerPlayer && worldObj.isClientSide()),
        // CoasterOptions.SeatHeight);
        //
        // zRot = ERCPosMat.viewRoll;
        // getXRot() = ERCPosMat.viewPitch;
        // getYRot() = ERCPosMat.viewYaw;
        //
        // yRotO = fixrot(getYRot(), yRotO);
        // xRotO = fixrot(getXRot(), xRotO);
        // prevzRot = fixrot(zRot, prevzRot);
        //
        // ERCPosMat.prevYaw = fixrot(ERCPosMat.yaw, ERCPosMat.prevYaw);
        // ERCPosMat.prevPitch = fixrot(ERCPosMat.pitch, ERCPosMat.prevPitch);
        // ERCPosMat.prevRoll = fixrot(ERCPosMat.roll, ERCPosMat.prevRoll);
        //
        // tlrail.SpecialRailProcessing(this);
        //
        // // update position by 't'
        // this.setPosition(ERCPosMat.Pos.xCoord, ERCPosMat.Pos.yCoord,
        // ERCPosMat.Pos.zCoord);
    }

    @Override
    public boolean save(CompoundTag nbt) {
        nbt.putFloat("distancetoparent", distanceToParent);
        nbt.putInt("connectidx", connectIndex);
        if (parent != null)
            nbt.putString("parentuuid", parent.getUUID().toString());
        return super.save(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        distanceToParent = nbt.getFloat("distancetoparent");
        connectIndex = nbt.getInt("connectidx");
        parentuuid = UUID.fromString(nbt.getString("parentuuid"));

        ERC_ManagerCoasterLoad.registerChildCoaster(this);
        super.load(nbt);
    }

    public void SyncCoasterMisc_Send(FriendlyByteBuf buf, int flag) {
        switch (flag) {
            // case 1 : //killcoaster super
            case 2: // connect coaster to child
                buf.writeInt((parent == null) ? -1 : parent.getId());
                // -1
                buf.writeInt(connectIndex);
                buf.writeFloat(distanceToParent);
                return;
        }
        super.SyncCoasterMisc_Send(buf, flag);
    }

    public void SyncCoasterMisc_Receive(FriendlyByteBuf buf, int flag) {
        switch (flag) {
            case 2: // connect coaster to parent
                if (parent != null)
                    return;
                int parentid = buf.readInt();
                if (parentid == -1)
                    return;
                int idx = buf.readInt();
                this.connectIndex = idx;
                this.distanceToParent = buf.readFloat();
                ERC_EntityCoaster parent = (ERC_EntityCoaster) level.getEntity(parentid);
                if (parent == null)
                    return;

                parent.connectionCoaster(this);
                return;
        }
        super.SyncCoasterMisc_Receive(buf, flag);
    }

    public void receiveConnectionRequestFromClient(int playerid) {
//	todo ERC_PacketHandler.INSTANCE.sendToAll(new ERC_MessageCoasterMisc(this, 2));

//		ERC_Logger.info("ERC_EntityCoasterConnector::recieveConnectionRequestFromClient, request sync do, packet return");
    }

}
