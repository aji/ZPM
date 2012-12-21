package net.ajitek.mc.zpm.proxy;

import net.ajitek.mc.zpm.core.TileEntityZPM;
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

		if (te != null && te instanceof TileEntityZPM) {
			TileEntityZPM zpm = (TileEntityZPM)te;
			if (world.isRemote)
				zpm.someData = -1;
			return new GuiZPM(zpm);
		}

		return null;
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

			if (tile != null && tile instanceof TileEntityZPM)
				((TileEntityZPM)tile).handleUpdatePacket(net, in, false);
		}
	}

	public Packet250CustomPayload buildUpdatePacket(TileEntityZPM zpm) {
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytesOut);

		try {
			out.writeByte((byte)0);
			out.writeInt(zpm.xCoord);
			out.writeInt(zpm.yCoord);
			out.writeInt(zpm.zCoord);
			zpm.buildUpdatePacket(out, false);
		} catch (IOException e) {
			return null;
		}

		Packet250CustomPayload pkt = new Packet250CustomPayload();
		pkt.channel = Common.CHANNEL;
		pkt.data = bytesOut.toByteArray();
		pkt.length = pkt.data.length;
		return pkt;
	}

	public void sendUpdateToServer(TileEntityZPM zpm) {
		sendPacketToServer(buildUpdatePacket(zpm));
	}

	public void sendUpdateToPlayer(String player, TileEntityZPM zpm) {
	}

	public void sendPacketToServer(Packet pkt) {
		FMLClientHandler.instance().sendPacket(pkt);
	}

	public void sendPacketToPlayer(String player, Packet pkt) {
	}
}
