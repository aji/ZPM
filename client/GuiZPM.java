package net.ajitek.mc.zpm.proxy;

import net.ajitek.mc.zpm.core.TileEntityZPM;
import net.ajitek.mc.zpm.core.Common;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiZPM extends GuiScreen
{
	protected TileEntityZPM zpm;

	protected int xSize = 212;
	protected int ySize = 34;

	protected int buttonWide = 35;
	protected final int buttonHigh = 20;

	protected int value;

	public GuiZPM(TileEntityZPM te) {
		zpm = te;
		value = 100;
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

		String s = Integer.toString(value) + "%";
		int wide = 2 * fontRenderer.getStringWidth(s) - fontRenderer.getStringWidth("100%");
		int high = fontRenderer.FONT_HEIGHT;
		fontRenderer.drawStringWithShadow(s, (width - wide) / 2, (height - high) / 2, 0xffffff);
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
		value += btn.id;
		if (value < 0)
			value = 0;
		if (value > 100)
			value = 100;
	}

}
