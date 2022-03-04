package erc.block.tileEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import erc.block.BlockRailBase;
import erc.core.ERC_Logger;
import erc.core.ERC_ReturnCoasterRot;
import erc.entity.ERC_EntityCoaster;
import erc.gui.RailScreen;
import erc.gui.container.DefMenu;
import erc.manager.ERC_CoasterAndRailManager;
import erc.manager.ERC_ModelLoadManager;
import erc.math.ERC_MathHelper;
import erc.model.ERC_ModelDefaultRail;
import erc.model.Wrap_RailRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

public abstract class TileEntityRailBase extends Wrap_BlockEntityRail {
    public Wrap_RailRenderer modelrail; //test
    public int modelrailindex;
    //base
    public DataTileEntityRail BaseRail;
    //next
    public DataTileEntityRail NextRail;
    // R[X^[
    public float Length;
    //	public int VertexNum = PosNum*4;
//	Vec3 posArray[] = new Vec3[VertexNum];
    public boolean doesDrawGUIRotaArraw;
    public ResourceLocation RailTexture;
    // `֘Ap^
    protected int PosNum = 15;
    public float[] fixedParamTTable = new float[PosNum];

    protected boolean isBreak;

    public TileEntityRailBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
//		for(int i = 0; i<VertexNum; ++i) posArray[i] = new Vec3(0.0, 0.0, 0.0);
        for (int i = 0; i < PosNum; ++i) fixedParamTTable[i] = 0;

        BaseRail = new DataTileEntityRail();
        NextRail = new DataTileEntityRail();

        Length = 1f;
        isBreak = false;

        RailTexture = new ResourceLocation("textures/block/iron_block.png");
        modelrail = new ERC_ModelDefaultRail(RenderType::entitySolid);
//		modelrail = ERC_ModelLoadManager.getRailModel(2, 0);
    }

    public void Init() {
        BaseRail.SetPos(-1, -1, -1);
        NextRail.SetPos(-1, -1, -1);
    }

    public BlockPos getPos() {
        return this.worldPosition;
    }

    @Override
    public TileEntityRailBase getRail() {
        return this;
    }

    //@Override
//    public boolean myisInvalid() {
//        return super.isInvalid();
//    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    private void SetPrevRailPosition(int x, int y, int z) {
        if (x == this.getPos().getX() && y == this.getPos().getY() && z == this.getPos().getZ()) {
            BaseRail.cx = -1;
            BaseRail.cy = -1;
            BaseRail.cz = -1;
            ERC_Logger.warn("TileEntityRail SetPrevRailPosition : connect oneself");
            return;
        }
        BaseRail.cx = x;
        BaseRail.cy = y;
        BaseRail.cz = z;
    }
//	private void SetNextRailPosition(int x, int y, int z)
//	{
//		if(x==xCoord && y==yCoord && z==zCoord)
//		{
//			NextRail.cx=-1;	NextRail.cy=-1;	NextRail.cz=-1;
//			ERC_Logger.warn("TileEntityRail SetNextRailPosition : connect oneself");
//			return;
//		}
//		NextRail.cx = x; NextRail.cy = y; NextRail.cz = z;
//	}

    public Wrap_BlockEntityRail getPrevRailTileEntity() {
        return (Wrap_BlockEntityRail) level.getBlockEntity(new BlockPos(BaseRail.cx, BaseRail.cy, BaseRail.cz));
    }

    public Wrap_BlockEntityRail getNextRailTileEntity() {
        return (Wrap_BlockEntityRail) level.getBlockEntity(new BlockPos(NextRail.cx, NextRail.cy, NextRail.cz));
    }

    public void SetPosNum(int num) {
        this.PosNum = num;
        fixedParamTTable = new float[PosNum];
        for (int i = 0; i < PosNum; ++i) fixedParamTTable[i] = 0;
//		CreateNewRailVertexFromControlPoint();
    }

    public int GetPosNum() {
        return this.PosNum;
    }

    public boolean isBreak() {
        return isBreak;
    }

    public void setBreak(boolean flag) {
        isBreak = flag;
    }

    //    @Override todo invalidate
    public void invalidate() {
        if (level.isClientSide()) {
            double dist = Minecraft.getInstance().player.distanceToSqr(this.getPos().getX() + 0.5, this.getPos().getY(), this.getPos().getZ() + 0.5);
            if (6d > dist) {
                ERC_CoasterAndRailManager.SetPrevData(BaseRail.cx, BaseRail.cy, BaseRail.cz);
                ERC_CoasterAndRailManager.SetNextData(NextRail.cx, NextRail.cy, NextRail.cz);
            }
        }
//        super.invalidate();
    }

