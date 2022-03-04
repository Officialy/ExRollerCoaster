package erc.entity;

import java.util.ArrayList;
import java.util.List;

import erc.network.ERC_MessageCoasterMisc;
import erc.network.ERC_MessageCoasterStC;
import erc.network.ERC_PacketHandler;
import erc.registry.ERCEntities;
import erc.registry.ERCItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import erc.core.ExRollerCoaster;
import erc.core.ERC_Logger;
import erc.core.ERC_ReturnCoasterRot;
import erc.item.ERC_ItemCoasterConnector;
import erc.manager.ERC_CoasterAndRailManager;
import erc.manager.ERC_ManagerCoasterLoad;
import erc.manager.ERC_ModelLoadManager;
import erc.manager.ERC_ModelLoadManager.ModelOptions;
import erc.model.ERC_ModelCoaster;
import erc.block.tileEntity.TileEntityRailBase;
import erc.block.tileEntity.TileEntityRailBranch2;
import erc.block.tileEntity.Wrap_BlockEntityRail;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;


public class ERC_EntityCoaster extends Wrap_EntityCoaster {

    protected String entityName;
    protected TileEntityRailBase tlrail;

    protected int CoasterType = -1;
    protected int ModelID = -1;
    protected ERC_ModelCoaster<ERC_EntityCoaster> modelCoaster;


    public ERC_ModelCoaster<ERC_EntityCoaster> getModelRenderer() {
        return modelCoaster;
    }

    private int savex;
    private int savey;
    private int savez;

    public int connectNum;

    public ERC_ReturnCoasterRot ERCPosMat;
    public float paramT;
    public double Speed;
    public double sumSpeed;
    //    public float rotationViewYaw;
//    public float prevRotationViewYaw;
//    public float rotationViewPitch;
//    public float prevRotationViewPitch;
    // T[o[̃pPbgMJEg
    protected int UpdatePacketCounter = 5;
    // TickXVpJE^ۑ
    protected int prevTickCount;

//    public float coasterLength; //AR[X^[̌낻炵

    public ERC_ModelLoadManager.ModelOptions CoasterOptions; // R[X^[̃IvV
    public int seatsNum = 0;
    protected ERC_EntityCoasterSeat[] seats;
    public boolean updateFlag = false;
//    public Entity[] riddenByEntities = new Entity[1];

    // App[^
    private final List<ERC_EntityCoasterConnector> connectCoasterList = new ArrayList<>();
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);

//    public AABB collisionAABB;

    public ERC_EntityCoaster(EntityType<? extends Wrap_EntityCoaster> type, Level world) {
        super(type, world);
        ERCPosMat = new ERC_ReturnCoasterRot();
        CoasterOptions = new ModelOptions();
        ERC_Logger.info("EntityCoaster:construct ... modelID:" + ModelID + ", CoasterType:" + CoasterType);
        ERC_Logger.info("EntityCoaster:construct ... pos init2  :" + getX() + "." + getY() + "." + getZ());
        ERC_Logger.info("EntityCoaster:construct ... aabb  :" + getBoundingBox() + ", width:" + getBbWidth());

    }

    public ERC_EntityCoaster(EntityType<? extends Wrap_EntityCoaster> type, Level world, TileEntityRailBase rail1, double x1, double y1, double z1) {
        super(type, world);
        this.tlrail = rail1;

        this.setPosition(x1, y1, z1);
        this.xo = x1;
        this.yo = y1;
        this.zo = z1;

        CoasterOptions = new ModelOptions();
        this.blocksBuilding = true;
        this.horizontalCollision = false;
        this.verticalCollision = false;
//todo		this.moveCollidedEntities = false;
        this.minorHorizontalCollision = false; //todo might be the above?

        this.setSize(1.7f, 0.4f);
        if (world.isClientSide()) setViewScale(1000f);
//        this.yOffset = 0.0f;//(this.height / 2.0F) - 0.3F;
        prevzRot = zRot = 0f;
        this.setYRot(this.yRotO = 0.0f);
        this.setDeltaMovement(0, 0, 0);

        ERCPosMat = new ERC_ReturnCoasterRot();
        Speed = 0;
        paramT = 0;
        savex = -1;
        savey = -1;
        savez = -1;

        connectNum = 0;
        prevTickCount = -1;

        connectCoasterList.clear();

        CoasterType = 0;
        seats = null;
        seatsNum = -1;

        if (world.isClientSide()) {
            ERC_ManagerCoasterLoad.registerParentCoaster(this);
            if (ERC_CoasterAndRailManager.coastersetY > -1) {
                int x = ERC_CoasterAndRailManager.coastersetX;
                int y = ERC_CoasterAndRailManager.coastersetY;
                int z = ERC_CoasterAndRailManager.coastersetZ;
                Wrap_BlockEntityRail rail = (Wrap_BlockEntityRail) world.getBlockEntity(new BlockPos(x, y, z));
                if (rail == null) {
                    ERC_Logger.warn("ERC_EntityCoaster.constractor : can't get rail... xyz:" + x + "." + y + "." + z);
                    return;
                }
                tlrail = rail.getRail();
                this.setPosition(x + 0.5, y + 1.0, z + 0.5);
            }

            if (ERC_CoasterAndRailManager.saveModelID > -1) {
                setModel(ERC_CoasterAndRailManager.saveModelID);
                setModelOptions();
                ERC_CoasterAndRailManager.saveModelID = -1;
            }
        }

        ERC_Logger.info("EntityCoaster:construct ... pos init   :" + getX() + "." + getY() + "." + getZ());
        ERC_Logger.info("EntityCoaster:construct2 ... aabb  :" + getBoundingBox() + ", width:" + getBbWidth());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_HURT, 0);
