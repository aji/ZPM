package net.ajitek.mc.zpm.core;

import net.ajitek.mc.zpm.proxy.*;
import net.minecraft.src.TileEntity;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import java.io.DataInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TileEntityBase extends TileEntity {
	public String getGuiClassName() {
		return null;
	}

	/* reinitialize client copy for GUI */
	public void clientGuiReinit() {
	}

	public void handleUpdatePacket(NetworkManager net, DataInputStream in, boolean isServer)
	throws IOException {
	}

	/* override this, not buildUpdatePacket */
	public void fillUpdatePacket(DataOutputStream out, boolean isServer)
	throws IOException {
	}

	public final Packet250CustomPayload buildUpdatePacket(boolean isServer) {
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytesOut);

		try {
			out.writeByte((byte)0);
			out.writeInt(xCoord);
			out.writeInt(yCoord);
			out.writeInt(zCoord);
			fillUpdatePacket(out, isServer);
		} catch (IOException e) {
			return null;
		}

		Packet250CustomPayload pkt = new Packet250CustomPayload();
		pkt.channel = Common.CHANNEL;
		pkt.data = bytesOut.toByteArray();
		pkt.length = pkt.data.length;

		return pkt;
	}

	public final void sendUpdateToServer() {
		mod_ZPM.instance().sendPacketToServer(buildUpdatePacket(false));
	}

	public final void sendUpdateToPlayer(String player) {
		mod_ZPM.instance().sendPacketToPlayer(player, buildUpdatePacket(true));
	}
}
