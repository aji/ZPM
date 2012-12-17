package net.ajitek.mc.zpm.core;

import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ModLoader;

import java.io.File;
import net.ajitek.mc.zpm.proxy.Proxy;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.Property;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;

public class mod_ZPM extends NetworkMod {
	public final static String NAME = "ZPM";
	public final static String VERSION = "1.2dev";

	private static mod_ZPM instance = null;

	protected Proxy proxy;

	public static mod_ZPM getInstance() {
		return mod_ZPM.instance;
	}

	@Override
	public void load() {
		mod_ZPM.instance = this;

		proxy = new Proxy();

		initConfig();
		initBlock();

		MinecraftForge.setGuiHandler(this, proxy);
	}

	public void initConfig() {
		File configFile = Proxy.getConfig();
		Configuration config = new Configuration(configFile);
		Property prop;

		config.load();

		prop = config.getOrCreateIntProperty("id", Configuration.CATEGORY_BLOCK, 500);
		prop.comment = "Block ID of the ZPM block";
		Common.ZPM_ID = prop.getInt(500);

		prop = config.getOrCreateProperty("name", Configuration.CATEGORY_BLOCK, "Zero-Point Module");
		prop.comment = "Name of the ZPM block";
		Common.ZPM_NAME = prop.value;

		prop = config.getOrCreateIntProperty("packetSize", Configuration.CATEGORY_GENERAL, 32);
		prop.comment = "Default EU per packet from the ZPM";
		Common.DEFAULT_PACKET_SIZE = prop.getInt(32);

		prop = config.getOrCreateIntProperty("energy", Configuration.CATEGORY_GENERAL, 4096);
		prop.comment = "Default amount of EU/t from the ZPM (broken into smaller packets)";
		Common.DEFAULT_ENERGY = prop.getInt(4096);

		config.save();
	}

	public void initBlock() {
		Common.blockZPM = new BlockZPM(Common.ZPM_ID, Material.rock);
		Common.itemBlockZPM = (new ItemBlock(Common.ZPM_ID - 256)).setItemName("ajitek.zpm");

		ModLoader.registerBlock(Common.blockZPM, ItemBlock.class);
		ModLoader.registerTileEntity(TileEntityZPM.class, "ajitek.zpm");

		ModLoader.addLocalization("item.ajitek.zpm", Common.ZPM_NAME);
		ModLoader.addLocalization("tile.ajitek.zpm.name", Common.ZPM_NAME);
	}

	@Override
	public String getVersion() {
		return "v" + mod_ZPM.VERSION;
	}
}