/*todo    public void SetRailDataFromMessage(ERC_MessageRailStC msg) {
        this.SetPosNum(msg.posnum);
        /////\\\\\
        Iterator<DataTileEntityRail> it = msg.raillist.iterator();
        // Base
        DataTileEntityRail e = it.next();
        SetBaseRailVectors(e.vecPos, e.vecDir, e.vecUp, e.Power);
        SetBaseRailfUpTwist(e.fUp, e.fDirTwist);
        SetPrevRailPosition(e.cx, e.cy, e.cz);
        // Next
        e = it.next();
        SetNextRailVectors(e.vecPos, e.vecDir, e.vecUp, e.fUp, e.fDirTwist, e.Power, e.cx, e.cy, e.cz);
        /////\\\\\
//    	this.CreateNewRailVertexFromControlPoint();
        this.CalcRailPosition();
    }*/

    public void SetBaseRailPosition(int x, int y, int z, Vec3 BaseDir, Vec3 up, float power) {
        BaseRail.vecPos = new Vec3((double) x + 0.5, (double) y + 0.5, (double) z + 0.5);
        SetBaseRailVectors(BaseRail.vecPos, BaseDir, up, power);
    }

    public void SetBaseRailVectors(Vec3 posBase, Vec3 dirBase, Vec3 vecup, float power) {
        BaseRail.vecPos = new Vec3(posBase.x, posBase.y, posBase.z);
        BaseRail.vecDir = new Vec3(dirBase.x, dirBase.y, dirBase.z);
        BaseRail.vecUp = new Vec3(vecup.x, vecup.y, vecup.z);
        BaseRail.Power = power;
    }

    public void SetBaseRailfUpTwist(float up, float twist) {
        BaseRail.fUp = up;
        BaseRail.fDirTwist = twist;
    }

    public void SetNextRailVectors(TileEntityRailBase nexttile) {
        SetNextRailVectors(nexttile.BaseRail, nexttile.getPos().getX(), nexttile.getPos().getY(), nexttile.getPos().getZ());//vecBase, nexttile.dirBase, nexttile.vecUp, nexttile.fUp, nexttile.fDirTwist, nexttile.Power);
    }

    public void SetNextRailVectors(DataTileEntityRail rail, int x, int y, int z) {
        SetNextRailVectors(rail.vecPos, rail.vecDir, rail.vecUp, rail.fUp, rail.fDirTwist, rail.Power, x, y, z);
    }

    public void SetNextRailVectors(Vec3 vecNext, Vec3 vecDir, Vec3 vecUp, float fUp, float fDirTwist, float Power, int cx, int cy, int cz) {
        this.NextRail.SetData(vecNext, vecDir, vecUp, fUp, fDirTwist, Power, cx, cy, cz);
    }

//	public ERC_TileEntityRailBase getOwnRailData()
//	{
//		return this;
//	}

    public void AddControlPoint(int n) {
        n = n * 2 - 1;
        if (this.PosNum + n > 50) SetPosNum(50);
        else if (this.PosNum + n < 2) SetPosNum(2);
        else SetPosNum(this.PosNum + n);
//		CreateNewRailVertexFromControlPoint();
    }

