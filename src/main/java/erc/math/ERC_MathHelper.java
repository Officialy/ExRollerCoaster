package erc.math;

import net.minecraft.world.phys.Vec3;

public class ERC_MathHelper {
	
	public static class Vec4{
		float x; float y; float z; float w;
		public Vec4(){x=0;y=0;z=0;w=0;}
		public Vec4(float x,float y,float z,float w){this.x = x; this.y = y; this.z = z; this.w = w;}
		public Vec4(double x,double y,double z,double w){this.x = (float) x; this.y = (float) y; this.z = (float) z; this.w = (float) w;}
	}
	
	public static Vec3 Spline(float t, Vec3 base, Vec3 next, Vec3 dir1, Vec3 dir2)
	{
//		Vec4 vec_t = new Vec4(t,t*t,t*t*t,1.0f);

//		Matrix4f spline_c = new Matrix4f();
//		spline_c.m00 = 2f; spline_c.m01 = -2f; spline_c.m02 = 1f; spline_c.m03 = 1f;
//		spline_c.m10 = -3f; spline_c.m11 = 3f; spline_c.m12 = -2f; spline_c.m13 = -1f;
//		spline_c.m20 = 0f; spline_c.m21 = 0f; spline_c.m22 = 1f; spline_c.m23 = 0f;
//		spline_c.m30 = 1f; spline_c.m31 = 0f; spline_c.m32 = 0f; spline_c.m33 = 0f;
//
//		Matrix4f spline_V = new Matrix4f();
//		spline_V.m00 = (float) base.xCoord; spline_V.m01 = (float)base.yCoord; spline_V.m02 = (float)base.zCoord; spline_V.m03 = 1f;
//		spline_V.m10 = (float) next.xCoord; spline_V.m11 = (float)next.yCoord; spline_V.m12 = (float)next.zCoord; spline_V.m13 = 1f;
//		spline_V.m20 = (float) dir1.xCoord; spline_V.m21 = (float)dir1.yCoord; spline_V.m22 = (float)dir1.zCoord; spline_V.m23 = 0f;
//		spline_V.m30 = (float) dir2.xCoord; spline_V.m31 = (float)dir2.yCoord; spline_V.m32 = (float)dir2.zCoord; spline_V.m33 = 0f;
//		
//		Matrix4f spline_c = new Matrix4f(
//				2f,-3f,0f,1f,
//				-2f,3f,0f,0f,
//				1f,-2f,1f,0f,
//				1f,-1f,0f,0f);
//		
//		Matrix4f spline_V = new Matrix4f(
//				(float)base.xCoord,
//				(float)next.xCoord,
//				(float)dir1.xCoord,
//				(float)dir2.xCoord,
//				(float)base.yCoord,
//				(float)next.yCoord,
//				(float)dir1.yCoord,
//				(float)dir2.yCoord,
//				(float)base.zCoord,
//				(float)next.zCoord,
//				(float)dir1.zCoord,
//				(float)dir2.zCoord,
//				1f,1f,0f,0f
//				);
		
		float t2 = t*t;
		float t3 = t2*t;
		Vec4 vt = new Vec4(2f*t3-3f*t2+1f, -2f*t3+3f*t2, t3-2f*t2+t, t3-t2);
				
		Vec4 ans = new Vec4(
			(float)base.x*vt.x+
			(float)next.x*vt.y+
			(float)dir1.x*vt.z+
			(float)dir2.x*vt.w,
			(float)base.y*vt.x+
			(float)next.y*vt.y+
			(float)dir1.y*vt.z+
			(float)dir2.y*vt.w,
			(float)base.z*vt.x+
			(float)next.z*vt.y+
			(float)dir1.z*vt.z+
			(float)dir2.z*vt.w,
			1f*vt.x+
			1f*vt.y);
//		vec_t = Matrix4f.transform(spline_c, vec_t, vec_t);
//		vec_t = Matrix4f.transform(spline_V, vec_t, vec_t);
//		spline_V.mul(spline_c);
//		spline_V.transform(vec_t);
		
		return new Vec3(ans.x, ans.y, ans.z);
	}
	
	public static Vec3 Lerp(float t, Vec3 base, Vec3 next)
	{
		return new Vec3(
				base.x*(1-t)+next.x*t,
				base.y*(1-t)+next.y*t,
				base.z*(1-t)+next.z*t
				);
	}
	
