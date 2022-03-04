package erc.manager;

import java.io.File;
import java.util.*;

import erc.core.ERCConstants;
import erc.core.ERC_Logger;
import erc.entity.ERC_EntityCoaster;
import erc.model.ERC_ModelAddedRail;
import erc.model.ERC_ModelCoaster;
import erc.model.ERC_ModelDefaultRail;
import erc.model.Wrap_RailRenderer;
import erc.block.tileEntity.TileEntityRailBase;
import erc.block.tileEntity.TileEntityRailBranch2;
import erc.block.tileEntity.TileEntityRailConstVelocity;
import erc.block.tileEntity.TileEntityRailDetector;
import erc.block.tileEntity.TileEntityRailInvisible;
import erc.block.tileEntity.TileEntityRailRedstoneAccelerator;
import erc.block.tileEntity.Wrap_BlockEntityRail;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.obj.OBJModel;

public class ERC_ModelLoadManager {

    public static class ModelPack {
        public OBJModel.ModelSettings Model;
        public ResourceLocation Tex;
        public String IconStr;
        public ModelOptions op;

        public ModelPack(OBJModel.ModelSettings m, ResourceLocation t, String ic, ModelOptions op) {
            Model = m;
            Tex = t;
            IconStr = ic;
            if (op == null) this.op = new ModelOptions();
            else this.op = op;
        }
    }

//	public static class ModelPack2{
//		public IModelCustom MainModel;
//		public IModelCustom ConnectModel;
//		public ResourceLocation MainTex;
//		public ResourceLocation ConnectTex;
//		public String IconStr;
//		public String ConnectIconStr;
//		public ModelOptions op[];
//		public ModelPack2(IModelCustom mm, ResourceLocation mt, IModelCustom cm, ResourceLocation ct, String ic, String cic)
//		{
//			MainModel = mm; ConnectModel = cm; MainTex = mt; ConnectTex = ct; IconStr = ic; ConnectIconStr = cic;
//			op = new ModelOptions[2];
//			for(int i=0; i<2; ++i) op[i] = new ModelOptions();
//		}
//		public ERC_ModelCoaster getModelSet(int flag)
//		{
//			switch(flag)
//			{
//			case 0 : return new ERC_ModelCoaster(MainModel, MainTex);
//			case 1 : return new ERC_ModelCoaster(ConnectModel, ConnectTex);
//			}
//			return null;
//		}
//	}

    public static class ModelOptions {
        public float Weight = 1.0f;
        public float Width = 2.0f;
        public float Height = 2.0f;
        public float Length = 1.5f;    //ڑ
        public boolean canRide = true;
        public int SeatNum = 0;
        public float[] offsetX;
        public float[] offsetY;
        public float[] offsetZ;
        public float[] rotX;
        public float[] rotY;
        public float[] rotZ;
        public float[] size;

        public ModelOptions() {
        }

        public void setSeatNum(int num) {
            SeatNum = num;
            offsetX = new float[num];
            offsetY = new float[num];
            offsetZ = new float[num];
            rotX = new float[num];
            rotY = new float[num];
            rotZ = new float[num];
            size = new float[num];
            for (int i = 0; i < num; ++i) size[i] = 1.0f;
        }

        public void WriteBuf(FriendlyByteBuf buf) {
            buf.writeBoolean(canRide);
            buf.writeFloat(Width);
            buf.writeFloat(Height);
            buf.writeFloat(Length);
            buf.writeInt(SeatNum);
            for (int i = 0; i < SeatNum; ++i) {
                buf.writeFloat(offsetX[i]);
                buf.writeFloat(offsetY[i]);
                buf.writeFloat(offsetZ[i]);
                buf.writeFloat(rotX[i]);
                buf.writeFloat(rotY[i]);
                buf.writeFloat(rotZ[i]);
                buf.writeFloat(size[i]);
            }
        }