//        this.dataWatcher.addObject(17, new Integer(0));
//        this.dataWatcher.addObject(18, new Integer(1));
//        this.dataWatcher.addObject(19, new Float(0.0F));
//        this.dataWatcher.addObject(20, new Integer(0));
//        this.dataWatcher.addObject(21, new Integer(6));
//        this.dataWatcher.addObject(22, Byte.valueOf((byte)0));
    }

    // return true if kill coaster
    protected boolean checkTileEntity() {
        BlockEntity tile = this.level.getBlockEntity(new BlockPos(savex, savey, savez));
        if (!(tile instanceof Wrap_BlockEntityRail)) {
            this.killCoaster();
            return true;
        }
        tlrail = ((Wrap_BlockEntityRail) tile).getRail();
        savex = savey = savez = -1;
        return false;
    }

    protected void setSize(float w, float h) {
        //    	w*=10.0;h*=10.0;
//      todo  if (w != this.width || h != this.height) {
//            this.width = w;// + 40f;
//            this.height = h;

        //            this.boundingBox.maxX = this.boundingBox.minX + (double)this.width;
        //            this.boundingBox.maxZ = this.boundingBox.minZ + (double)this.width;
        //            this.boundingBox.maxY = this.boundingBox.minY + (double)this.height;
        this.setBoundingBox(new AABB(-w / 2 + this.getX(), +h / 2 + this.getY(), -w / 2 + this.getZ(), +w / 2 + this.getX(), +h / 2 + this.getY(), +w / 2 + this.getZ()));

        //            if (this.width > f2 && !this.worldObj.isClientSide())
        //            {
        //                this.moveEntity((double)(f2 - this.width), 0.0D, (double)(f2 - this.width));
        //            }

//        }

    }

    @Override
    public boolean shouldRenderAtSqrDistance(double p_19883_) {
        return true;
    }

    public Item getItem() {
        return ERCItems.COASTER_ITEM.get();
    }

    public int getModelID() {
        return ModelID;
    }

    public void setModelOptions(int modelID, ModelOptions op) {
        if (this.ModelID == modelID) return;
        setModel(modelID);
        setModelOptions(op);
    }

    public void setModel(int id) {
        ModelID = id;
        if (!level.isClientSide()) return;
        if (id < 0) return;
        ERC_Logger.info("EntityCoaster:setModel ... set ModelID : " + id + " -> " + (id % ERC_ModelLoadManager.getModelPackNum(CoasterType)) + " CoasterType:" + CoasterType);
        ModelID = ModelID % ERC_ModelLoadManager.getModelPackNum(CoasterType);
        modelCoaster = ERC_ModelLoadManager.getModel(ModelID, CoasterType);
    }

    protected void setModelOptions() {
        ModelOptions op = ERC_ModelLoadManager.getModelOP(ModelID, CoasterType);
        if (op == null) return;
        setModelOptions(op);
    }

    protected void setModelOptions(ModelOptions op) {
        if (op.SeatNum < 0) return;
        CoasterOptions = op;
        setSize(op.Width, op.Height);
        addSeats(op);
    }

    protected void addSeats(ModelOptions op) {
        seatsNum = op.SeatNum;

        if (!level.isClientSide()) {
            if (seatsNum <= 0) return;
            if (seats == null) seats = new ERC_EntityCoasterSeat[seatsNum];
            else if (seats.length != seatsNum) seats = new ERC_EntityCoasterSeat[seatsNum];

            for (int i = 0; i < seatsNum; ++i) {
                seats[i] = new ERC_EntityCoasterSeat(ERCEntities.COASTERSEAT.get(), this, level, i); //todo seat
                seats[i].setOptions(CoasterOptions, i);
                seats[i].setPosition(getX(), getY(), getZ());
                level.addFreshEntity(seats[i]);
//				seats[i]._onUpdate();
//				ERC_Logger.debugInfo(flag?"***********success":"*********************failed");
            }
        } else {
            if (seats == null) seats = new ERC_EntityCoasterSeat[seatsNum];
            else if (seats.length != seatsNum) seats = new ERC_EntityCoasterSeat[seatsNum];
            for (int i = 0; i < seats.length; ++i) {
                if (seats[i] == null) continue;
                seats[i].setOptions(CoasterOptions, i);
            }
        }
    }

    // NCAgŎꂽEntity̕ߊl
    protected void addSeat(ERC_EntityCoasterSeat seat, int index) {
        if (index < 0) return;
        if (seats == null) {
            ERC_Logger.warn("ERC_EntityCoaster::addSeat, seats is null");
            return;
        } else if (seats.length <= index) return;

        seats[index] = seat;
        seats[index].setOptions(CoasterOptions, index);
        seat.updateFlag = this.updateFlag;
        ERC_Logger.debugInfo("EntityCaoster::addSeat... seatidx:" + index);
    }

    public boolean resetSeat(int idx, ERC_EntityCoasterSeat seat) {
        if (idx < 0 || idx >= seats.length) {
            ERC_Logger.warn("EntityCoaster::resetSeat : out of index");
//			seat.setDead();
            return true;
        }
        seats[idx] = seat;
        return false;
    }

    public ERC_EntityCoasterSeat[] getSeats() {
        return this.seats;
    }
