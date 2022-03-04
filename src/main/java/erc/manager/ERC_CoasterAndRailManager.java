package erc.manager;

import erc.core.ERC_ReturnCoasterRot;
import erc.math.ERC_MathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

import erc.core.ERC_Logger;
import erc.entity.ERC_EntityCoaster;
import erc.entity.ERC_EntityCoasterSeat;
import erc.block.tileEntity.TileEntityRailBase;
import erc.block.tileEntity.Wrap_BlockEntityRail;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ERC_CoasterAndRailManager {
	private static final Vec3 UP = new Vec3(0, 1, 0);
	private static final Vec3 RIGHT = new Vec3(1, 0, 0);

	//	public static ERC_TileEntityRailTest prevTileRail;
	public static int prevX = -1;
	public static int prevY = -1;
	public static int prevZ = -1;
	public static int nextX = -1;
	public static int nextY = -1;
	public static int nextZ = -1;
	// R[X^[ݒuʒuNCAgɒm点p
	public static int coastersetX = -1;
	public static int coastersetY = -1;
	public static int coastersetZ = -1;
	// AR[X^[p@eR[X^[ID
	private static int parentCoasterID = -1;
	// fIp@fID
	public static int saveModelID = -1;
	// Ԓ_ړ
	public static float rotationViewYaw = 0f;
	public static float prevRotationViewYaw = 0f;
	public static float rotationViewPitch = 0f;
	public static float prevRotationViewPitch = 0f;
	// Jpx
	public static float rotationYaw = 0;
	public static float yRotO = 0;
	public static float rotationPitch = 0;
	public static float xRotO = 0;
	public static float zRot = 0f;
	public static float prevzRot = 0f;

	public static TileEntityRailBase clickedTileForGUI;

	public ERC_CoasterAndRailManager() {
		ResetData();
		clickedTileForGUI = null;
	}

	public static void SetPrevData(int x, int y, int z) {
		prevX = x;
		prevY = y;
		prevZ = z;
	}

	public static void SetNextData(int x, int y, int z) {
		nextX = x;
		nextY = y;
		nextZ = z;
	}

	public static void ResetData() {
		prevX = -1;
		prevY = -1;
		prevZ = -1;
		nextX = -1;
		nextY = -1;
		nextZ = -1;
	}

	public static boolean isPlacedRail() {
		return isPlacedPrevRail() || isPlacedNextRail();
	}

	public static boolean isPlacedPrevRail() {
		return prevY > -1;
	}

	public static boolean isPlacedNextRail() {
		return nextY > -1;
	}

	public static Wrap_BlockEntityRail GetPrevTileEntity(Level world) {
		return (Wrap_BlockEntityRail) world.getBlockEntity(new BlockPos(prevX, prevY, prevZ));
	}

	public static Wrap_BlockEntityRail GetNextTileEntity(Level world) {
		return (Wrap_BlockEntityRail) world.getBlockEntity(new BlockPos(nextX, nextY, nextZ));
	}

	public static void OpenRailGUI(TileEntityRailBase tl) {
		clickedTileForGUI = tl;

	}

	public static void CloseRailGUI() {
		clickedTileForGUI = null;
	}

	public static void SetCoasterPos(BlockPos pos) {
		coastersetX = pos.getX();
		coastersetY = pos.getY();
		coastersetZ = pos.getZ();
	}

	public static void client_setParentCoaster(ERC_EntityCoaster parent) {
		parentCoasterID = parent.getId();
	}

	public static ERC_EntityCoaster client_getParentCoaster(Level world) {
		ERC_EntityCoaster ret = (ERC_EntityCoaster) world.getEntity(parentCoasterID);
		parentCoasterID = -1;
		return ret;
	}

	//	@OnlyIn(Side.CLIENT)
	public static void setAngles(float deltax, float deltay) {
		float f2 = rotationViewPitch;
		float f3 = rotationViewYaw;
		rotationViewYaw = (float) ((double) rotationViewYaw + (double) deltax * 0.15D);
		rotationViewPitch = (float) ((double) rotationViewPitch + (double) deltay * 0.15D);

		if (rotationViewPitch < -80.0F) rotationViewPitch = -80.0F;
		if (rotationViewPitch > 80.0F) rotationViewPitch = 80.0F;
		if (rotationViewYaw < -150.0F) rotationViewYaw = -150.0F;
		if (rotationViewYaw > 150.0F) rotationViewYaw = 150.0F;

		prevRotationViewPitch += rotationViewPitch - f2;
		prevRotationViewYaw += rotationViewYaw - f3;
	}

	public static void resetViewAngles() {
		rotationViewYaw = 0f;
		prevRotationViewYaw = 0f;
		rotationViewPitch = 0f;
		prevRotationViewPitch = 0f;
		zRot = 0f;
		prevzRot = 0f;
	}

	public static void setRots(float y, float py, float p, float pp, float r, float pr) {
		rotationYaw = y;
		yRotO = py;
		rotationPitch = p;
		xRotO = pp;
		zRot = r;
		prevzRot = pr;
	}

	public static void setRotRoll(float r, float pr) {
		zRot = r;
		prevzRot = pr;
	}

	public static void CameraProc(float f) {
		Player player = Minecraft.getInstance().player;
		//Rotate coasterVec using rotation of coaster
		Entity e = player.getVehicle();
		if (e instanceof ERC_EntityCoasterSeat) {
			ERC_ReturnCoasterRot mat = ((ERC_EntityCoasterSeat) e).parent.ERCPosMat;
			Vec3 up = mat.up;
			//Vector to translate the world by relative to Coaster
			//The offset vector (0, 0, 0) puts the camera where the player's head would be
			//up=(0, 1, 0) (e.g. when the coaster is upright) puts the camera in the middle of the coaster
			//up=(0, -1, 0) puts the camera... in the wrong place
			//The offset vector (0, 2, 0) or (0, 2.5, 0) puts the camera in the appropriate place when upside down
			//up=(1, 0, 0) puts the camera in the right place!
			//up=(-1, 0, 0) puts the camera in the right place!
			//up=(0, 0, 1) puts the camera in the opposite place!
			//up=(0, 0, -1) puts the camera in the opposite place!
			//This is why we negate the z and y(translate) = -y(up) + 1.5
			Vec3 coasterVec = new Vec3(up.x, -up.y + 1.5, -up.z);
			//Align this vector with the player pitch and yaw
			Vec3 alignedToYaw = ERC_MathHelper.rotateAroundVector(coasterVec, UP, player.getYRot() * Math.PI / 180);
			Vec3 alignedToPitch = ERC_MathHelper.rotateAroundVector(alignedToYaw, RIGHT, player.getXRot() * Math.PI / 180);
			//Put rotation first, so it comes out, then turns at the point it came out to
			GL11.glRotatef(prevzRot + (zRot - prevzRot) * f, 0.0F, 0.0F, 1.0F);
			GL11.glTranslated(alignedToPitch.x, alignedToPitch.y, -alignedToPitch.z);
		}
	}


	static Vec3 dir;
	static double speed;
	static Player player;

	public static void GetOffAndButtobi(Player Player) {
		if (/*!Player.worldObj.isClientSide() &&*/ Player.isCrouching()) {
			if (Player.getVehicle() instanceof ERC_EntityCoasterSeat) {
				ERC_EntityCoasterSeat seat = (ERC_EntityCoasterSeat) Player.getVehicle();
				dir = seat.parent.ERCPosMat.Dir;
				player = Player;
				speed = seat.parent.Speed;
				//Player.motionX += seat.parent.Speed * dir.xCoord * 1;
				//Player.motionY += seat.parent.Speed * dir.yCoord * 1;
				//Player.motionZ += seat.parent.Speed * dir.zCoord * 1;
				ERC_Logger.info(dir.toString());
			}
		}
	}

	public static void motionactive() {
		player.getDeltaMovement().add(speed * dir.x * 1, speed * dir.y * 1, speed * dir.z * 1);
	}
}