        public void ReadBuf(FriendlyByteBuf buf) {
            canRide = buf.readBoolean();
            Width = buf.readFloat();
            Height = buf.readFloat();
            Length = buf.readFloat();
            setSeatNum(buf.readInt());
//			SeatLine = buf.readInt();
            for (int i = 0; i < SeatNum; ++i) {
                offsetX[i] = buf.readFloat();
                offsetY[i] = buf.readFloat();
                offsetZ[i] = buf.readFloat();
                rotX[i] = buf.readFloat();
                rotY[i] = buf.readFloat();
                rotZ[i] = buf.readFloat();
                size[i] = buf.readFloat();
            }
        }

        public void WriteToNBT(CompoundTag nbt) {
            nbt.putBoolean("op_canride", canRide);
            nbt.putFloat("op_width", Width);
            nbt.putFloat("op_height", Height);
            nbt.putFloat("op_length", Length);
            nbt.putInt("op_seatnum", SeatNum);
//			nbt.putInt("op_seatline", SeatLine);
            for (int i = 0; i < SeatNum; ++i) {
                nbt.putFloat("op_seatx" + i, offsetX[i]);
                nbt.putFloat("op_seaty" + i, offsetY[i]);
                nbt.putFloat("op_seatz" + i, offsetZ[i]);
                nbt.putFloat("op_seatrx" + i, rotX[i]);
                nbt.putFloat("op_seatry" + i, rotY[i]);
                nbt.putFloat("op_seatrz" + i, rotZ[i]);
                nbt.putFloat("op_seats" + i, size[i]);
            }
        }

        public void ReadFromNBT(CompoundTag nbt) {
            canRide = nbt.getBoolean("op_canride");
            Width = nbt.getFloat("op_width");
            Height = nbt.getFloat("op_height");
            Length = nbt.getFloat("op_length");
            setSeatNum(nbt.getInt("op_seatnum"));
//			SeatLine = nbt.getInt("op_seatline");
            for (int i = 0; i < SeatNum; ++i) {
                offsetX[i] = nbt.getFloat("op_seatx" + i);
                offsetY[i] = nbt.getFloat("op_seaty" + i);
                offsetZ[i] = nbt.getFloat("op_seatz" + i);
                rotX[i] = nbt.getFloat("op_seatrx" + i);
                rotY[i] = nbt.getFloat("op_seatry" + i);
                rotZ[i] = nbt.getFloat("op_seatrz" + i);
                size[i] = nbt.getFloat("op_seats" + i);
            }
        }
    }

    public static class RailPack {
        public OBJModel.ModelSettings[] RailModels;
        public ResourceLocation[] RailTexs;
        public String IconStr;

        public RailPack(OBJModel.ModelSettings[] Models, ResourceLocation[] texs, String ic) {
            RailModels = Models;
            RailTexs = texs;
            IconStr = ic;
        }

        public ERC_ModelAddedRail getModelSet(int flag) {
            if (flag < 0 || flag > 4) flag = 0;

            return new ERC_ModelAddedRail(RailModels[flag], RailTexs[flag]);
        }
    }

    private static final Map<String, OBJModel.ModelSettings> MapModel = new HashMap<>();
    private static final Map<String, ResourceLocation> MapTex = new HashMap<>();
    //	private static List<ModelPack2> ModelPackList = new ArrayList<ModelPack2>();
    private static final List<ModelPack> ModelPackList_Main = new ArrayList<>();
    public static final List<ModelPack> ModelPackList_Connect = new ArrayList<>();
    private static final List<ModelPack> ModelPackList_Mono = new ArrayList<>();
    private static final List<RailPack> RailPackList = new ArrayList<>();

    private static final String AddModelAdd = "./mods/ERC_AddModels/assets/" + ERCConstants.D_AM + "/";
//	private static final String ExternalFileAdd = "../../../"; // fobOp		TODO
//	private static final String ExternalFileAdd = "../../../../"; // pbP[Wp

