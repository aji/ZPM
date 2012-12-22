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

	protected int xSize = 212;
	protected int ySize = 34;

	protected int buttonWide = 35;
	protected final int buttonHigh = 20;

	@Override
	public void setTileEntity(TileEntityBase teb) {
		/* unchecked cast, to raise an exception if necessary */
		zpm = (TileEntityZPM)teb;
	}

	@Override
	public void onGuiClosed() {
		zpm.sendUpdateToServer();
		zpm.updateMetadata();
	}

	@Override
	public void drawScreen(int x, int y, float z) {
		int tex = mc.renderEngine.getTexture(Common.GUI_PNG);
		int dx = (width - xSize) / 2;
		int dy = (height - ySize) / 2;

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(tex);
		drawTexturedModalRect(dx, dy, 0, 0, xSize, ySize);

		super.drawScreen(x, y, z);

		String s = Integer.toString(zpm.someData) + "%";
		if (zpm.someData < 0)
			s = "";

		int wide = 2 * fontRenderer.getStringWidth(s) - fontRenderer.getStringWidth("100%");
		int high = fontRenderer.FONT_HEIGHT;
		fontRenderer.drawString(s, (width - wide) / 2, (height - high) / 2, 0);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void initGui() {
		int dx = (width - xSize) / 2;
		int y = 7 + (height - ySize) / 2;

		/* these coords are from the .png */
		addButton(-10, dx+  7, y, "-10");
		addButton( -1, dx+ 45, y,  "-1");
		addButton(  1, dx+132, y,  "+1");
		addButton( 10, dx+170, y, "+10");
	}

	protected void addButton(int id, int x, int y, String s) {
		controlList.add(new GuiButton(id, x, y, buttonWide + 1, buttonHigh, s));
	}

	@Override
	protected void actionPerformed(GuiButton btn) {
		if (zpm.someData < 0)
			return;

		zpm.someData += btn.id;
		if (zpm.someData < 0)
			zpm.someData = 0;
		if (zpm.someData > 100)
			zpm.someData = 100;
	}

}
