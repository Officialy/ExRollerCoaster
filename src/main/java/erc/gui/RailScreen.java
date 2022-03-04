package erc.gui;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.*;
import erc.network.ERC_MessageRailGUICtS;
import erc.network.ERC_PacketHandler;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import erc.core.ERCConstants;
import erc.gui.container.DefMenu;
import erc.manager.ERC_CoasterAndRailManager;
import erc.block.tileEntity.TileEntityRailBase;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.GuiUtils;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class RailScreen extends AbstractContainerScreen<DefMenu> {

    @Override
    protected void renderBg(PoseStack stack, float p_97788_, int p_97789_, int p_97790_) {
        if (this.minecraft.level != null) {
            GuiUtils.drawGradientRect(stack.last().pose(), 0, this.width * 3 / 4, 0, this.width, this.height, -1072689136, -804253680); //todo check
        }
        this.minecraft.getTextureManager().bindForSetup(TEXTURE);
        this.ERCRail_drawTexturedModalRect(this.getGuiLeft(), this.getGuiTop(), 0, 0, imageWidth, imageHeight);
    }

    public enum editFlag {
        CONTROLPOINT, POW, ROTRED, ROTGREEN, ROTBLUE, SMOOTH, RESET, SPECIAL, RailModelIndex
    }

    private static final ResourceLocation TEXTURE = new ResourceLocation(ERCConstants.DOMAIN, "textures/gui/gui.png");
    private static int buttonid;
    private static final int buttonidoffset = 0;
    private boolean clickedTileExists;

    int offsetx;
    int offsety;

    static class GUIName {
        String name;
        int x;
        int y;
        int flag;
        int baseID;

        GUIName(String str, int x, int y, editFlag flag, int base) {
            name = str;
            this.x = x;
            this.y = y;
            this.flag = flag.ordinal();
            this.baseID = base;
        }
//		Function<float, float> func
    }

    Map<Integer, GUIName> GUINameMap = new HashMap<>();

    public RailScreen(DefMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        imageWidth = 100;
        imageHeight = 230;
    }

    @Override
    public void init() {
        super.init();
        GUINameMap.clear();
        this.leftPos = this.width * 7 / 8 - getXSize() / 2;
        buttonid = buttonidoffset; // 0
        offsetx = this.leftPos + 20;
        offsety = this.topPos;
//		int offset = 4;
//        int length = 27;

        addButton2("ControlPoint", editFlag.CONTROLPOINT);
        addButton4("Power", editFlag.POW);

        addButton4("Rotation", editFlag.ROTRED);
        addButton4("", editFlag.ROTGREEN, 14);
        addButton4("", editFlag.ROTBLUE, 14);

        addButton1(60, 13, new TextComponent("smooth"), editFlag.SMOOTH);
        addButton1(60, 13, new TextComponent("Reset rot"), editFlag.RESET);

        if (ERC_CoasterAndRailManager.clickedTileForGUI == null) {
            this.clickedTileExists = false;
        } else {
            this.clickedTileExists = true;
            ERC_CoasterAndRailManager.clickedTileForGUI.SpecialGUIInit(this);
        }
    }

    @SuppressWarnings("unchecked")
    public void addButton1(int lenx, int leny, Component str, editFlag flag) {
        offsety += 17;
        this.renderables.add(new ExtendedButton(offsetx, offsety, lenx, leny, str, (button) -> actionPerformed(buttonid++)));
        GUINameMap.put(buttonid - 1, new GUIName("", -100, -100, flag, -1));
    }

    @SuppressWarnings("unchecked")
    public void addButton2(String str, editFlag flag) {
        offsety += 27;
        this.renderables.add(new ExtendedButton(offsetx, offsety, 18, 13, new TextComponent("-"), (button) -> actionPerformed(buttonid++)));
        this.renderables.add(new ExtendedButton(offsetx + 42, offsety, 18, 13, new TextComponent("+"), (button) -> actionPerformed(buttonid++)));
        GUIName data = new GUIName(str, 5, offsety - 10 - topPos, flag, buttonid - 2);
        GUINameMap.put(buttonid - 2, data);
        GUINameMap.put(buttonid - 1, data);
    }

    public void addButton4(String str, editFlag flag) {
        addButton4(str, flag, 27);
    }

    @SuppressWarnings("unchecked")
    public void addButton4(String str, editFlag flag, int yshift) {
        offsety += yshift;
        this.renderables.add(new ExtendedButton(offsetx - 13, offsety, 17, 13, new TextComponent("<<"), (button) -> actionPerformed(buttonid++)));
        this.renderables.add(new ExtendedButton(offsetx + 6, offsety, 13, 13, new TextComponent("<"), (button) -> actionPerformed(buttonid++)));
        this.renderables.add(new ExtendedButton(offsetx + 41, offsety, 13, 13, new TextComponent(">"), (button) -> actionPerformed(buttonid++)));
        this.renderables.add(new ExtendedButton(offsetx + 56, offsety, 17, 13, new TextComponent(">>"), (button) -> actionPerformed(buttonid++)));
        GUIName data = new GUIName(str, 5, offsety - 10 - topPos, flag, buttonid - 4);
        GUINameMap.put(buttonid - 4, data);
        GUINameMap.put(buttonid - 3, data);
        GUINameMap.put(buttonid - 2, data);
        GUINameMap.put(buttonid - 1, data);
    }

    @Override
    public void onClose() {
        super.onClose();
        ERC_CoasterAndRailManager.CloseRailGUI();
    }

    @Override
    public void render(PoseStack stack, int p_97796_, int p_97797_, float p_97798_) {
        super.render(stack, p_97796_, p_97797_, p_97798_);
        int offset = 4;
//        int length = 27;
//        this.font.draw(stack, "Control Point", 	5, offset, 0x404040);
//        this.font.draw(stack, "Power", 			5, offset+1*length, 0x404040);
//        this.font.draw(stack, "Rotation Red", 	5, offset+2*length, 0x404040);
//        this.font.draw(stack, "Rotation Green", 	5, offset+3*length, 0x404040);
//        this.font.draw(stack, "Rotation Blue", 	5, offset+4*length, 0x404040);
//        
        for (GUIName g : GUINameMap.values()) {
            this.font.draw(stack, g.name, g.x, g.y, 0x404040);
        }

        if (this.clickedTileExists) {
            drawString(stack, this.font, "" + ERC_CoasterAndRailManager.clickedTileForGUI.GetPosNum(), 43, 29, 0xffffff);
            drawString(stack, this.font, String.format("% 2.1f", ERC_CoasterAndRailManager.clickedTileForGUI.BaseRail.Power), 37, 56, 0xffffff);
            drawString(stack, this.font, ERC_CoasterAndRailManager.clickedTileForGUI.SpecialGUIDrawString(), 42, 172, 0xffffff);
        }
    }

    public void ERCRail_drawTexturedModalRect(int x, int y, int z, int v, int width, int height) {
        float f = 1f / (float) width;
        float f1 = 1f / (float) height;
        BufferBuilder wr = Tesselator.getInstance().getBuilder();
        wr.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        wr.vertex((x + 0), (y + height), this.getBlitOffset()).uv(((float) (z + 0) * f), ((float) (v + height) * f1)).endVertex();
        wr.vertex((x + width), (y + height), this.getBlitOffset()).uv((((float) (z + width) * f)), ((float) (v + height) * f1)).endVertex();
        wr.vertex((x + width), (y + 0), this.getBlitOffset()).uv(((float) (z + width) * f), ((float) (v + 0) * f1)).endVertex();
        wr.vertex((x + 0), (y + 0), this.getBlitOffset()).uv(((float) (z + 0) * f), ((float) (v + 0) * f1)).endVertex();
        wr.end();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void actionPerformed(int id) {
        if (this.clickedTileExists) {
            TileEntityRailBase entity = ERC_CoasterAndRailManager.clickedTileForGUI;

            GUIName obj = GUINameMap.get(id);
            int data = (id - obj.baseID);

            ERC_MessageRailGUICtS packet = new ERC_MessageRailGUICtS(entity.getPos(), obj.flag, data);
            ERC_PacketHandler.INSTANCE.sendToServer(packet);
        }
    }


}