    public static void init() {
//    	addObj("../../../../model.obj");
//    	ERC_Logger.info("file.obj ... OK");

        // R[X^[ftHo^
//		ERC_Logger.info("LoadManager Init");
//		addObj(defaultModel);
//		addObj(defaultModel_c);
//		addTexture(defaultTex);
//		ModelPack2 mp = addModelPack2(defaultModel, defaultTex, defaultModel_c, defaultTex, defaultIcon, defaultIcon_c);
//		readOptionsFromFileName(mp, "file.sn1", 0);
//		readOptionsFromFileName(mp, "file.sn1", 1);

        File ercfirCoaster = new File(AddModelAdd + "Coaster/");
//		ercfirCoaster.mkdirs();
        File ercfirRail = new File(AddModelAdd + "Rail/");
//		ercfirRail.mkdirs();

        String[] modeldirsC = ercfirCoaster.list();
        String[] modeldirsR = ercfirRail.list();

        if (modeldirsC == null) return;
        for (String strDirName : modeldirsC) {
            ERC_Logger.info("folder open:" + "/Coaster/" + strDirName);
            registerCoaster("Coaster/" + strDirName);
        }
        if (modeldirsR == null) return;
        for (String strDirName : modeldirsR) {
            ERC_Logger.info("folder open:" + "/Rail/" + strDirName);
            registerRail("Rail/" + strDirName);
        }
    }

    private static void registerCoaster(String str) {
        File dir = new File(AddModelAdd + str);
        if (!dir.isDirectory()) return;

        int modelFlags = 0;
        String ObjName = null;//defaultModel;
        String cObjName = null;//defaultModel_c;
        String mObjName = null;
        String TexName = null;//defaultTex;
        String cTexName = null;//defaultTex_c;
        String mTexName = null;
        String IconName = null;//defaultIcon;
        String cIconName = null;//defaultIcon_c;
        String mIconName = null;
        String OptionFileNameMain = "";
        String OptionFileNameConnect = "";
        String OptionFileNameMono = "";

        String[] fileNames = dir.list();
        for (String fname : fileNames) {
            ERC_Logger.info("file open:" + str + "/" + fname);
//			File file = new File(str+"/"+fname);

            //////// model_c.obj
            if (fname.matches(".*model_c\\..*.obj$")) {
                cObjName = ERCConstants.D_AM + ":" + str + "/" + fname;
                OptionFileNameConnect = fname;
                addObj(cObjName);
                modelFlags |= 2;
            }
            //////// model_m.obj
            if (fname.matches(".*model_m\\..*.obj$")) {
                mObjName = ERCConstants.D_AM + ":" + str + "/" + fname;
                OptionFileNameMono = fname;
                addObj(mObjName);
                modelFlags |= 4;
            }
            //////// model.obj
            if (fname.matches(".*model\\..*obj$")) {
                ObjName = ERCConstants.D_AM + ":" + str + "/" + fname;
                OptionFileNameMain = fname;
                addObj(ObjName);
                modelFlags |= 1;
            }

            //////// tex.png
            if (fname.matches(".*tex\\.*png$")) {
                TexName = ERCConstants.D_AM + ":" + str + "/" + fname;
                addTexture(TexName);
            }
            //////// tex_c.png
            if (fname.matches(".*tex_c\\.*png$")) {
                cTexName = ERCConstants.D_AM + ":" + str + "/" + fname;
                addTexture(cTexName);
            }
            //////// tex_m.png
            if (fname.matches(".*tex_m\\.*png$")) {
                mTexName = ERCConstants.D_AM + ":" + str + "/" + fname;
                addTexture(mTexName);
            }

            //////// icon.png
            if (fname.matches(".*icon\\.*png$")) {
                IconName = removeExtention(ERCConstants.D_AM + ":" + "../../" + str + "/" + fname);
//	        	addTexture(IconName);
            }
            //////// icon.png
            if (fname.matches(".*icon_c\\.*png$")) {
                cIconName = removeExtention(ERCConstants.D_AM + ":" + "../../" + str + "/" + fname);
//	        	addTexture(cIconName);
            }
            //////// icon.png
            if (fname.matches(".*icon_m\\.*png$")) {
                mIconName = removeExtention(ERCConstants.D_AM + ":" + "../../" + str + "/" + fname);
//	        	addTexture(mIconName);
            }
        }

//		ModelPack2 mp = addModelPack2(ObjName, TexName, cObjName, cTexName, IconName, cIconName);
        ModelPack mp = null;
        ModelOptions op = new ModelOptions();
        if ((modelFlags & 1) == 1) {
            readOptionsFromFileName(op, OptionFileNameMain);
            mp = new ModelPack(getObj(ObjName), getTex(TexName), IconName, op);
            ModelPackList_Main.add(mp);
        }
        if ((modelFlags & 2) == 2) {
            readOptionsFromFileName(op, OptionFileNameConnect);
            mp = new ModelPack(getObj(cObjName), getTex(cTexName), cIconName, op);
            ModelPackList_Connect.add(mp);
        }
        if ((modelFlags & 4) == 4) {
            readOptionsFromFileName(op, OptionFileNameMono);
            mp = new ModelPack(getObj(mObjName), getTex(mTexName), mIconName, op);
            ModelPackList_Mono.add(mp);
        }
    }