//	public Entity[] getParts()
//	{
//		return this.seats;
//    }

//	@Override
//	protected void setSize(float p_70105_1_, float p_70105_2_){}
//	protected void setSize(float width, float height, float length)
//    {
////        float f2;
//
//        if (width != this.width || height != this.height || length != this.length)
//        {
////            f2 = this.width;
//            this.width = width;
//            this.height = height;
//            this.length = length;
//            this.boundingBox.minX = this.getX() - (double)this.width/2f; 
//            this.boundingBox.minZ = this.getZ() - (double)this.length/2f;
//            this.boundingBox.minY = this.getY() ;
//            this.boundingBox.maxX = this.boundingBox.minX + (double)this.width;
//            this.boundingBox.maxZ = this.boundingBox.minZ + (double)this.length;
//            this.boundingBox.maxY = this.boundingBox.minY + (double)this.height;
//
//            ERC_Logger.info("pos        :"+getX()+"."+getY()+"."+getZ());
//            ERC_Logger.info("boundingMin:"+boundingBox.minX+"."+boundingBox.minY+"."+boundingBox.minZ);
//            ERC_Logger.info("boundingMax:"+boundingBox.maxX+"."+boundingBox.maxY+"."+boundingBox.maxZ);
//        }
//    }


    @Override
    public boolean canCollideWith(Entity p_20303_) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
