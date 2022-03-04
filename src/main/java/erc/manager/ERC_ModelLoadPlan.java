package erc.manager;

import erc.manager.ERC_ModelLoadManager.ModelOptions;

public class ERC_ModelLoadPlan {

    private final String ModelName;
    private final String TextureName;
    private final String IconName;
    private final ModelOptions Option = new ModelOptions();

    public String getModelName() {
        return ModelName;
    }

    public String getTextureName() {
        return TextureName;
    }

    public String getIconName() {
        return IconName;
    }

    public ModelOptions getOption() {
        return Option;
    }

    public ERC_ModelLoadPlan(String ObjName, String TextureName, String IconName) {
        this.ModelName = ObjName;
        this.TextureName = TextureName;
        this.IconName = IconName;
        Option.setSeatNum(1);
    }

    public boolean setCoasterMainData(float length, float width, float height, boolean canRide) {
        Option.Length = length;
        Option.Width = width;
        Option.Height = height;
        Option.canRide = canRide;
        return true;
    }

    public boolean setSeatNum(int num) {
        if (num < 1) return false;
        Option.setSeatNum(num);
        return true;
    }

    public boolean setSeatOffset(int index, float offsetX, float offsetY, float offsetZ) {
        if (index < 0 || index >= Option.SeatNum) return false;
        Option.offsetX[index] = offsetX;
        Option.offsetY[index] = offsetY;
        Option.offsetZ[index] = offsetZ;
        return true;
    }

    public boolean setSeatRotation(int index, float rotX, float rotY, float rotZ) {
        if (index < 0 || index >= Option.SeatNum) return false;
        Option.rotX[index] = rotX;
        Option.rotY[index] = rotY;
        Option.rotZ[index] = rotZ;
        return true;
    }

    public boolean setSeatSize(int index, float size) {
        if (index < 0 || index >= Option.SeatNum) return false;
        Option.size[index] = size;
        return true;
    }
}
