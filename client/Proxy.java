package net.ajitek.mc.zpm.proxy;

import net.ajitek.mc.zpm.core.TileEntityZPM;
import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.TileEntity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import java.io.File;

public class Proxy implements IGuiHandler {
	public static File getConfig() {
		return new File(new File(Minecraft.getMinecraftDir(), "config"), "ZPM.conf");
	}

	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te;

		if (!world.blockExists(x, y, z)) 
			return null;

		te = world.getBlockTileEntity(x, y, z);

		if (te instanceof TileEntityZPM)
			return new GuiZPM((TileEntityZPM)te);

		return null;
	}
}
