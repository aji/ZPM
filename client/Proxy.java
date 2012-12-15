package net.ajitek.mc.zpm;

import net.minecraft.client.Minecraft;
import java.io.File;

public class Proxy {
	public static File getConfig() {
		return new File(new File(Minecraft.getMinecraftDir(), "config"), "ZPM.conf");
	}
}
