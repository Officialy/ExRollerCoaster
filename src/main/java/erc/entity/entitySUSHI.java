//package erc.entity;
//
//import erc._core.ERC_CONST;
//import erc._core.ERC_Logger;
//import net.minecraft.network.syncher.EntityEntityDataSerializers;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.client.model.obj.OBJLoader;
//import net.minecraftforge.client.model.obj.OBJModel;
//
//import java.util.Random;
//
//public class entitySUSHI extends Entity {
//
//    private static final EntityDataAccessor<Integer> ID = EntityDataManager.<Integer>createKey(entitySUSHI.class, EntityEntityDataSerializers.VARINT);
//    private static final EntityDataAccessor<Float> ROT = EntityDataManager.<Float>createKey(entitySUSHI.class, EntityEntityDataSerializers.FLOAT);
//
//
//    public static OBJModel model1;
//
//    public static OBJModel model2;
//
//    public static OBJModel model3;
//
//    public static OBJModel model4;
//
//    public static OBJModel model5;
//
//    public static OBJModel[] models;
//
//    public static void clientInitSUSHI()
//    {
//        try {
//            model1 = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ERC_CONST.DOMAIN, "models/sushi/" + "sushi_m.obj"));
//            model2 = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ERC_CONST.DOMAIN, "models/sushi/" + "sushi_t.obj"));
//            model3 = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ERC_CONST.DOMAIN, "models/sushi/" + "sushi_w.obj"));
//            model4 = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ERC_CONST.DOMAIN, "models/sushi/" + "sushi_e.obj"));
//            model5 = (OBJModel) OBJLoader.INSTANCE.loadModel(new ResourceLocation(ERC_CONST.DOMAIN, "models/sushi/" + "sushi_g.obj"));
//        }
//        catch(Exception e){
//            ERC_Logger.warn("Loading sushi model failed");
//        }
//        models = new OBJModel[5];
//        models[0] = model2;
//        models[1] = model3;
//        models[2] = model4;
//        models[3] = model5;
//        models[4] = model1;
//    }
//
//    float rotation;
//    float prevRotation;
//
//    public entitySUSHI(Level world)
//    {
//        super(world);
//        setSize(0.9f, 0.4f);
//    }
//
//    public entitySUSHI(Level world, double posX, double posY, double posZ)
//    {
//        this(world);
//        setPosition(posX, posY, posZ);
//    }
//
//    public float getInterpRotation(float partialTicks)
//    {
//        return this.prevRotation + (this.rotation - this.prevRotation) * partialTicks;
//    }
//
//    public OBJModel getModel() { return models[this.getId()];}
//
//    @Override
//    protected void entityInit()
//    {
//        Random r = new Random();
//
//        this.dataManager.register(ROT, 0f);
////		this.dataManager.addObject(21, new Integer(1));
//        this.dataManager.register(ID, (int) Math.floor(r.nextInt(44)/10d));
//    }
//
//    @Override
//    public boolean canBeCollidedWith()
//    {
//        return !this.isDead;
//    }
//
//    @Override
//    public boolean attackEntityFrom(DamageSource ds, float p_70097_2_)
//    {
//        boolean flag = ds.getTrueSource() instanceof EntityPlayer;
//
//        if (flag)
//        {
//            setDead();
//            boolean flag1 = ((EntityPlayer)ds.getTrueSource()).capabilities.isCreativeMode;
//            if(!flag1 && !world.isRemote)entityDropItem(new ItemStack(ERC_Core.ItemSUSHI,1,0), 0f);
//        }
//
//        return false;
//    }
//
//    @Override
//    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
//    {
//        if(player.isSneaking())
//        {
//            setRot(getRot()*1.1f);
//        }
//        else
//        {
//            if(getRot()==0)setRot(3.0f);
//            else if(getRot()>0)setRot(-3.0f);
//            else if(getRot()<0)setRot(0);
//        }
//        player.swingArm(hand);
//        return false;
//    }
//
//    public void onUpdate()
//    {
////		setDead();
//        prevRotation = rotation;
//        rotation += getRot();
//        ERC_MathHelper.fixrot(rotation, prevRotation);
//    }
//
//    @Override
//    protected void readEntityFromNBT(NBTTagCompound nbt) {
//        setRot(nbt.getFloat("speed"));
//        int id = nbt.getInteger("modelid");
//        if(id>0)setId(id);
//    }
//
//    @Override
//    protected void writeEntityToNBT(NBTTagCompound nbt) {
//        nbt.setFloat("speed",getRot());
//        nbt.setInteger("modelid",getId());
//    }
//
//    public float getRot()
//    {
//        return this.dataManager.get(ROT);
//    }
//    public void setRot(float rot)
//    {
//        this.dataManager.set(ROT, rot);
//    }
//
//    public int getId()
//    {
//        return this.dataManager.get(ID);
//    }
//    public void setId(int id)
//    {
//        this.dataManager.set(ID, id);
//    }
//}
