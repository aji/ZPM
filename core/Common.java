package net.ajitek.mc.zpm.core;

import net.ajitek.mc.zpm.proxy.*;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.Material;
import net.minecraft.src.forge.Property;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.ModLoader;
import net.minecraft.src.ItemBlock;
import java.io.File;

public class Common {
	public static final String CHANNEL = "ajitekZPM";

	public static final String BLOCK_PNG = "/net/ajitek/mc/zpm/block.png";
	public static final String GUI_PNG = "/net/ajitek/mc/zpm/gui.png";

	public static int ZPM_ID = 500;
	public static String ZPM_NAME = "Zero-Point Module";

	public static int DEFAULT_PACKET_SIZE = 32;
	public static int DEFAULT_ENERGY = 4096;

	public static BlockZPM blockZPM;
	public static Item itemBlockZPM;

	public static void initConfig(File configFile) {
		Configuration config = new Configuration(configFile);
		Property prop;

		config.load();

		prop = config.getOrCreateIntProperty("id", Configuration.CATEGORY_BLOCK, 500);
		prop.comment = "Block ID of the ZPM block";
		ZPM_ID = prop.getInt(500);

		prop = config.getOrCreateProperty("name", Configuration.CATEGORY_BLOCK, "Zero-Point Module");
		prop.comment = "Name of the ZPM block";
		ZPM_NAME = prop.value;

		prop = config.getOrCreateIntProperty("packetSize", Configuration.CATEGORY_GENERAL, 32);
		prop.comment = "Default EU per packet from the ZPM";
		DEFAULT_PACKET_SIZE = prop.getInt(32);

		prop = config.getOrCreateIntProperty("energy", Configuration.CATEGORY_GENERAL, 4096);
		prop.comment = "Default amount of EU/t from the ZPM (broken into smaller packets)";
		DEFAULT_ENERGY = prop.getInt(4096);

		config.save();
	}

	public static void initBlock() {
		Common.blockZPM = new BlockZPM(Common.ZPM_ID, Material.rock);
		Common.itemBlockZPM = (new ItemBlock(Common.ZPM_ID - 256)).setItemName("ajitek.zpm");

		ModLoader.registerBlock(Common.blockZPM, ItemBlock.class);
		ModLoader.registerTileEntity(TileEntityZPM.class, "ajitek.zpm");

		ModLoader.addLocalization("item.ajitek.zpm", Common.ZPM_NAME);
		ModLoader.addLocalization("tile.ajitek.zpm.name", Common.ZPM_NAME);

		ModLoader.addLocalization("ajitek.zpm.draining", "ZPM set to drain");
		ModLoader.addLocalization("ajitek.zpm.filling", "ZPM set to fill");
	}

}