//        return !this.isDead;
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }

    protected boolean canConnectForrowingCoaster() {
        return true;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset() {
        return (double) this.getEyeHeight() * 0.4; //todo was just height, idk what it was
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (requestConnectCoaster(player)) return InteractionResult.SUCCESS;
//    	if(isRiddenSUSHI(player))return true;
//    	if(!canBeRidden())return true;

//        if (this.riddenByEntity != null && this.riddenByEntity instanceof Player && this.riddenByEntity != player)
//        {
//            return true;
//        }
//        else if (this.riddenByEntity != null && this.riddenByEntity != player)
//        {
//            return true;
//        }
//        else
//        {
//            if (!this.worldObj.isClientSide())
//            {
//            	
////                player.mountEntity(this);
////                mountSeats(player);
//            }
        return InteractionResult.SUCCESS;
//        }
    }

    // return true to prevent any further processing.
    protected boolean requestConnectCoaster(Player player) {
        // AR[X^[vC[Ă炾
        if (player.getMainHandItem() == null) return false;
        if (!(player.getMainHandItem().getItem() instanceof ERC_ItemCoasterConnector)) return false;

        //NbNR[X^[AĂ̂Ƃ
        if (!canConnectForrowingCoaster()) return false;

        AnswerRequestConnect(player);
        return true;
    }

    protected void AnswerRequestConnect(Player player) {
        // ȉẢENbN͂
        //FTThis code is main hand only when it should probably allow offhand too. Problem is calling getActiveHand
        //FTreturns null...
        if (level.isClientSide()) {
            ERC_ItemCoasterConnector itemcc = (ERC_ItemCoasterConnector) player.getMainHandItem().getItem();
            itemcc.setCoaster(tlrail.getBlockPos(), 1);//todo this.getId());
            ERC_CoasterAndRailManager.client_setParentCoaster(this);
        }
        if (!player.isCreative()) player.getMainHandItem().grow(-1);
        player.swing(InteractionHand.MAIN_HAND);
    }

    public void connectionCoaster(ERC_EntityCoasterConnector followCoaster) {
        if (level.isClientSide()) {

        }
        followCoaster.parent = this;
//		if(!connectCoasterList.isEmpty())connectCoasterList.get(connectCoasterList.size()-1).setNextCoasterConnect(followCoaster);
        float length = this.CoasterOptions.Length;
        for (ERC_EntityCoasterConnector c : connectCoasterList) {
            length += c.CoasterOptions.Length;
        }
        if (followCoaster.tlrail == null) followCoaster.tlrail = this.tlrail;
        followCoaster.initConnectParamT(paramT, length);
        connectCoasterList.add(followCoaster);
        ERC_Logger.info("connectEntityIdx:" + followCoaster.connectIndex);
        followCoaster.connectIndex = connectCoasterList.size();
    }

    public void deleteConnectCoaster(ERC_EntityCoasterConnector c) {
        connectCoasterList.remove(c);
    }

    public void clearConnectCoaster() {
        connectCoasterList.clear();
    }

    protected void mountSeats(Player player) {
        player.startRiding(this);
//		player.mountEntity(this.seats[0]);
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean hurt(DamageSource ds, float p_70097_2_) {
        boolean flag = ds.getEntity() instanceof Player;// && ((Player)ds.getEntity()).capabilities.isCreativeMode; todo check if this is right

        if (this.level.isClientSide() || !this.isRemoved()) {
            if (ds.getEntity() instanceof Player) //todo was istruesource, check if this is right
                this.killCoaster(!((Player) ds.getEntity()).isCreative()); //todo check if this is right
            else this.killCoaster();
            return true;
        }

        if (this.isInvulnerableTo(ds) || !this.isRemoved()) {
            return false;
        }

//	    this.setRollingDirection(-this.getRollingDirection());
//	    this.setRollingAmplitude(10);
//	    this.setBeenAttacked();
//	    this.setDamage(this.getDamage() + p_70097_2_ * 10.0F);

        if (flag) {
            if (this.getControllingPassenger() != null) {
                this.getControllingPassenger().startRiding(this);
            }

            this.killCoaster(!((Player) ds.getEntity()).isCreative()); //todo check if this works
            return true;
        }

        return true;
    }

    public void killCoaster() {
        killCoaster(true);
    }

    public void killCoaster(boolean dropflag) {
        if (level.isClientSide()) return;

        if (tlrail != null) tlrail.onDeleteCoaster();
        tlrail = null;
        if (!this.isAlive()) return;

        this.kill();
        ItemStack itemstack = dropflag ? new ItemStack(getItem(), 1) : null;

        if (itemstack != null)
            this.spawnAtLocation(itemstack, 0.0F);

//        if(seats!=null)for(ERC_EntitySecondSeat s : seats)if(s!=null)s.setDead();

        ERC_Logger.info("server coaster is dead : " + this.getId());
        if (!connectCoasterList.isEmpty()) connectCoasterList.get(0).killCoaster(dropflag);
        ERC_PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new ERC_MessageCoasterMisc(this, 1));
    }

    public void killPrevCoasters(boolean dropflag, int idx) {
        List<ERC_EntityCoasterConnector> removelist = new ArrayList<ERC_EntityCoasterConnector>();
        for (ERC_EntityCoasterConnector c : connectCoasterList) {
            if (c.connectIndex > idx) removelist.add(c);
        }
        for (ERC_EntityCoasterConnector c : removelist) {
            c.killCoaster(dropflag);
            connectCoasterList.remove(c);
        }
    }

    protected void killCoaster_Clientside() {
        if (tlrail != null) tlrail.onDeleteCoaster();
        tlrail = null;
        if (!this.isAlive()) return;
        this.kill();
//        for(ERC_EntitySecondSeat s : seats)if(s!=null)s.setDead();
//        ERC_Logger.info("client coaster is dead : "+this.getId());
        if (!connectCoasterList.isEmpty()) connectCoasterList.get(0).killCoaster_Clientside();
    }

    public void onChunkLoad() {
    }

    @Override
    public void tick() {
        super.tick();
//		setDead();
//		if(!worldObj.isClientSide())
//        ERC_Logger.info("update:" + this.getClass().getName() + "   speed:" + Speed);
        syncToClient();
        if (updateInit()) return;
        savePrevData();

        updateParamT();
        AdjustParamT();

//        ERC_Logger.info("update parent pos");

//        ERC_Logger.info("update seats pos");
//    	if(seats!=null)
//		{
//    		for(int i=0; i<seats.length; ++i)if(seats[i]!=null)seats[i].onUpdate(i);
//    		if(worldObj.isClientSide())ERC_Logger.debugInfo("seats id : "+seats[0].getUniqueID().toString());
//		}
        updateSpeedAndRot();
//        ERC_Logger.debugInfo("parent update ");

        // S
        if (seats != null) {
            for (int i = 0; i < seats.length; ++i)
                if (seats[i] != null) {
                    if (!seats[i].isAddedToWorld() && !level.isClientSide()) level.addFreshEntity(seats[i]);
                    if (seats[i].updateFlag != this.updateFlag) seats[i]._onUpdate();
                }
            if (level.isClientSide()) ERC_Logger.debugInfo("end coaster onUpdate");
        }

        // T
        updateFlag = !updateFlag;
        return;
    }

    protected boolean updateInit() {
//    	setDead();
        if (tlrail == null) {
            if (checkTileEntity()) {
                killCoaster();
                return true;
            }
        }
        if (this.getY() < -64.0D || Double.isNaN(this.Speed) || !this.isAlive() || tlrail.isBreak()) {
            if (Double.isNaN(this.Speed)) {
                Speed = -0.1;
                paramT = -0.1f;
//        		tlrail = tlrail.getPrevRailTileEntity().getRail();
                return false;
            }
            this.killCoaster();
            return true;
        }

//        if (this.getRollingAmplitude() > 0)
//        {
//            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
//        }
//        if(this.riddenByEntity == null)
//        {
//        	ERC_BlockRailManager.rotationViewYaw = 0;
//        	ERC_BlockRailManager.rotationViewPitch = 0;
//        	ERC_BlockRailManager.prevRotationViewYaw = 0;
//        	ERC_BlockRailManager.prevRotationViewPitch = 0;
//        }
//        this.killCoaster();return false;
        return false;
    }

    protected void syncToClient() {
        if (this.UpdatePacketCounter-- <= 0) {
            UpdatePacketCounter = 50;
            if (!level.isClientSide() && tlrail != null) {
                ERC_MessageCoasterStC packet = new ERC_MessageCoasterStC(getId(), this.paramT, this.Speed, tlrail.getPos(), ModelID, CoasterOptions);
                ERC_PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
            }
//    		else
//    		{
//				 modelIDppPbg
//				ERC_MessageCoasterCtS packet = new ERC_MessageCoasterCtS(getId(), -100f, -100f, -1, -1, -1, ModelID, CoasterOptions);
//			    ERC_PacketHandler.INSTANCE.sendToServer(packet);
//    		}
        }
    }

    protected void savePrevData() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
//        this.xRotO = this.getXRot();
//        this.yRotO = this.getYRot();
//        this.prevzRot = this.zRot;

        ERCPosMat.prevRoll = ERCPosMat.roll;
        ERCPosMat.prevPitch = ERCPosMat.pitch;
        ERCPosMat.prevYaw = ERCPosMat.yaw;
    }

    protected void updateParamT() {
        updateParamTFirstOnTick();
        paramT += sumSpeed / tlrail.Length;
    }

    protected void updateParamTFirstOnTick() {
        @SuppressWarnings("static-access")
        int tick = ExRollerCoaster.tickEventHandler.getTickcounter();
        if (prevTickCount != tick) {
//    		if(worldObj.isClientSide())ERC_Logger.info("updateparamtfistontick");
            prevTickCount = tick;

            sumSpeed = Speed;
            if (!connectCoasterList.isEmpty()) {
                int num = 1;
                for (ERC_EntityCoasterConnector c : connectCoasterList) {
                    ++num;
                    sumSpeed += c.getSpeed();
                }
                sumSpeed /= num;
                for (ERC_EntityCoasterConnector c : connectCoasterList) {
                    if (c.isAlive())
                        c.setSumSpeed(sumSpeed);
                }
            }
        }
    }

    protected boolean AdjustParamT() {
        if (paramT > 1.0f) {
            if (tlrail == null) return true;
//        	ERC_Logger.info("adjust paramT .before : "+paramT);
            tlrail.onPassedCoaster(this);
            Wrap_BlockEntityRail nextRailWrapper = tlrail.getNextRailTileEntity();
            if (nextRailWrapper != null) {
                TileEntityRailBase nextRail = nextRailWrapper.getRail();
                nextRail.onCoasterEntry(this);
            }

            do {
                Wrap_BlockEntityRail wr = tlrail.getNextRailTileEntity();
                if (wr == null) {
//        			this.killCoaster(); 
                    Speed = -Speed * 0.1; // ]
                    paramT = 0.99f;
                    //if(!world.isClientSide())ERC_Logger.info("Rails aren't connected. Check status of connection. (next)");
                    return false;
                }
                paramT -= 1f;
                paramT = paramT * tlrail.Length;
                TileEntityRailBase next = wr.getRail();
                next.BaseRail.SetPos(tlrail.getPos().getX(), tlrail.getPos().getY(), tlrail.getPos().getZ());
                tlrail = wr.getRail();
                paramT /= tlrail.Length;
            } while (paramT >= 1f);
//        	ERC_Logger.info("adjust paramT .after : "+paramT);

        } else if (paramT < 0f) {
            tlrail.onPassedCoaster(this);
            if (tlrail == null) return true;
//        	ERC_Logger.info("adjust paramT .before : "+paramT);
            do {
                Wrap_BlockEntityRail wr = tlrail.getPrevRailTileEntity();
                if (wr == null) {
//        			this.killCoaster(); 
                    Speed = -Speed * 0.1; // ]
                    paramT = 0.01f;
                    ERC_Logger.info("Rails aren't connected. Check status of connection. (prev)");
                    return false;
                }
                paramT = -paramT * tlrail.Length;
                tlrail = wr.getRail();
                paramT = -paramT / tlrail.Length + 1f;
            } while (paramT < 0f);
//        	ERC_Logger.info("adjust paramT .after : "+paramT);
        }
        return false;
    }

    protected void updateSpeedAndRot() {
        Speed *= 0.9985;
        Speed += 0.027 * tlrail.CalcRailPosition2(paramT, ERCPosMat, ERC_CoasterAndRailManager.rotationViewYaw, ERC_CoasterAndRailManager.rotationViewPitch,
                (this.getControllingPassenger() instanceof ServerPlayer && level.isClientSide()));

//        zRot = ERCPosMat.viewRoll;
//        getXRot() = ERCPosMat.viewPitch;
//        getYRot() = ERCPosMat.viewYaw;

//        yRotO = fixrot(getYRot(), yRotO);
//        xRotO = fixrot(getXRot(), xRotO);
//        prevzRot = fixrot(zRot, prevzRot);

        tlrail.SpecialRailProcessing(this);

        ERCPosMat.prevYaw = fixrot(ERCPosMat.yaw, ERCPosMat.prevYaw);
        ERCPosMat.prevPitch = fixrot(ERCPosMat.pitch, ERCPosMat.prevPitch);
        ERCPosMat.prevRoll = fixrot(ERCPosMat.roll, ERCPosMat.prevRoll);

        this.setPosition(ERCPosMat.pos.x, ERCPosMat.pos.y, ERCPosMat.pos.z);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition(x, y, z);
    }

