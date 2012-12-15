package net.ajitek.mc.zpm;

import java.io.File;

public class Proxy {
	public static File getConfig() {
		return new File(new File(".", "config"), "ZPM.conf");
	}
}
