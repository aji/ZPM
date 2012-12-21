package net.ajitek.mc.zpm.proxy;

import net.ajitek.mc.zpm.core.TileEntityBase;
import net.ajitek.mc.zpm.core.Common;
import net.ajitek.mc.zpm.core.mod_ZPM;
import net.minecraft.client.Minecraft;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.forge.IPacketHandler;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.Packet;
import net.minecraft.src.NetClientHandler;
import cpw.mods.fml.client.FMLClientHandler;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class Proxy implements IGuiHandler, IPacketHandler {
	public static File getConfig() {
		return new File(new File(Minecraft.getMinecraftDir(), "config"), "ZPM.conf");
	}

	public Object getGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te;

		if (!world.blockExists(x, y, z)) 
			return null;

		te = world.getBlockTileEntity(x, y, z);

		if (te == null || !(te instanceof TileEntityBase))
			return null;

		TileEntityBase teb = (TileEntityBase)te;
		String guiClassName = teb.getGuiClassName();
		Class guiClass;
		Object guiObject;

		if (guiClassName == null)
			return null;

		try {
			guiClass = Class.forName(guiClassName);
			guiObject = guiClass.newInstance();
		} catch (Exception e) {
			/* TODO: be smarter about this */
			return null;
		}

		if (!(guiObject instanceof GuiBase))
			return null;

		((GuiBase)guiObject).setTileEntity(teb);

		if (world.isRemote)
			teb.clientGuiReinit();

		return guiObject;
	}

	public void onPacketData(NetworkManager net, String channel, byte[] data) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

		if (!channel.equals(Common.CHANNEL))
			return; /* shouldn't happen, but just in case */

		try {
			handlePacket(net, in);
		} catch (Exception e) {
			return;
		}
	}

	public void handlePacket(NetworkManager net, DataInputStream in) throws IOException {
		World world = FMLClientHandler.instance().getClient().theWorld;

		switch (in.readByte()) {
		case 0: /* tile entity update */
			int x = in.readInt();
			int y = in.readInt();
			int z = in.readInt();
			TileEntity tile = world.getBlockTileEntity(x, y, z);

			if (tile != null && tile instanceof TileEntityBase)
				((TileEntityBase)tile).handleUpdatePacket(net, in, false);
		}
	}

	public void sendPacketToServer(Packet pkt) {
		FMLClientHandler.instance().sendPacket(pkt);
	}

	public void sendPacketToPlayer(String player, Packet pkt) {
	}
}
