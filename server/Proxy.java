package net.ajitek.mc.zpm.proxy;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraft.src.forge.IGuiHandler;
import java.io.File;

public class Proxy implements IGuiHandler {
	public static File getConfig() {
		return new File(new File(".", "config"), "ZPM.conf");
	}

	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
}