//    @Override
//    public void positionRider(Entity passenger) {
//		if (riddenByEntity!= null)
//	    {
//	
////			int x = 0 % CoasterOptions.SeatLine;
////			int y = 0 / CoasterOptions.SeatNum;
////			float w = x - (CoasterOptions.SeatLine-1)*0.5f;
////			float h = y - (CoasterOptions.SeatNum-1)*0.5f;
//	//    		w /= width;
//	//    		h /= width;
////			float w = 0;
////			float h = 0;
//			if(ERCPosMat==null)return;
//			if(ERCPosMat.Dir==null)return;
//			if(ERCPosMat.Pitch==null)return;
//	        this.riddenByEntity.setPosition(
//	        		this.getX() + this.ERCPosMat.offsetY.xCoord * this.riddenByEntity.getYOffset(),  
//	        		this.getY() + this.ERCPosMat.offsetY.yCoord * this.riddenByEntity.getYOffset(),
//	        		this.getZ() + this.ERCPosMat.offsetY.zCoord * this.riddenByEntity.getYOffset()
//	        		);
//            this.riddenByEntity.getYRot() = this.getYRot();
//            this.riddenByEntity.getXRot() = this.getXRot();
//            this.riddenByEntity.yRotO = this.yRotO;
//            this.riddenByEntity.xRotO = this.xRotO; 
//	        
//	    }
//    }

    //@Override
    //public void setPositionAndRotation2(double x, double y, double z, float yaw, float pit, int p_70056_9_)
    //{
    //}

    public void setParamFromPacket(float t, double speed, BlockPos pos) {
        Wrap_BlockEntityRail rail = (Wrap_BlockEntityRail) level.getBlockEntity(pos);
        if (rail instanceof TileEntityRailBranch2) return;

        this.setParamT(t);
        this.Speed = speed;
//		if(this instanceof ERC_EntityCoasterConnector)
//			ERC_Logger.info("sync client from server packet : "+paramT);

        if (rail == null) return;
        this.setRail(rail.getRail());
        if (tlrail == null) {
            if (checkTileEntity()) {
                killCoaster();
                return;
            }
        }
    }

    @Override
    public boolean isInWater() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        savex = tag.getInt("railx");
        savey = tag.getInt("raily");
        savez = tag.getInt("railz");
        this.paramT = tag.getFloat("coastert");
        this.Speed = tag.getDouble("coasterSpeed");
        this.connectNum = tag.getInt("connectnum");
        this.seatsNum = tag.getInt("seatsnum");
        seats = new ERC_EntityCoasterSeat[seatsNum];
        if (CoasterOptions == null) CoasterOptions = new ModelOptions();
        CoasterOptions.ReadFromNBT(tag);
        int modelid = tag.getInt("modelid");
        setModelOptions(modelid, CoasterOptions);
        if (this instanceof ERC_EntityCoaster)
            ERC_ManagerCoasterLoad.registerParentCoaster(this);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (this.tlrail == null)
            return;

        tag.putInt("railx", this.tlrail.getPos().getX());
        tag.putInt("raily", this.tlrail.getPos().getY());
        tag.putInt("railz", this.tlrail.getPos().getZ());
        tag.putFloat("coastert", this.paramT);
        tag.putDouble("coasterSpeed", this.Speed);
        tag.putInt("connectnum", connectCoasterList.size());
        tag.putInt("modelid", ModelID);
        tag.putInt("seatsnum", seatsNum);
        CoasterOptions.WriteToNBT(tag);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    public void setRail(TileEntityRailBase rail) {
        this.tlrail = rail;
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    @Override
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(x, y, z);

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            float f = Mth.sqrt((float) (x * x + z * z)); //todo try fastInvSqrt at some point
            this.setYRot((float) (Math.atan2(x, z) * 180.0D / Math.PI)); // todo look below
            this.setXRot((float) (Math.atan2(y, f) * 180.0D / Math.PI)); // todo check old git code to see if this is correct
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }
    }

