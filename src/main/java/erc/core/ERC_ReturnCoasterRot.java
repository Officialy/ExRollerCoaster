package erc.core;

import net.minecraft.world.phys.Vec3;

public class ERC_ReturnCoasterRot {
    public Vec3 pos;
    public float roll;
    public float yaw;
    public float pitch;
    public float prevRoll;
    public float prevYaw;
    public float prevPitch;
    //	public DoubleBuffer rotmat;
    public float viewRoll;
    public float viewYaw;
    public float viewPitch;
    public Vec3 offsetX;
    public Vec3 offsetY;
    public Vec3 offsetZ;
    public Vec3 Dir;
    public Vec3 Pitch;
    public Vec3 up;

    public ERC_ReturnCoasterRot() {
        pos = new Vec3(0, 0, 0);

        roll = 0;
        yaw = 0;
        pitch = 0;
        viewRoll = 0;
        viewYaw = 0;
        viewPitch = 0;

        offsetX = new Vec3(1, 0, 0);
        offsetY = new Vec3(0, 1, 0);
        offsetZ = new Vec3(0, 0, 1);

        up = new Vec3(0, 1, 0);
        Dir = new Vec3(0, 0, 0);
    }

    public float getFixedRoll(float partialTicks) {
        return prevRoll + (roll - prevRoll) * partialTicks;
    }

    public float getFixedYaw(float partialTicks) {
        return prevYaw + (yaw - prevYaw) * partialTicks;
    }

    public float getFixedPitch(float partialTicks) {
        return prevPitch + (pitch - prevPitch) * partialTicks;
    }

    public void printInfo() {
        System.out.println("Coaster transform info");
        System.out.println("Position:\nx: " + pos.x + "\ny: " + pos.y + "\nz: " + pos.z);
        System.out.println("Rotation:\npitch: " + pitch + "\nyaw: " + yaw + "\n roll: " + roll);
    }
}
