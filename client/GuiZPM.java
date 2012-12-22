package net.ajitek.mc.zpm.proxy;

import net.ajitek.mc.zpm.core.*;
import net.minecraft.src.ModLoader;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiButton;
import net.minecraft.src.Packet132TileEntityData;
import org.lwjgl.opengl.GL11;

public class GuiZPM extends GuiBase
{
	protected TileEntityZPM zpm;

	protected int xSize = 118;
	protected int ySize = 58;

	protected int buttonWide = 104;
	protected final int buttonHigh = 20;

	protected GuiButton drainingBtn;
	protected GuiButton redstoneBtn;

	@Override
	public void setTileEntity(TileEntityBase teb) {
		/* unchecked cast, to raise an exception if necessary */
		zpm = (TileEntityZPM)teb;
	}

	@Override
	public void drawScreen(int x, int y, float z) {
		int tex = mc.renderEngine.getTexture(Common.GUI_PNG);
		int dx = (width - xSize) / 2;
		int dy = (height - ySize) / 2;

		updateLabels();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(tex);
		drawTexturedModalRect(dx, dy, 0, 0, xSize, ySize);

		super.drawScreen(x, y, z);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui() {
		int dx = (width - xSize) / 2;
		int dy = (height - ySize) / 2;

		/* these coords are from the .png */
		drainingBtn = addButton(0, dx+7, dy+7);
		redstoneBtn = addButton(1, dx+7, dy+31);

		updateLabels();
	}

	protected GuiButton addButton(int id, int x, int y) {
		GuiButton btn = new GuiButton(id, x, y, buttonWide + 1, buttonHigh, "");
		controlList.add(btn);
		return btn;
	}

	protected void updateLabels() {
		drainingBtn.displayString = (zpm.getDraining() ? "Draining" : "Filling");
		redstoneBtn.displayString = (zpm.getRedstone() ? "Redstone: ON" : "Redstone: OFF");
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		switch (btn.id) {
		case 0:
			zpm.setDraining(!zpm.getDraining());
			break;
		case 1:
			zpm.setRedstone(!zpm.getRedstone());
			break;
		}

		zpm.sendUpdateToServer();
	}

}