//	/**
//     * Sets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
//     * 40.
//     */
//    public void setDamage(float p_70492_1_)
//    {
//        this.dataWatcher.updateObject(19, Float.valueOf(p_70492_1_));
//    }
//    /**
//     * Gets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
//     * 40.
//     */
//    public float getDamage()
//    {
//        return this.dataWatcher.getWatchableObjectFloat(19);
//    }


    public void setParamT(float t) {
        this.paramT = t;
//        this.dataWatcher.updateObject(30, Float.valueOf(t));
    }

    public float getParamT() {
        return paramT;
//        return this.dataWatcher.getWatchableObjectFloat(30);
    }

//    /**
//     * Sets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
//     */
//    public void setRollingDirection(int p_70494_1_)
//    {
//        this.dataWatcher.updateObject(18, Integer.valueOf(p_70494_1_));
//    }
//
//    /**
//     * Gets the rolling direction the cart rolls while being attacked. Can be 1 or -1.
//     */
//    public int getRollingDirection()
//    {
//        return this.dataWatcher.getWatchableObjectInt(18);
//    }

    protected float fixrot(float rot, float prevrot) {
        if (rot - prevrot > 180f) prevrot += 360f;
        else if (rot - prevrot < -180f) prevrot -= 360f;
        return prevrot;
    }