    private static void registerRail(String str) {
        File dir = new File(AddModelAdd + str);
        if (!dir.isDirectory()) return;

        String[] Formats = {"n", "r", "c", "d", "b"};
        String[] ObjNames = {"null", "null", "null", "null", "null"};
        String[] TexNames = {"null", "null", "null", "null", "null"};
        String IconName = "";

//		String OptionFileNameMain = "";
//		String OptionFileNameConnect = "";

        //obj
        String[] objfile = dir.list();
        for (String fname : objfile) {
            ERC_Logger.info("filename:" + str + "/" + fname);
//			File file = new File(str+"/"+fname);

            for (int i = 0; i < Formats.length; ++i) {
                if (fname.matches(".*\\." + Formats[i] + "\\..*obj")) {
                    String name = ERCConstants.D_AM + ":" + str + "/" + fname;
                    addObj(name);
                    ObjNames[i] = name;
                    break;
                }
                if (fname.matches(".*\\." + Formats[i] + "\\..*png")) {
                    String name = ERCConstants.D_AM + ":" + str + "/" + fname;
                    addTexture(name);
                    TexNames[i] = name;
                    break;
                }
            }
            if (fname.matches("^icon\\.png$")) {
                IconName = removeExtention(ERCConstants.D_AM + ":" + "../../" + str + "/" + fname);
            }
        }

        for (int i = 0; i < ObjNames.length; ++i) if (ObjNames[i].matches("^null$")) ObjNames[i] = ObjNames[0];
        for (int i = 0; i < TexNames.length; ++i) if (TexNames[i].matches("^null$")) TexNames[i] = TexNames[0];

        if (ObjNames[0].matches("^null$")) return;

        addRailModelPack(ObjNames, TexNames, IconName);
    }

    public static void readOptionsFromFileName(ModelOptions Options, String filename) {
        String op;
        filename = removeExtention(filename);//gq
        while ((op = getExtention(filename)) != null) {
            if (!op.matches("^[a-zA-Z]+[0-9]+_*-*[0-9]*$")) {
                filename = removeExtention(filename);
                continue;
            }
            char flag = op.charAt(0);
            switch (flag) {
                case 'L'/**/ -> Options.Length = Float.parseFloat(op.substring(1)) * 0.01f;
                case 'g'/*d*/ -> Options.Weight = Float.parseFloat(op.substring(1)) * 0.01f;
                case 'w'/*@*/ -> Options.Width = Float.parseFloat(op.substring(1)) * 0.01f;
                case 'h'/**/ -> Options.Height = Float.parseFloat(op.substring(1)) * 0.01f;
                case 's'/**/ -> subOpstions(Options, op.charAt(1), op.substring(2));
            }
            filename = removeExtention(filename);
        }
    }

