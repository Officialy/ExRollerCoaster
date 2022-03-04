package erc.block.tileEntity;

import net.minecraft.core.BlockPos;
import com.mojang.math.Vector3d;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class DataTileEntityRail {
	public Vec3 vecPos;
	public Vec3 vecDir;
	public Vec3 vecUp;
	public float fUp;
	public float fDirTwist;
	public float Power;	// 	Power̂BaseNext̊Ԃŋ
	public int cx, cy, cz; // connection rail OǂɌqĂ邩
	
	public DataTileEntityRail()
	{
		vecPos = new Vec3(0, 0, 0);
		vecDir = new Vec3(0, 0, 0);
		vecUp = new Vec3(0, 0, 0);
		fUp = 0;
		fDirTwist = 0;
		Power = 25f;
		cx = cy = cz = -1;
	}
	
	public void SetData(Vec3 pos, Vec3 dir, Vec3 up, float fup, float fdir, float pow, int x, int y, int z)
	{
		vecPos = pos;
		vecDir = dir;
		vecUp = up;
		fUp = fup;
		fDirTwist = fdir;
		Power = pow;
		cx = x;
		cy = y;
		cz = z;
	}
	
	public void SetData(DataTileEntityRail src)
	{
		vecPos = src.vecPos;                      
		vecDir = src.vecDir;                    
		vecUp = src.vecUp;
		fUp = src.fUp;
		fDirTwist = src.fDirTwist;
		Power = src.Power;
		cx = src.cx;
		cy = src.cy;
		cz = src.cz;
	}
	
	public void SetPos(int x, int y, int z)
	{
		cx = x;
		cy = y;
		cz = z;
	}
	
	public void addFUp(float rot)
	{
		fUp += rot; if(fUp > 1f)fUp=1f; else if(fUp < -1f)fUp=-1f;
	}
	public void addTwist(float rot)
	{
		fDirTwist -= rot; if(fDirTwist > 1f)fDirTwist=1f; else if(fDirTwist < -1f)fDirTwist=-1f;
	}
	public void resetRot()
	{
		fUp = 0;
		fDirTwist = 0;
	}

	//FT vecUp is a normalised up direction, which fUp scales
	//FT vecDir is general direction of entire rail
	//FT pow is
	//FT((vecUp * fUp) + vecDir) is the resultant of the dir and up vectors
	//FT((vecUp * fUp) + vecDir) * pow
	public Vec3 CalcVec3DIRxPOW(float pow)
	{
		return new Vec3(
				(vecDir.x+vecUp.x*fUp)*pow,
				(vecDir.y+vecUp.y*fUp)*pow,
				(vecDir.z+vecUp.z*fUp)*pow);
	}
	public Vec3 CalcVec3UpTwist()
	{
		Vec3 vecpitch1 = vecUp.cross(vecDir).normalize();
		return new Vec3(
				vecUp.x+vecpitch1.x*fDirTwist,
				vecUp.y+vecpitch1.y*fDirTwist,
				vecUp.z+vecpitch1.z*fDirTwist).normalize();
	}
	
	public Wrap_BlockEntityRail getConnectionTileRail(Level world)
	{
		return (Wrap_BlockEntityRail) world.getBlockEntity(new BlockPos(cx, cy, cz));
	}
	
}
