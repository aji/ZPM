package net.ajitek.mc.zpm.proxy;

import net.ajitek.mc.zpm.core.TileEntityZPM;
import net.ajitek.mc.zpm.core.Common;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.World;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.TileEntity;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.forge.IPacketHandler;
import cpw.mods.fml.server.FMLServerHandler;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class Proxy implements IGuiHandler, IPacketHandler {
	public static File getConfig() {
		return new File(new File(".", "config"), "ZPM.conf");
	}

	public Object getGuiElement(int ID, EntityPlayer p, World world, int x, int y, int z) {
		TileEntity te;
		EntityPlayerMP player;

		if (!(p instanceof EntityPlayerMP))
			return null;
		if (!world.blockExists(x, y, z))
			return null;

		player = (EntityPlayerMP)p;
		te = world.getBlockTileEntity(x, y, z);

		if (te != null && te instanceof TileEntityZPM) {
			TileEntityZPM zpm = (TileEntityZPM)te;
			sendUpdateToPlayer(p.getUsername(), zpm);
		}

		return null;
	}

	public void onPacketData(NetworkManager net, String channel, byte[] data) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

		if (!channel.equals(Common.CHANNEL))
			return;

		try {
			handlePacket(net, in);
		} catch (Exception e) {
			return;
		}
	}

	public void handlePacket(NetworkManager net, DataInputStream in) throws IOException {
		World world;

		if (!(net.getNetHandler() instanceof NetServerHandler))
			return;

		world = ((NetServerHandler)net.getNetHandler()).getPlayerEntity().worldObj;

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
			zpm.buildUpdatePacket(out, true);
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
	}

	public void sendUpdateToPlayer(String player, TileEntityZPM zpm) {
		sendPacketToPlayer(player, buildUpdatePacket(zpm));
	}

	public void sendPacketToServer(Packet pkt) {
	}

	public void sendPacketToPlayer(String player, Packet pkt) {
		FMLServerHandler.instance().getServer().configManager.sendPacketToPlayer(player, pkt);
	}
}