    public static void subOpstions(ModelOptions op, char flag, String opstr) {
        int idx = 0;
        int num = 0;
        boolean numflag;
        if (flag != 'n') {
            do {
                num = num * 10 + Character.getNumericValue(opstr.charAt(idx));
                if (idx + 1 >= opstr.length()) break;
                idx += 1;
                char next = opstr.charAt(idx);
                numflag = next >= '0' && next <= '9';
            }
            while (numflag);
            opstr = opstr.substring(idx + 1);
            --num;
            if (num < 0) return;
        }
        switch (flag) {
            case 'n' /*op:s*/ -> op.setSeatNum(Integer.parseInt(opstr));
            case 'x' /*op:s*/ -> op.offsetX[num] = Integer.parseInt(opstr) * 0.01f;
            case 'y' /*op:s*/ -> op.offsetY[num] = Float.parseFloat(opstr) * 0.01f;
            case 'z' /*op:s*/ -> op.offsetZ[num] = Float.parseFloat(opstr) * 0.01f;
            case 'r' /*op:s]*/ -> op.rotZ[num] = Float.parseFloat(opstr) * 0.01f;
        }
    }

    public static void addObj(String filename) {
        if (MapModel.containsKey(filename)) return;
        try {
            MapModel.put(filename, new OBJModel.ModelSettings(new ResourceLocation(filename), true, true, false, true, null));
        } catch (Exception e) {
            System.out.println("Failed to load OBJ model " + filename);
        }
    }
//	public static void addObjCustom(String filename)
//	{
//		if(MapModel.containsKey(filename))return;	
//		MapModel.put(filename, new ercWavefrontObject(new ResourceLocation(filename)));
//	}

    public static void addTexture(String filename) {
        if (MapTex.containsKey(filename)) return;
        MapTex.put(filename, new ResourceLocation(filename));
    }

    public static OBJModel.ModelSettings getObj(String filename) {
        return MapModel.get(filename);
    }

    public static ResourceLocation getTex(String filename) {
        return MapTex.get(filename);
    }

//	public static ModelPack2 addModelPack2(String MainObjName, String MainTexName, String ConnectObjName, String ConnectTexName, String IconName, String connectIconName)
//	{
//		ERC_Logger.info("additional model register : "+MainObjName+", "+MainTexName+", "+ConnectObjName+", "+ConnectTexName+", "+IconName);
//		ModelPack2 modelpack = new ModelPack2(getObj(MainObjName), getTex(MainTexName), getObj(ConnectObjName), getTex(ConnectTexName), IconName, connectIconName);
////		ModelPackList.add(modelpack);
//		return modelpack;
//	}

    public static void addRailModelPack(String[] ObjNames, String[] TexNames, String iconName) {
        if (ObjNames.length != TexNames.length) return;
        OBJModel.ModelSettings[] models = new OBJModel.ModelSettings[ObjNames.length];
        ResourceLocation[] texs = new ResourceLocation[TexNames.length];
        for (int i = 0; i < ObjNames.length; ++i) models[i] = getObj(ObjNames[i]);
        for (int i = 0; i < TexNames.length; ++i) texs[i] = getTex(TexNames[i]);
        RailPack railpack = new RailPack(models, texs, iconName);
        RailPackList.add(railpack);
    }

    public static ERC_ModelCoaster<ERC_EntityCoaster> getModel(int Index, int CoasterType) {
        ModelPack mp;
        switch (CoasterType) {
            case 1:
                Index %= ModelPackList_Connect.size();
                mp = ModelPackList_Connect.get(Index);
            case 2:
                Index %= ModelPackList_Mono.size();
                mp = ModelPackList_Mono.get(Index);
            case 0:
            default:
                Index %= ModelPackList_Main.size();
                mp = ModelPackList_Main.get(Index);

        }
        return new ERC_ModelCoaster<>(mp.Model, mp.Tex);
    }

    public static ModelOptions getModelOP(int Index, int CoasterType) {
        if (Index < 0) Index = 0;
        switch (CoasterType) {
            case 1:
                Index %= ModelPackList_Connect.size();
                return ModelPackList_Connect.get(Index).op;
            case 2:
                Index %= ModelPackList_Mono.size();
                return ModelPackList_Mono.get(Index).op;
            case 0:
            default:
                Index %= ModelPackList_Main.size();
                return ModelPackList_Main.get(Index).op;
        }
    }