//	public void CreateNewRailVertexFromControlPoint()
//	{
//		this.VertexNum = this.PosNum*4;
//		posArray = new Vec3[VertexNum];
//		for(int i = 0; i<VertexNum; ++i) posArray[i] = new Vec3(0.0, 0.0, 0.0);
//		CalcRailPosition();
//	}

    public void AddPower(int idx) {
        float f = switch (idx) {
            case 0 -> -1.0f;
            case 1 -> -0.1f;
            case 2 -> 0.1f;
            case 3 -> 1.0f;
            default -> 0;
        };
        if (BaseRail.Power + f > 100f) BaseRail.Power = 100f;
        else if (BaseRail.Power + f < 0.1f) BaseRail.Power = 0.1f;
        else BaseRail.Power += f;
//		CreateNewRailVertexFromControlPoint(); //->	CalcRailPosition();
//		CalcPrevRailPosition();
    }

    public void UpdateDirection(RailScreen.editFlag flag, int idx) {
        float rot = switch (idx) {
            case 0 -> -0.5f;
            case 1 -> -0.05f;
            case 2 -> 0.05f;
            case 3 -> 0.5f;
            default -> 0;
        };
        Direction dir = level.getBlockState(new BlockPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ())).getValue(BlockRailBase.DIRECTION);
        switch (flag) {
            case ROTRED -> // 
                    ConvertVec3FromDir(dir, BaseRail.vecDir, rot);
            case ROTGREEN -> // z Up
                    BaseRail.addFUp(rot);
            case ROTBLUE -> // 
                    BaseRail.addTwist(rot);
            default -> {
            }
        }
    }

    private Vec3 ConvertVec3FromDir(Direction direction, Vec3 dir, float rot) {
        return switch (direction) {
            case DOWN, UP -> this.rotateAroundY(dir, -rot);
            case NORTH, SOUTH -> this.rotateAroundZ(dir, -rot);
            case WEST, EAST -> this.rotateAroundX(dir, -rot);
            default -> dir;
        };
    }

    public void ResetRot() {
        BaseRail.resetRot();
    }

    public void Smoothing() {
        if (isConnectRail_prev1_next2()) ;
        Wrap_BlockEntityRail prevtl = BaseRail.getConnectionTileRail(level);
        Wrap_BlockEntityRail nexttl = NextRail.getConnectionTileRail(level);
        if (prevtl == null) return;
        if (nexttl == null) return;

//		Vec3 n = BaseRail.vecPos.subtract(nexttl.getRail().BaseRail.vecPos).normalize();
//		Vec3 p = BaseRail.vecPos.subtract(prevtl.getRail().BaseRail.vecPos).normalize();
        Vec3 n = nexttl.getRail().BaseRail.vecPos;
        Vec3 p = prevtl.getRail().BaseRail.vecPos;
        Vec3 tempDir = n.subtract(p).normalize(); //FT p.subtract(n) made smoothing go all weird
        BaseRail.vecDir = tempDir.normalize();

        switch (level.getBlockState(new BlockPos(this.getPos().getX(), this.getPos().getY(), this.getPos().getZ())).getValue(BlockRailBase.DIRECTION)) {
            case DOWN:
            case UP: {//y
                BaseRail.vecDir = new Vec3(BaseRail.vecDir.x, 0, BaseRail.vecDir.z);
                BaseRail.fUp = (tempDir.y > 0 ? -1 : 1) * (float) (tempDir.y / Math.sqrt(tempDir.x * tempDir.x + tempDir.z * tempDir.z));
            }
            case NORTH:
            case SOUTH: { //z
                BaseRail.vecDir = new Vec3(BaseRail.vecDir.x, BaseRail.vecDir.y, 0);
                BaseRail.fUp = (tempDir.z > 0 ? -1 : 1) * (float) (tempDir.z / Math.sqrt(tempDir.x * tempDir.x + tempDir.y * tempDir.y));
            }
            case WEST:
            case EAST: { //x
                BaseRail.vecDir = new Vec3(0, BaseRail.vecDir.y, BaseRail.vecDir.z);
                BaseRail.fUp = (tempDir.x > 0 ? -1 : 1) * (float) (tempDir.x / Math.sqrt(tempDir.y * tempDir.y + tempDir.z * tempDir.z));
            }
        }
        BaseRail.vecDir = BaseRail.vecDir.normalize();

        BaseRail.Power = (float) p.subtract(n).length() / 2;
//		BaseRail.Power = ERC_MathHelper.CalcSmoothRailPower(
//    			BaseRail.vecDir, nexttl.getRail().BaseRail.vecDir, 
//    			BaseRail.vecPos, nexttl.getRail().BaseRail.vecPos
//    			);
        CalcRailPosition();
        prevtl.getRail().SetNextRailVectors(this);
        prevtl.getRail().CalcRailPosition();
        BaseRail.Power = (float) p.subtract(n).length() / 2;
//		BaseRail.Power = ERC_MathHelper.CalcSmoothRailPower(BaseRail.vecDir, NextRail.vecDir, BaseRail.vecPos, NextRail.vecPos);

//		prevtl.SetNextRailPosition(vecBase, dirBase, vecUp, Power);
    }

    public boolean isConnectRail_prev1_next2() {
        return false;
//		if(getPrevRailTileEntity()==null)return false;
//		else
//		{
//			Wrap_TileEntityRail next1 = getNextRailTileEntity();
//			if(next1==null)return false;
//			if(next1.getNextRailTileEntity() == null)return false;
//			return true;
//		}
    }

    public void SmoothingSpecial() {
        Smoothing();
    }

    public void CalcRailPosition() {
//		if(!world.isClientSide())return;

        ////pos
        Vec3 Base = new Vec3(BaseRail.vecUp.x * 0.5, BaseRail.vecUp.y * 0.5, BaseRail.vecUp.z * 0.5);
        Vec3 Next = NextRail.vecPos.subtract(BaseRail.vecPos); //FT Used to be Base subtract Next but that made things go backwards
        Next = new Vec3(Next.x + NextRail.vecUp.x * 0.5, Next.y + NextRail.vecUp.y * 0.5, Next.z + NextRail.vecUp.z * 0.5);

        ////dir
//		float basepow = ERC_MathHelper.Lerp(0.2f, BaseRail.Power, NextRail.Power);
//		float nextpow = ERC_MathHelper.Lerp(0.8f, BaseRail.Power, NextRail.Power);
        Vec3 DirxPowb = BaseRail.CalcVec3DIRxPOW(BaseRail.Power);//basepow);
        Vec3 DirxPown = NextRail.CalcVec3DIRxPOW(NextRail.Power);//nextpow);

        ////pair of rail Vertex
//		Vec3 vecpitch1 = vecUp.cross(dirBase).normalize();
//		Vec3 vecpitch2 = vecNextUp.cross(dirNext).normalize();
        Vec3 vecUp_1 = BaseRail.CalcVec3UpTwist();
        Vec3 vecUp_2 = NextRail.CalcVec3UpTwist();

        Length = 0;
        Vec3 tempPrev = null;
        fixedParamTTable[0] = 0;
        if (modelrail != null) modelrail.setModelNum(PosNum);

        for (int i = 0; i < PosNum; ++i) {
//			int j = i*4; // VertexIndex
            float f = (float) i / (float) (PosNum - 1);

            ////spline
            Vec3 center = ERC_MathHelper.Spline(f, Base, Next, DirxPowb, DirxPown);
            if (i > 0) {
                Length += center.distanceTo(tempPrev);
                fixedParamTTable[i] = Length;
            }
            tempPrev = center;
        }

        calcFixedParamT();//////////////////////////////////////////////////////////////////////////

        float ModelLen = Length / (PosNum - 1);

        for (int i = 0; i < PosNum; ++i) {

            float f = (float) i / (float) (PosNum - 1);
            ////fixed spline
//			float lT;
            int T = (int) Math.floor(f * (PosNum - 1));
//			if(PosNum-1 <= T) lT = fixedParamTTable[T];
            f = fixedParamTTable[T];
//			else lT = ERC_MathHelper.Lerp(f-(T/(float)(PosNum-1)), fixedParamTTable[T], fixedParamTTable[T+1]);
            Vec3 center = ERC_MathHelper.Spline(f, Base, Next, DirxPowb, DirxPown);
//			ERC_Logger.info("f-t:"+(f-(T/(float)(PosNum-1))));

            Vec3 dir1;
            if (f <= 0.01f) {
                dir1 = DirxPowb.normalize();
            }
            if (f >= 0.99f) {
                dir1 = DirxPown.normalize();
            } else {
                dir1 = ERC_MathHelper.Spline((f + 0.01f), Base, Next, DirxPowb, DirxPown);
                Vec3 dir2 = ERC_MathHelper.Spline((f - 0.01f), Base, Next, DirxPowb, DirxPown);
                dir1 = dir2.subtract(dir1); // dir1 - dir2
            }

            ////pair of rail Vertex
            Vec3 up = ERC_MathHelper.Slerp(f, vecUp_1, vecUp_2).normalize();
            Vec3 cross = up.cross(dir1);
            cross = cross.normalize().normalize();


//			if(j>=posArray.length)
//			{
//				ERC_Logger.warn("index exception");
//				return;
//			}
            if (i == PosNum - 1) cross = new Vec3(-cross.x, -cross.y, -cross.z);
            if (modelrail != null) modelrail.construct(i, center, dir1, cross, ModelLen);
//			// 
//			posArray[j  ].xCoord = center.xCoord - cross.xCoord*t1;
//			posArray[j  ].yCoord = center.yCoord - cross.yCoord*t1;
//			posArray[j  ].zCoord = center.zCoord - cross.zCoord*t1;
//			posArray[j+1].xCoord = center.xCoord - cross.xCoord*t2;
//			posArray[j+1].yCoord = center.yCoord - cross.yCoord*t2;
//			posArray[j+1].zCoord = center.zCoord - cross.zCoord*t2;
//			// E 
//			posArray[j+2].xCoord = center.xCoord + cross.xCoord*t2;
//			posArray[j+2].yCoord = center.yCoord + cross.yCoord*t2;
//			posArray[j+2].zCoord = center.zCoord + cross.zCoord*t2;
//			posArray[j+3].xCoord = center.xCoord + cross.xCoord*t1;
//			posArray[j+3].yCoord = center.yCoord + cross.yCoord*t1;
//			posArray[j+3].zCoord = center.zCoord + cross.zCoord*t1;

//			// ʒu
//			posArray[j  ] = center;
//			// px
//			Vec3 crossHorz = new Vec3(0, 1, 0).cross(dir1);
//			Vec3 dir_horz = new Vec3(dir1.xCoord, 0, dir1.zCoord);
//			posArray[j+1].xCoord = -Math.toDegrees( Math.atan2(dir1.xCoord, dir1.zCoord) );
//			posArray[j+1].yCoord = Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir1, dir_horz) * (dir1.yCoord>0?-1f:1f) );
//			posArray[j+1].zCoord = Math.toDegrees( ERC_MathHelper.angleTwoVec3(cross, crossHorz) * (cross.yCoord>0?1f:-1f) );
//			// 
//			if(i!=PosNum-1)posArray[j+2].xCoord = (fixedParamTTable[i+1]-fixedParamTTable[i])*Length;


        }
//		calcFixedParamT();
    }


    protected void calcFixedParamT() {
        ///////////// fixedParamTC

        // [0,1]PosNum̊ԊǔvZ
        float div = Length / (float) (PosNum - 1);
//		float divT = 1.0f / (float)PosNum;
        float[] tempFixed = new float[PosNum];
        // `Ԃdiv̈ʒuT
        int I = 1;
        for (int i = 1; i < PosNum; ++i) {
//			ERC_Logger.info("i I div:"+i+" "+I+" "+div*i/Length
//					+ "  fixedParamTTable[I]"+fixedParamTTable[I]/Length
//					+"  fixedParamTTable[I-1]"+fixedParamTTable[I-1]/Length);
            if (div * i < fixedParamTTable[I] && div * i >= fixedParamTTable[I - 1]) {
                float divnum = PosNum - 1f;
                float t = (div * i - fixedParamTTable[I - 1]) / (fixedParamTTable[I] - fixedParamTTable[I - 1]);
                tempFixed[i] = (I - 1) / divnum + t * (1f / divnum);
//				ERC_Logger.info("tempfix[i]:"+tempFixed[i]);
            } else {
                if (I < PosNum - 1) {
                    ++I;
                    --i;
                } else {
                    tempFixed[i] = 1.0f;
                }
            }
        }
        tempFixed[PosNum - 1] = 1.0f;
        fixedParamTTable = tempFixed;
//        ERC_Logger.info(""+fixedParamTTable[3]);
    }

    public double CalcRailPosition2(float t, ERC_ReturnCoasterRot ret, float viewyaw, float viewpitch, boolean riddenflag) {

        //FT positions of *rails*, relative to the base (current) rail *block*
        Vec3 Base = new Vec3(BaseRail.vecUp.x, BaseRail.vecUp.y, BaseRail.vecUp.z).scale(0.5);
        //FT My fix - seems to work
        Vec3 Next = NextRail.vecPos.subtract(BaseRail.vecPos).add(NextRail.vecUp.scale(0.5));
        //FTMotty's original code said
        //FTVec3 Next = BaseRail.vecPos.subtract(NextRail.vecPos).add(NextRail.vecUp.scale(0.5));
        //FTNext = new Vec3(NextRail.vecUp.x * 1.5, NextRail.vecUp.y * 1.5, NextRail.vecUp.z * 1.5);

        ////dir
//		float basepow = ERC_MathHelper.Lerp(0.2f, BaseRail.Power, NextRail.Power);
//		float nextpow = ERC_MathHelper.Lerp(0.8f, BaseRail.Power, NextRail.Power);
        Vec3 DirxPowb = BaseRail.CalcVec3DIRxPOW(BaseRail.Power);//basepow);
        Vec3 DirxPown = NextRail.CalcVec3DIRxPOW(NextRail.Power);//nextpow);

        ////pair of rail Vertex
        Vec3 vecUp_1 = BaseRail.CalcVec3UpTwist();
        Vec3 vecUp_2 = NextRail.CalcVec3UpTwist();

        ////spline
        float lT = 0f;
        if (t < 0) {
            ERC_Logger.warn("tileentityrailbase.calcposition2 : paramT is smaller than 0");
            t = 0;
        }
        int T = (int) Math.floor(t * (PosNum - 1));
        if (PosNum - 1 <= T) lT = fixedParamTTable[PosNum - 1];
        else lT = ERC_MathHelper.Lerp(t * (PosNum - 1) - T, fixedParamTTable[T], fixedParamTTable[T + 1]);
        t = lT;

        ret.pos = ERC_MathHelper.Spline(t, Base, Next, DirxPowb, DirxPown);

        Vec3 dir1;
        if (t <= 0.01f) {
//			dir1 = new Vec3(BaseRail.vecDir.xCoord, BaseRail.vecDir.yCoord, BaseRail.vecDir.zCoord);
            dir1 = DirxPowb.normalize();
        } else if (t >= 0.99f) {
//			dir1 = new Vec3(NextRail.vecDir.xCoord, NextRail.vecDir.yCoord, NextRail.vecDir.zCoord);
            dir1 = DirxPown.normalize();
        } else {
            dir1 = ERC_MathHelper.Spline((t + 0.01f), Base, Next, DirxPowb, DirxPown);
            Vec3 dir2 = ERC_MathHelper.Spline((t - 0.01f), Base, Next, DirxPowb, DirxPown);
            //FTMAY BE WRONG BUT I DON'T THINK SO
            //dir1 = dir2.subtract(dir1).normalize(); // dir1 - dir2
            dir1 = dir1.subtract(dir2).normalize(); //dir1 - dir2
        }

        ////pair of rail Vertex
        Vec3 up = ERC_MathHelper.Slerp(t, vecUp_1, vecUp_2).normalize();

        Vec3 cross = up.cross(dir1).normalize();

//		ERC_MathHelper.CalcCoasterRollMatrix(ret, ret.Pos, dir1, up);

        ret.pos = ret.pos.add(this.getPos().getX() + 0.5, this.getPos().getY() + 0.5, this.getPos().getZ() + 0.5);//+ coords of up

        ret.Dir = dir1;
        ret.Pitch = cross;
        
        Vec3 fixUp = dir1.cross(cross);
        ret.offsetX = cross;
        ret.offsetY = fixUp;
        ret.offsetZ = dir1;
        ret.up = fixUp;


//		if(riddenflag)
        {
            ////////////// vC[]ʌvZ

            /* memo
             * VecEEE dir1,up,cross
             * _]EEE dir_rotView
             */

            // ViewYaw]xNg@dir1->dir_rotView, cross->turnCross
            Vec3 dir_rotView = ERC_MathHelper.rotateAroundVector(dir1, fixUp, Math.toRadians(viewyaw));
            Vec3 turnCross = ERC_MathHelper.rotateAroundVector(cross, fixUp, Math.toRadians(viewyaw));
            // ViewPitch]xNg dir1->dir_rotView
            Vec3 dir_rotViewPitch = ERC_MathHelper.rotateAroundVector(dir_rotView, turnCross, Math.toRadians(viewpitch));
            // pitchp dir_rotViewPitch
//			Vec3 dir_rotViewPitchHorz = new Vec3(dir_rotViewPitch.xCoord, 0, dir_rotViewPitch.zCoord);
            // rollpturnCross
            Vec3 crossHorz = new Vec3(0, 1, 0).cross(dir1);
            if (crossHorz.length() == 0.0) crossHorz = new Vec3(1, 0, 0);
            Vec3 crossHorzFix = new Vec3(0, 1, 0).cross(dir_rotViewPitch);
            if (crossHorzFix.length() == 0.0) crossHorzFix = new Vec3(1, 0, 0);

            Vec3 dir_horz = new Vec3(dir1.x, 0, dir1.z);
            if (dir_horz.length() == 0.0) dir_horz = fixUp;
//			Vec3 dir_WorldUp = new Vec3(0, 1, 0);

            // yaw OK
            ret.yaw = (float) -Math.toDegrees(Math.atan2(dir1.x, dir1.z));
//			ret.viewYaw = (float) -Math.toDegrees( Math.atan2(dir_rotViewPitch.xCoord, dir_rotViewPitch.zCoord) );

            // pitch OK
            ret.pitch = (float) Math.toDegrees(ERC_MathHelper.angleTwoVec3(dir1, dir_horz) * (dir1.y >= 0 ? -1f : 1f));
//			ret.viewPitch = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(dir_rotViewPitch, dir_rotViewPitchHorz) * (dir_rotViewPitch.yCoord>=0?-1f:1f) );
//			if(Float.isNaN(ret.viewPitch))
//				ret.viewPitch=0;

            // roll
            ret.roll = (float) Math.toDegrees(ERC_MathHelper.angleTwoVec3(cross, crossHorz) * (cross.y >= 0 ? 1f : -1f));
//			ret.viewRoll = (float) Math.toDegrees( ERC_MathHelper.angleTwoVec3(turnCross, crossHorzFix) * (turnCross.yCoord>=0?1f:-1f) );
//			if(Float.isNaN(ret.viewRoll))
//				ret.viewRoll=0;
        }

        return -dir1.normalize().y;
    }

    public float CalcRailLength() {
        ////pos
        Vec3 Base = new Vec3(BaseRail.vecUp.x, BaseRail.vecUp.y, BaseRail.vecUp.z).scale(0.5);
        Vec3 Next = NextRail.vecPos.subtract(BaseRail.vecPos).add(NextRail.vecUp.scale(0.5));

        ////dir
//		float basepow = ERC_MathHelper.Lerp(0.2f, BaseRail.Power, NextRail.Power);
//		float nextpow = ERC_MathHelper.Lerp(0.8f, BaseRail.Power, NextRail.Power);
        Vec3 DirxPowb = BaseRail.CalcVec3DIRxPOW(BaseRail.Power);//basepow);
        Vec3 DirxPown = NextRail.CalcVec3DIRxPOW(NextRail.Power);//nextpow);

        Length = 0;
        Vec3 tempPrev = Base;
        fixedParamTTable[0] = 0;

        for (int i = 0; i < PosNum; ++i) {
//			int j = i*4; // VertexIndex
            float f = (float) i / (float) (PosNum - 1);

            ////spline
            Vec3 center = ERC_MathHelper.Spline(f, Base, Next, DirxPowb, DirxPown);
            if (i > 0) {
                Length += center.distanceTo(tempPrev);
                fixedParamTTable[i] = Length;
            }
            tempPrev = center;
        }

        calcFixedParamT();
        return Length;
    }

    public void CalcPrevRailPosition() {
        Wrap_BlockEntityRail Wprevtile = BaseRail.getConnectionTileRail(level);
        if (Wprevtile == null) {
            return;
        }
        TileEntityRailBase prevtile = Wprevtile.getRail();
        prevtile.SetNextRailVectors(this);
//		prevtile.CreateNewRailVertexFromControlPoint();
//		prevtile.CalcRailPosition();
    }

    public abstract void SpecialRailProcessing(ERC_EntityCoaster EntityCoaster);

    public void onCoasterEntry(ERC_EntityCoaster coaster) {
    }

    public void onPassedCoaster(ERC_EntityCoaster EntityCoaster) {
    }

    public void onApproachingCoaster() {
    }

    public void onDeleteCoaster() {
    }