//    private float fixrotRoll(float roll, float prevRoll)
//    {
//    	if(roll - prevRoll>160f)prevRoll += 180f;
//        else if(roll - prevRoll<-160f)prevRoll -= 180f;
//    	return prevRoll;
//    }


//    /**
//     * Sets the rolling amplitude the cart rolls while being attacked.
//     */
//    public void setRollingAmplitude(int p_70497_1_)
//    {
//        this.dataWatcher.updateObject(17, Integer.valueOf(p_70497_1_));
//    }
//
//    /**
//     * Gets the rolling amplitude the cart rolls while being attacked.
//     */
//    public int getRollingAmplitude()
//    {
//        return this.dataWatcher.getWatchableObjectInt(17);
//    }

//    /**
//     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
//     */
//    @OnlyIn(Side.CLIENT)
//    public void performHurtAnimation()
//    {
////        this.setRollingDirection(-this.getRollingDirection());
////        this.setRollingAmplitude(10);
////        this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
//    }

    public void SyncCoasterMisc_Send(FriendlyByteBuf buf, int flag) {
        switch (flag) {
            case 1: //killcoaster
                break;
        }
    }

    public void SyncCoasterMisc_Receive(FriendlyByteBuf buf, int flag) {
        switch (flag) {
            case 1:
                killCoaster_Clientside();
                return;
        }
    }

    /**
     * Set the position and rotation values directly without any clamping.
     */
//    @Override
//    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
    //FT HORRENDOUS HACK TO JUST IGNORE MOVEMENT PACKETS
    //FT But it does make the whole thing so much smoother
//		this.setPosition(x, y, z);
//		this.setRotation(yaw, pitch);
//    }
    public TileEntityRailBase getTlrail() {
        return this.tlrail;
    }
}
