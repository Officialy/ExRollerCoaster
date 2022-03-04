package erc.entity;

import erc.core.ERC_Logger;
import erc.block.tileEntity.TileEntityRailDrift;
import erc.manager.ERC_ModelLoadManager;
import erc.math.ERC_MathHelper;
import erc.network.ERC_MessageCoasterMisc;
import erc.network.ERC_PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import erc.manager.ERC_CoasterAndRailManager;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class ERC_EntityCoasterSeat extends Wrap_EntityCoaster {

    private static final EntityDataAccessor<Integer> SEAT_INDEX = SynchedEntityData.defineId(ERC_EntityCoasterSeat.class, EntityDataSerializers.INT); //was VARINT
    private static final EntityDataAccessor<Float> OFFSET_X = SynchedEntityData.defineId(ERC_EntityCoasterSeat.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> OFFSET_Y = SynchedEntityData.defineId(ERC_EntityCoasterSeat.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> OFFSET_Z = SynchedEntityData.defineId(ERC_EntityCoasterSeat.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROT_X = SynchedEntityData.defineId(ERC_EntityCoasterSeat.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROT_Y = SynchedEntityData.defineId(ERC_EntityCoasterSeat.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ROT_Z = SynchedEntityData.defineId(ERC_EntityCoasterSeat.class, EntityDataSerializers.FLOAT);

    public ERC_EntityCoaster parent;
    private int UpdatePacketCounter = 4;
    boolean canRide = true;
    public boolean updateFlag = false;
    public boolean waitUpdateRiderFlag = false;
    private float prevyBodyRot;
    //part of Options -> this.dataManager
//	int seatIndex = -1;
//	public float offsetX;
//	public float offsetY;
//	public float offsetZ;
//	public float rotX;
//	public float rotY;
//	public float rotZ;

    public ERC_EntityCoasterSeat(EntityType<? extends Entity> type, Level level) {
        super(type, level);
        setSize(1.1f, 0.8f);
//		spawnControl = true;

//		forceSpawn = true;
//        ERC_ManagerPrevTickCoasterSeatSetPos.addSeat(this);

        ERC_Logger.info("***seat create, x:" + getX() + ", y:" + getY() + ", z:" + getZ());
    }

    public ERC_EntityCoasterSeat(EntityType<? extends Entity> type, ERC_EntityCoaster parent, Level level, int i) {
        this(type, level);
        this.parent = parent;
        setSeatIndex(i);
    }

    public void setOptions(ERC_ModelLoadManager.ModelOptions op, int idx) {
        if (op == null) return;
        if (op.offsetX == null) return;
        if (op.offsetX.length <= idx) return;
        if (level.isClientSide()) return;
        setSize(op.size[idx], op.size[idx]);
        setOffsetX(op.offsetX[idx]);
        setOffsetY(op.offsetY[idx]);
        setOffsetZ(op.offsetZ[idx]);
        setRotX(op.rotX[idx]);
        setRotY(op.rotY[idx]);
        setRotZ(op.rotZ[idx]);
        canRide = op.canRide;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SEAT_INDEX, -1);
        this.entityData.define(OFFSET_X, 0f);
        this.entityData.define(OFFSET_Y, 0f);
        this.entityData.define(OFFSET_Z, 0f);
        this.entityData.define(ROT_X, 0f);
        this.entityData.define(ROT_Y, 0f);
        this.entityData.define(ROT_Z, 0f);
    }

    protected void setSize(float w, float h) {
//    	w*=10.0;h*=10.0;

//      todo  if (w != this.width || h != this.height) {
//            this.width = w;// + 40f;
//            this.height = h;
        this.setBoundingBox(new AABB(-w / 2 + this.getX(), +h / 2 + this.getY(), -w / 2 + this.getZ(), +w / 2 + this.getX(), +h / 2 + this.getY(), +w / 2 + this.getZ()));
//        }
    }

    public boolean canBeCollidedWith() {
        return true;
    }

    public boolean attackEntityFrom(DamageSource ds, float p_70097_2_) {
        if (parent == null) return true;
        return parent.hurt(ds, p_70097_2_);
//    	parent.setDead();
//    	this.setPosition(getX(), getY(), getZ());
//    	return true;
    }

    public boolean canBeRidden() {
        if (level.isClientSide()) return false;
        return canRide; // true : 
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (parent == null) return InteractionResult.FAIL;
        if (parent.requestConnectCoaster(player)) return InteractionResult.FAIL;
//    todo    if (isRiddenSUSHI(player)) return InteractionResult.FAIL;
        if (requestRidingMob(player)) return InteractionResult.FAIL;
        if (!canBeRidden()) return InteractionResult.FAIL;

        //Ă@{@vC[Ă@{@ENbNvC[ƈႤvC[Ă
        Entity passenger = this.getControllingPassenger();
        if (passenger != null && passenger instanceof Player && passenger != player) {
            return InteractionResult.FAIL;
        }
        //Ă@{@ENbNvC[ȊỎĂ
        else if (passenger != null && passenger != player) {
            //낷
            passenger.stopRiding(); //dismountRidingEntity
            return InteractionResult.FAIL;
        }
        //Ă@
        else if (passenger != null) {
            return InteractionResult.FAIL;
        } else {
            if (!this.level.isClientSide()) {
                ERC_CoasterAndRailManager.resetViewAngles();
                player.startRiding(this);
            }
            return InteractionResult.SUCCESS;
        }
    }

/* todo sushi   protected boolean isRiddenSUSHI(Player player) {
        if (player.getMainHandItem() == null) return false;
        if (player.getMainHandItem().getItem() instanceof itemSUSHI) {
            if (!level.isClientSide()) {
                entitySUSHI e = new entitySUSHI(level, getX(), getY(), getZ());
                level.addFreshEntity(e);
                e.startRiding(this);
                if (!player.isCreative()) player.getMainHandItem().grow(-1);
            }
            player.swing(player.getUsedItemHand());
            return true;
        }
        return false;
    }*/

    protected boolean requestRidingMob(Player player) {
        if (level.isClientSide()) return false;
        ItemStack is = player.getMainHandItem();
        if (is == null) return false;
        /*todo if (is.getItem() instanceof ItemMonsterPlacer) {
            Entity entity = ItemMonsterPlacer.spawnCreature(level, ItemMonsterPlacer.getNamedIdFrom(is), getX(), getY(), getZ());
            entity.startRiding(this);
            if (!player.isCreative()) is.grow(-1);
            player.swing(player.getUsedItemHand());
            return true;
        }*/
        if (is.getItem() instanceof LeadItem) {
            double d0 = 7.0D;
            @SuppressWarnings("unchecked")
            List<Mob> list = level.getEntitiesOfClass(Mob.class, new AABB(getX() - d0, getY() - d0, getZ() - d0, getX() + d0, getY() + d0, getZ() + d0));
            if (list != null) {
                Iterator<Mob> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Mob LivingEntity = iterator.next();

                    if (LivingEntity.isLeashed() && LivingEntity.getLeashHolder() == player) {
                        LivingEntity.startRiding(this);
                        LivingEntity.dropLeash(true, !player.isCreative());
                        player.swing(player.getUsedItemHand());
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void kill() {
//		ERC_Logger.debugInfo("seat is dead ... id:"+this.getId());
        super.kill();
    }

    @Override
    public void tick() {
//		if(worldObj.isClientSide())ERC_Logger.debugInfo("end seat onUpdate");
//		setDead();
        if (updateInit()) return;
        if (updateFlag == parent.updateFlag) {
//			ERC_Logger.debugInfo("seat stay");
        }    // QDҋ@
        else {
//			ERC_Logger.debugInfo("seat update");
            _onUpdate();                        // UDeゾXV
        }
        updateFlag = !updateFlag;
    }

    public void _onUpdate() {
//		if(worldObj.isClientSide())ERC_Logger.debugInfo("end seat _onUpdate");
//		if(updateInit())return;
        syncToClient();
        savePrevData();
        double ox = getOffsetX();
        double oy = getOffsetY();
        double oz = getOffsetZ();
        this.setPosition(
                parent.getX() + parent.ERCPosMat.offsetX.x * ox + parent.ERCPosMat.offsetY.x * oy + parent.ERCPosMat.offsetZ.x * oz,
                parent.getY() + parent.ERCPosMat.offsetX.y * ox + parent.ERCPosMat.offsetY.y * oy + parent.ERCPosMat.offsetZ.y * oz,
                parent.getZ() + parent.ERCPosMat.offsetX.z * ox + parent.ERCPosMat.offsetY.z * oy + parent.ERCPosMat.offsetZ.z * oz);

        if (waitUpdateRiderFlag) updateRiderPosition2(this.getControllingPassenger());
    }

    protected void syncToClient() {
        if (this.UpdatePacketCounter-- <= 0) {
            UpdatePacketCounter = 40;
            if (!level.isClientSide()) {
                if (parent != null) {
//				todo	ERC_MessageCoasterMisc packet = new ERC_MessageCoasterMisc(this,4);
//				todo	ERC_PacketHandler.INSTANCE.sendToAll(packet);

//					parent.resetSeat(getSeatIndex(), this);
//					ERC_Logger.info("Server teach client parentid");
                } else {
//					if(parent.resetSeat(getSeatIndex(), this))
//						setDead();
                }
            } else // client
            {
//				parent.resetSeat(getSeatIndex(), this);
            }
        }
    }

    protected boolean updateInit() {
        if (parent == null) {
            if (!level.isClientSide()) {
//				if(searchParent())return false;
//				else 
                ERC_Logger.debugInfo("seat log : parent is null.");
                if (!isRemoved()) kill();
            }
            return true;
        }
        if (parent.isRemoved()) {
            if (!level.isClientSide()) if (!isRemoved()) kill();
            return true;
        }
        if (!level.isClientSide() && getSeatIndex() == -1) {
            if (!isRemoved()) kill();
            return true;
        }
        return false;
    }

    protected void savePrevData() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

//       todo this.prevzRot = this.zRot;
    }

    public boolean searchParent() {
        return false;
//		double x = getX() - getOffsetX();
//		double y = getY() - getOffsetY();
//		double z = getZ() - getOffsetZ();
//		double s = Math.max(Math.abs(getOffsetX()), Math.abs(getOffsetY()));
//		s = Math.max(s, Math.abs(getOffsetZ()));
//		@SuppressWarnings("unchecked")
//		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, AABB.getBoundingBox(x-s, y-s, z-s, x+s, y+s, z+s));
//		for(Entity e : list)
//		{
//			if(e instanceof ERC_EntityCoaster)
//			{
//				parent = (ERC_EntityCoaster) e;
//				parent.resetSeat(getSeatIndex(), this);
//				return true;
//			}
//		}
//		return false;
    }

    public double getMountedYOffset() {
//todo        return (double) this.height * 0.4;
        return 1;
    }

    /**
     * For vehicles, the first passenger is generally considered the controller and "drives" the vehicle. For example,
     * Pigs, Horses, and Boats are generally "steered" by the controlling passenger.
     */
    @Nullable
    public Entity getControllingPassenger() {
        List<Entity> passengers = this.getPassengers();
        return passengers.isEmpty() ? null : passengers.get(0);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return null;
    }

    @Override
    public void positionRider(Entity passenger) {
        if (parent == null) {
            return;
        }
        if (updateFlag != parent.updateFlag) {
//			ERC_Logger.debugInfo("seat rider stay");
            waitUpdateRiderFlag = true;
        }    // QDҋ@
        else {
//			ERC_Logger.debugInfo("seat rider update");
            updateRiderPosition2(passenger);
            // UDeゾXV
        }
    }

    public void updateRiderPosition2(Entity passenger) {
//		updateRiderPosFlag = true;
//		ERC_Logger.info("entityseat::updateRilderPosition");
        if (parent == null) return;
        if (passenger != null) {
            waitUpdateRiderFlag = false;
//    		if(worldObj.isClientSide())ERC_Logger.debugInfo("seat updateRiderposition");
            Vec3 vx = parent.ERCPosMat.offsetX;
            Vec3 vy = parent.ERCPosMat.offsetY;
            Vec3 vz = parent.ERCPosMat.offsetZ;
            // Z]
            vx = ERC_MathHelper.rotateAroundVector(vx, vz, getRotZ());
            vy = ERC_MathHelper.rotateAroundVector(vy, vz, getRotZ());
            // Y]
            vx = ERC_MathHelper.rotateAroundVector(vx, parent.ERCPosMat.offsetY, getRotY());
            vz = ERC_MathHelper.rotateAroundVector(vz, parent.ERCPosMat.offsetY, getRotY());
            // X]
            vy = ERC_MathHelper.rotateAroundVector(vy, parent.ERCPosMat.offsetX, getRotX());
            vz = ERC_MathHelper.rotateAroundVector(vz, parent.ERCPosMat.offsetX, getRotX());
            {
                //////////////
                // ViewYaw]xNg@dir1->dir_rotView, cross->turnCross
                Vec3 dir_rotView = ERC_MathHelper.rotateAroundVector(vz, vy, Math.toRadians(ERC_CoasterAndRailManager.rotationViewYaw));
                Vec3 turnCross = ERC_MathHelper.rotateAroundVector(vx, vy, Math.toRadians(ERC_CoasterAndRailManager.rotationViewYaw));
                // ViewPitch]xNg dir1->dir_rotView
                Vec3 dir_rotViewPitch = ERC_MathHelper.rotateAroundVector(dir_rotView, turnCross, Math.toRadians(ERC_CoasterAndRailManager.rotationViewPitch));
                // pitchp dir_rotViewPitch
                Vec3 dir_rotViewPitchHorz = new Vec3(dir_rotViewPitch.x, 0, dir_rotViewPitch.z);
                // rollpturnCross
                Vec3 crossHorzFix = new Vec3(0, 1, 0).cross(dir_rotViewPitch);
                if (crossHorzFix.length() == 0.0) crossHorzFix = new Vec3(1, 0, 0);

                // yaw OK
                setYRot((float) -Math.toDegrees(Math.atan2(dir_rotViewPitch.x, dir_rotViewPitch.z)));

                // pitch OK
                setXRot((float) Math.toDegrees(ERC_MathHelper.angleTwoVec3(dir_rotViewPitch, dir_rotViewPitchHorz) * (dir_rotViewPitch.y >= 0 ? -1f : 1f)));
                if (Float.isNaN(getXRot()))
                    setXRot(0);

                // roll
                zRot = (float) Math.toDegrees(ERC_MathHelper.angleTwoVec3(turnCross, crossHorzFix) * (turnCross.y >= 0 ? 1f : -1f));
                if (Float.isNaN(zRot))
                    zRot = 0;
            }
            yRotO = ERC_MathHelper.fixrot(getYRot(), yRotO);
            xRotO = ERC_MathHelper.fixrot(getXRot(), xRotO);

            prevzRot = ERC_MathHelper.fixrot(zRot, prevzRot);

            passenger.setYRot(this.getYRot());
            passenger.setXRot(this.getXRot());
            passenger.yRotO = this.yRotO;
            passenger.xRotO = this.xRotO;

//    		passenger.getYRot() = 0;
//    		passenger.getXRot() = -ERC_CoasterAndRailManager.rotationViewPitch;

            double toffsety = passenger.yRotO;//todo passenger.getYOffset();
//            passenger.setPosition(
//            		this.getX() + vy.xCoord*toffsety, 
//            		this.getY() + vy.yCoord*toffsety,
//            		this.getZ() + vy.zCoord*toffsety
//            		);
//    		double ox = getOffsetX();
//    		double oy = getOffsetY();
//    		double oz = getOffsetZ();
//            passenger.setPosition(
//    				parent.getX() + vy.xCoord*toffsety + parent.ERCPosMat.offsetX.xCoord*ox + parent.ERCPosMat.offsetY.xCoord*oy + parent.ERCPosMat.offsetZ.xCoord*oz, 
//    				parent.getY() + vy.yCoord*toffsety + parent.ERCPosMat.offsetX.yCoord*ox + parent.ERCPosMat.offsetY.yCoord*oy + parent.ERCPosMat.offsetZ.yCoord*oz, 
//    				parent.getZ() + vy.zCoord*toffsety + parent.ERCPosMat.offsetX.zCoord*ox + parent.ERCPosMat.offsetY.zCoord*oy + parent.ERCPosMat.offsetZ.zCoord*oz);
            passenger.setPos(
                    this.getX() + vy.x * toffsety,
                    this.getY() + vy.y * toffsety,
                    this.getZ() + vy.z * toffsety);

            passenger.setDeltaMovement(this.parent.Speed * parent.ERCPosMat.Dir.x * 1, this.parent.Speed * parent.ERCPosMat.Dir.y * 1, this.parent.Speed * parent.ERCPosMat.Dir.z * 1);
//            ERC_Logger.info("" + riddenByEntity.motionX + riddenByEntity.motionY + riddenByEntity.motionZ );

            if (level.isClientSide() && passenger instanceof LivingEntity) {
                LivingEntity el = (LivingEntity) passenger;
                if (this.parent.tlrail instanceof TileEntityRailDrift) {
                    el.yBodyRot = this.prevyBodyRot;
                } else {
                    el.yBodyRot = this.parent.ERCPosMat.yaw;
                    this.prevyBodyRot = el.yBodyRot;
                }
                if (passenger == Minecraft.getInstance().player) {
                    el.yHeadRot = ERC_CoasterAndRailManager.rotationViewYaw + el.yBodyRot;
                    if (this.parent.tlrail instanceof TileEntityRailDrift) {
                        el.setYRot(el.yHeadRot);
                        el.yRotO = el.getYRot();
                    }
                }
            }
        }
//    	ERC_CoasterAndRailManager.setRotRoll(zRot, prevzRot);
    }

    public void setAngles(float deltax, float deltay) {
//    	ERC_CoasterAndRailManager.setAngles(deltax, deltay);
    }

    //@Override
    //public void setPositionAndRotation2(double x, double y, double z, float yaw, float pit, int p_70056_9_)
    //{
    //dlƂĉ@T[o[̋KEntityŎgĂA𖳌ɂ邽
//		ERC_Logger.debugInfo("catch!");
//		super.setPositionAndRotation2(x, y, z, yaw, pit, p_70056_9_);
    //}

//	public float getRoll(float partialTicks)
//	{
//		return offsetRot + parent.prevzRot + (parent.zRot - parent.prevzRot)*partialTicks;
//	}

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        setSeatIndex(nbt.getInt("seatindex"));
        setOffsetX(nbt.getFloat("seatoffsetx"));
        setOffsetY(nbt.getFloat("seatoffsety"));
        setOffsetZ(nbt.getFloat("seatoffsetz"));
        setRotX(nbt.getFloat("seatrotx"));
        setRotY(nbt.getFloat("seatroty"));
        setRotZ(nbt.getFloat("seatrotz"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("seatindex", getSeatIndex());
        nbt.putFloat("seatoffsetx", getOffsetX());
        nbt.putFloat("seatoffsety", getOffsetY());
        nbt.putFloat("seatoffsetz", getOffsetZ());
        nbt.putFloat("seatrotx", getRotX());
        nbt.putFloat("seatroty", getRotY());
        nbt.putFloat("seatrotz", getRotZ());
    }

    public void SyncCoasterMisc_Send(FriendlyByteBuf buf, int flag) {
        switch (flag) {
            case 3: //CtS \
                break;
            case 4: //StC eNɋ
                buf.writeInt(parent.getId());
                break;
        }
    }

    public void SyncCoasterMisc_Receive(FriendlyByteBuf buf, int flag) {
        switch (flag) {
            case 3:
                ERC_MessageCoasterMisc packet = new ERC_MessageCoasterMisc(this, 4);
                ERC_PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
//			ERC_Logger.info("server repost parentID to client");
                break;
            case 4:
                int parentid = buf.readInt();
                parent = (ERC_EntityCoaster) level.getEntity(parentid);
                if (parent == null) {
                    ERC_Logger.warn("parent id is Invalid.  id:" + parentid);
                    return;
                }
                parent.addSeat(this, getSeatIndex());
//			ERC_Logger.info("client get parent");
                return;
        }
    }

    ////////////////////////////////////////this.dataManager
    public int getSeatIndex() {
        return this.entityData.get(SEAT_INDEX);
    }

    public void setSeatIndex(int idx) {
        this.entityData.set(SEAT_INDEX, idx);
    }

    public float getOffsetX() {
        return this.entityData.get(OFFSET_X);
    }

    public void setOffsetX(float offsetx) {
        this.entityData.set(OFFSET_X, offsetx);
    }

    public float getOffsetY() {
        return this.entityData.get(OFFSET_Y);
    }

    public void setOffsetY(float offsety) {
        this.entityData.set(OFFSET_Y, offsety);
    }

    public float getOffsetZ() {
        return this.entityData.get(OFFSET_Z);
    }

    public void setOffsetZ(float offsetz) {
        this.entityData.set(OFFSET_Z, offsetz);
    }

    public float getRotX() {
        return this.entityData.get(ROT_X);
    }

    public void setRotX(float rot) {
        this.entityData.set(ROT_X, rot);
    }

    public float getRotY() {
        return this.entityData.get(ROT_Y);
    }

    public void setRotY(float rot) {
        this.entityData.set(ROT_Y, rot);
    }

    public float getRotZ() {
        return this.entityData.get(ROT_Z);
    }

    public void setRotZ(float rot) {
        this.entityData.set(ROT_Z, rot);
    }
}