    public static ERC_ModelAddedRail getRailModel(int Index, int flag) {
        if (Index < 0 || Index >= RailPackList.size()) Index = 0;
        if (flag < 0 || flag >= 6) flag = 0;
        return RailPackList.get(Index).getModelSet(flag);
    }

    public static Wrap_RailRenderer createRailRenderer(int Index, Wrap_BlockEntityRail tile) {
        if (Index <= 0 || Index > 6)
            return new ERC_ModelDefaultRail(RenderType::entitySolid);
        int flag = 0;
        if (tile instanceof TileEntityRailRedstoneAccelerator) flag = 1;
        else if (tile instanceof TileEntityRailConstVelocity) flag = 2;
        else if (tile instanceof TileEntityRailDetector) flag = 3;
        else if (tile instanceof TileEntityRailBranch2) flag = 4;
        else if (tile instanceof TileEntityRailInvisible) flag = 5;
        else if (tile instanceof TileEntityRailBase) flag = 0;
        return getRailModel(Index - 1, flag);
    }

    public static String[] getCoasterIconStrings(int CoasterType) {
        String[] ret;
        List<ModelPack> list = switch (CoasterType) {
            case 0 -> ModelPackList_Main;
            case 1 -> ModelPackList_Connect;
            case 2 -> ModelPackList_Mono;
            default -> null;
        };
        ret = new String[list.size()];
        int idx = 0;
        for (ModelPack mp : list) {
            ret[idx++] = mp.IconStr;
        }
        return ret;
    }

    public static String[] getRailIconStrings() {
        String[] ret = new String[RailPackList.size() + 1];
        ret[0] = "erc:railicondef";
        int idx = 1;
        for (RailPack rp : RailPackList) {
            ret[idx++] = rp.IconStr;
        }
        return ret;
    }

    public static int getModelPackNum(int CoasterType) {
        return switch (CoasterType) {
            case 1 -> ModelPackList_Connect.size();
            case 2 -> ModelPackList_Mono.size();
            case 0 -> ModelPackList_Main.size();
            default -> ModelPackList_Main.size();
        };

    }

    public static int getRailPackNum() {
        return RailPackList.size();
    }

    // gq؂ƂIvV؂Ƃ
    private static String removeExtention(String fileName) {
        if (fileName == null)
            return null;
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(0, point);
        }
        return fileName;
    }

    // IvV̔o
    private static String getExtention(String fileName) {
        if (fileName == null)
            return null;
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            return fileName.substring(point + 1);
        }
        return null; //IvV炱
    }


    private static ModelPack makeModelPack(ERC_ModelLoadPlan mp) {
        ERC_Logger.info("additional model is registered: " + mp.getModelName() + ", " + mp.getTextureName() + ", " + mp.getIconName() + ".png");
        try {
            addObj(mp.getModelName());
        } catch (Exception e) {
            ERC_Logger.warn("can't load model '" + mp.getModelName() + "'");
            ERC_Logger.warn(e.getMessage());
            return null;
        }
        addTexture(mp.getTextureName());
//		addTexture(mp.getIconName());
        ModelPack modelpack = new ModelPack(getObj(mp.getModelName()), getTex(mp.getTextureName()), mp.getIconName(), mp.getOption());
        return modelpack;
    }

    //API
    public static boolean registerCoaster(ERC_ModelLoadPlan mp) {
        ModelPack modelpack = makeModelPack(mp);
        if (modelpack == null) return false;
        ModelPackList_Main.add(modelpack);
        return true;
    }

    public static boolean registerConnectionCoaster(ERC_ModelLoadPlan mp) {
        ModelPack modelpack = makeModelPack(mp);
        if (modelpack == null) return false;
        ModelPackList_Connect.add(modelpack);
        return true;
    }

    public static boolean registerMonoCoaster(ERC_ModelLoadPlan mp) {
        if (mp.getOption().SeatNum != 1) return false;
        ModelPack modelpack = makeModelPack(mp);
        if (modelpack == null) return false;
        ModelPackList_Mono.add(modelpack);
        return false;
    }
}