//	public void onTileSetToWorld_Init(){}

    public void SpecialGUIInit(RailScreen gui) {
    }

    public void SpecialGUISetData(int flag) {
    }

    public String SpecialGUIDrawString() {
        return "";
    }

    public void setDataToByteMessage(FriendlyByteBuf buf) {
    }

    public void getDataFromByteMessage(FriendlyByteBuf buf) {
    }

    public ResourceLocation getDrawTexture() {
        return this.RailTexture;
    }

    public void render(PoseStack stack, VertexConsumer consumer, int a, int b, float c, float d, float e, float f) {
        modelrail.renderToBuffer(stack, consumer, a, b, c, d, e, f);
    }

    public void changeRailModelRenderer(int index) //TODO
    {
        modelrailindex = index;

//		if(world==null)return;
//		if(world.isClientSide())
        if (FMLLoader.getDist().isClient()) {
            modelrail = ERC_ModelLoadManager.createRailRenderer(index, this);
            modelrail.setModelNum(PosNum);
            CalcRailPosition();
        }
    }

    public void readFromNBT(CompoundTag par1NBTTagCompound) {
        super.load(par1NBTTagCompound);
        loadFromNBT(par1NBTTagCompound, "");
    }

    public void loadFromNBT(CompoundTag nbt, String tag) {
        //For some reason this BlockEntity receives two NBTTagCompounds to read from.
        //The first contains all the necessary info.
        //The second only has the x, y and z positions.
        //Updating from the first compound is fine, but when updating from the second, there is no posnum tag so instead
        //of throwing, 0 is returned.
        //Unfortunately posnum controls the size of an array, which is subsequently indexed into, so a posnum of 0
        //cannot be allowed. I have added a workaround which checks if the posnum tag exists, and only continues if it
        //does, in case the second packet has some usage I'm not aware of.
        if (nbt.contains("posnum")) {
            SetPosNum(nbt.getInt(tag + "posnum"));
            //    	this.VertexNum = PosNum*4;

            readRailNBT(nbt, BaseRail, tag + "");
            readRailNBT(nbt, NextRail, tag + "n");

            modelrailindex = nbt.getInt(tag + "railmodelindex");
            changeRailModelRenderer(modelrailindex);
            //        this.CreateNewRailVertexFromControlPoint();
            //        if(world.isClientSide())
            this.CalcRailPosition();
            //        else this.CalcRailLength();
        }
    }

    protected void readRailNBT(CompoundTag nbt, DataTileEntityRail rail, String tag) {
        rail.vecPos = readVec3(nbt, tag + "pos");
        rail.vecDir = readVec3(nbt, tag + "dir");
        rail.vecUp = readVec3(nbt, tag + "up");

        rail.fUp = nbt.getFloat(tag + "fup");
        rail.fDirTwist = nbt.getFloat(tag + "fdt");
        rail.Power = nbt.getFloat(tag + "pow");

        rail.cx = nbt.getInt(tag + "cx");
        rail.cy = nbt.getInt(tag + "cy");
        rail.cz = nbt.getInt(tag + "cz");

        if (rail.cx == this.getPos().getX() && rail.cy == this.getPos().getY() && rail.cz == this.getPos().getZ()) {
            rail.cx = -1;
            rail.cy = -1;
            rail.cz = -1;
        }
    }

    private Vec3 readVec3(CompoundTag nbt, String name) {
        return new Vec3(nbt.getDouble(name + "x"), nbt.getDouble(name + "y"), nbt.getDouble(name + "z"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        saveToNBT(tag, "");
        super.saveAdditional(tag);
    }

    public void saveToNBT(CompoundTag nbt, String tag) {
        nbt.putInt(tag + "posnum", this.PosNum);
        writeRailNBT(nbt, BaseRail, tag + "");
        writeRailNBT(nbt, NextRail, tag + "n");
        nbt.putInt(tag + "railmodelindex", modelrailindex);
    }

    protected void writeRailNBT(CompoundTag nbt, DataTileEntityRail rail, String tag) {
        writeVec3(nbt, rail.vecPos, tag + "pos");
        writeVec3(nbt, rail.vecDir, tag + "dir");
        writeVec3(nbt, rail.vecUp, tag + "up");

        nbt.putFloat(tag + "fup", rail.fUp);
        nbt.putFloat(tag + "fdt", rail.fDirTwist);
        nbt.putFloat(tag + "pow", rail.Power);

        nbt.putInt(tag + "cx", rail.cx);
        nbt.putInt(tag + "cy", rail.cy);
        nbt.putInt(tag + "cz", rail.cz);

        if (rail.cx == this.getPos().getX() && rail.cy == this.getPos().getY() && rail.cz == this.getPos().getZ()) {
            rail.cx = -1;
            rail.cy = -1;
            rail.cz = -1;
        }
    }

    private void writeVec3(CompoundTag nbt, Vec3 vec, String name) {
        nbt.putDouble(name + "x", vec.x);
        nbt.putDouble(name + "y", vec.y);
        nbt.putDouble(name + "z", vec.z);
    }

    /* todo @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        CompoundTag nbtTagCompound = new CompoundTag();
        this.writeToNBT(nbtTagCompound);
        return new SPacketUpdateTileEntity(this.pos, 1, nbtTagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }*/

    public void syncData(ServerPlayer player) {
//    	ERC_MessageRailStC packet = new ERC_MessageRailStC(
//    			xCoord, yCoord, zCoord, PosNum, 
//    			px, py, pz, nx, ny, nz,
//    			BaseRail.vecPos, BaseRail.vecDir, BaseRail.vecUp, 
//    			NextRail.vecPos, NextRail.vecDir, NextRail.vecUp, 
//    			BaseRail.Power, BaseRail.fUp, BaseRail.fDirTwist,
//    			NextRail.Power, NextRail.fUp, NextRail.fDirTwist
//    			);

//     todo   ERC_MessageRailStC packet = new ERC_MessageRailStC(this.getXcoord(), this.getYcoord(), this.getZcoord(), PosNum, modelrailindex);
//        packet.addRail(BaseRail);
//        packet.addRail(NextRail);
//        todo ERC_PacketHandler.INSTANCE.sendTo(packet, player);
//	    ERC_PacketHandler.INSTANCE.sendToAll(packet);
    }

    public void syncData() {
//       todo ERC_MessageRailStC packet = new ERC_MessageRailStC(this.getXcoord(), this.getYcoord(), this.getZcoord(), PosNum, modelrailindex);
//        packet.addRail(BaseRail);
//        packet.addRail(NextRail);
//        todo ERC_PacketHandler.INSTANCE.sendToAll(packet);
    }

    public void connectionFromBack(int x, int y, int z) {
        if (x == this.getPos().getX() && y == this.getPos().getY() && z == this.getPos().getZ()) return;

        this.SetPrevRailPosition(x, y, z);
        this.syncData();
    }

    public void connectionToNext(DataTileEntityRail next, int x, int y, int z) {
        if (x == this.getPos().getX() && y == this.getPos().getY() && z == this.getPos().getZ()) return;

        float power = ERC_MathHelper.CalcSmoothRailPower(BaseRail.vecDir, next.vecDir, BaseRail.vecPos, next.vecPos);
        this.BaseRail.Power = power;
        this.SetNextRailVectors(next, x, y, z);
        Wrap_BlockEntityRail prev = this.getPrevRailTileEntity();
        if (prev != null) {
            TileEntityRailBase r = prev.getRail();
            r.SetNextRailVectors(this.getRail());
            r.CalcRailLength();
            prev.syncData();
        }
//    	this.CreateNewRailVertexFromControlPoint();
        this.CalcRailLength();
        this.syncData();
    }

    private Vec3 rotateAroundX(Vec3 dir, double value) {
        double cosval = Math.cos(value);
        double sinval = Math.sin(value);
        return new Vec3(dir.x,
                dir.y * cosval - dir.z * sinval,
                dir.y * sinval + dir.z * cosval);
    }

    private Vec3 rotateAroundY(Vec3 dir, double value) {
        double cosval = Math.cos(value);
        double sinval = Math.sin(value);
        return new Vec3(dir.x * cosval + dir.z * sinval,
                dir.y,
                -dir.x * sinval + dir.z * cosval);
    }

    private Vec3 rotateAroundZ(Vec3 dir, double value) {
        double cosval = Math.cos(value);
        double sinval = Math.sin(value);
        return new Vec3(dir.x * cosval - dir.y * sinval,
                dir.x * sinval + dir.y * cosval,
                dir.z);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return BaseContainerBlockEntity.canUnlock(player, LockCode.NO_LOCK, this.getDisplayName()) ? new DefMenu(id, inv, this) : null;
    }

    public void writeMenu(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(worldPosition);
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container.rail");
    }
}
