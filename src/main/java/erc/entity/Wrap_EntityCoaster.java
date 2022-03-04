package erc.entity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public abstract class Wrap_EntityCoaster extends Entity {

    public float zRot;
    public float prevzRot;

    public Wrap_EntityCoaster(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean canBeRidden() {
        return true;
    }


    public void setPosition(double x, double y, double z) {
        this.setPos(x, y, z);

        double f = this.getBbWidth() / 2.0F;
        double f1 = this.getBbHeight() / 2.0F;
        this.setBoundingBox(new AABB(x - f, y - f1, z - f, x + f, y + f1, z + f));
    }

    public void setAngles(float deltax, float deltay) {
//    	ERC_CoasterAndRailManager.setAngles(deltax, deltay);
    }

    public void SyncCoasterMisc_Send(FriendlyByteBuf buf, int flag) {
    }

    public void SyncCoasterMisc_Receive(FriendlyByteBuf buf, int flag) {
    }

    public float getRoll(float partialTicks) {
        return prevzRot + (zRot - prevzRot) * partialTicks;
    }
}