	public static float CalcSmoothRailPower(Vec3 dirBase, Vec3 dirNext, Vec3 vecBase, Vec3 vecNext)
	{
		double dot = dirBase.dot(dirNext);
    	double len = vecBase.distanceTo(vecNext);
    	float f = (float) ((-dot+1d)*3d+len*0.8);
    	//ERC_Logger.info("math helper = "+f+", distance = "+len+", dot = "+dot);
    	return f;
	}
	
//	public static void CalcCoasterRollMatrix(ERC_ReturnCoasterRot out, Vec3 Pos, Vec3 Dir, Vec3 Up)
//	{
//		Vec3 zaxis = Dir.normalize();//Pos.subtract(At).normalize();
//		Vec3 xaxis = zaxis.cross(Up).normalize();
//		Vec3 yaxis = xaxis.cross(zaxis);
//
////		out.rotmat.clear();
//////		out.rotmat.put(xaxis.xCoord).put(yaxis.xCoord).put(-zaxis.xCoord).put(0);
//////		out.rotmat.put(xaxis.yCoord).put(yaxis.yCoord).put(-zaxis.yCoord).put(0);
//////		out.rotmat.put(xaxis.zCoord).put(yaxis.zCoord).put(-zaxis.zCoord).put(0);
//////		out.rotmat.put(0).put(0).put(0).put(1);
////		out.rotmat.put(xaxis.xCoord).put(xaxis.yCoord).put(xaxis.zCoord).put(0);
////		out.rotmat.put(yaxis.xCoord).put(yaxis.yCoord).put(yaxis.zCoord).put(0);
////		out.rotmat.put(zaxis.xCoord).put(zaxis.yCoord).put(zaxis.zCoord).put(0);
////		out.rotmat.put(0).put(0).put(0).put(1);
////		out.rotmat.flip();
//	}
	
	public static double angleTwoVec3(Vec3 a, Vec3 b)
	{
//		@SuppressWarnings("unused")
//		double temp = a.normalize().dotProduct(b.normalize());
		return Math.acos( clamp(a.normalize().dot(b.normalize())) );
	}
	
	public static double clamp(double a)
	{
		return a>1d?1d:(a<-1d?-1d:a);
	}
	
	public static float wrap(float a)
	{
		if(a >  Math.PI)a -= Math.PI*2;
		if(a < -Math.PI)a += Math.PI*2;
		return a;
	}
	
	public static float Lerp(float t, float a1, float a2)
	{
		return a1*(1-t) + a2*t;
	}
	
	public static Vec3 rotateAroundVector(Vec3 rotpos, Vec3 axis, double radian)
	{
		radian *= 0.5;
		Vec4 Qsrc = new Vec4(0,rotpos.x,rotpos.y,rotpos.z);
		Vec4 Q1 = new Vec4(Math.cos(radian), axis.x*Math.sin(radian), axis.y*Math.sin(radian), axis.z*Math.sin(radian));
		Vec4 Q2 = new Vec4(Math.cos(radian),-axis.x*Math.sin(radian),-axis.y*Math.sin(radian),-axis.z*Math.sin(radian));
	
		Vec4 ans = MulQuaternion(MulQuaternion(Q2, Qsrc), Q1);
		return new Vec3(ans.y, ans.z, ans.w);
	}

	private static Vec4 MulQuaternion(Vec4 q1, Vec4 q2)
	{
		return new Vec4(
				q1.x*q2.x - (q1.y*q2.y+q1.z*q2.z+q1.w*q2.w),
				q1.x*q2.y + q2.x*q1.y + (q1.z*q2.w - q1.w*q2.z),
				q1.x*q2.z + q2.x*q1.z + (q1.w*q2.y - q1.y*q2.w),
				q1.x*q2.w + q2.x*q1.w + (q1.y*q2.z - q1.z*q2.y)
				);
	}

	public static Vec3 Slerp(float t, Vec3 base, Vec3 Goal)
	{
		double theta = Math.acos(clamp(base.dot(Goal)));
		if(theta == 0 || theta == 1d)return base;
		double sinTh = Math.sin(theta);
		double Pb = Math.sin(theta*(1-t));
		double Pg = Math.sin(theta*t);
		return new Vec3(
				(base.x*Pb + Goal.x*Pg)/sinTh,
				(base.y*Pb + Goal.y*Pg)/sinTh,
				(base.z*Pb + Goal.z*Pg)/sinTh);
	}
	
	public static float fixrot(float rot, float prevrot)
    {
    	if(rot - prevrot>180f)prevrot += 360f;
        else if(rot - prevrot<-180f)prevrot -= 360f;
    	return prevrot;
    }
}
