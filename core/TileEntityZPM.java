package net.ajitek.mc.zpm.core;

import net.ajitek.mc.zpm.proxy.*;
import net.minecraft.src.TileEntity;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.forge.MessageManager;
import cpw.mods.fml.common.FMLCommonHandler;
import java.lang.reflect.Field;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class TileEntityZPM extends TileEntityBase {
	/* cached from metadata */
	private boolean draining;

	public TileEntityZPM() {
		super();
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		/* read metadata every update until we find a better way to go about this */
		readMetadata();

		chargeToward(ZPMDirection.EAST);
		chargeToward(ZPMDirection.WEST);
		chargeToward(ZPMDirection.UP);
		chargeToward(ZPMDirection.DOWN);
		chargeToward(ZPMDirection.SOUTH);
		chargeToward(ZPMDirection.NORTH);
	}

	public void chargeToward(ZPMDirection dir) {
		TileEntity tile;
		int energy, pax;

		tile = dir.apply(this);

		if (tile == null)
			return;

		FillerRegistry.fillOrDrain(dir, tile, !draining);
	}

	/* This seemingly unnecessary cascade of functions comes from a
	   time where there were two booleans with individual accessors */

	public void setAction(boolean drain) {
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, (drain?1:0));

		draining = drain;
	}

	public void readMetadata() {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		draining = (meta & 1) == 1;
	}

	public void setDraining(boolean drain) {
		setAction(drain);
	}

	public boolean getDraining() {
		return draining;
	}

	/* Network stuff */

	@Override
	public String getGuiClassName() {
		//return "net.ajitek.mc.zpm.proxy.GuiZPM";
		return "";
	}

	@Override
	public void clientGuiReinit() {
	}

	@Override
	public void handleUpdatePacket(NetworkManager net, DataInputStream in, boolean isServer)
	throws IOException {
		if (!isServer)
			return;

		/* We do it this way to ensure the arguments are evaluated
		   in the right order. I don't know what order Java
		   evaluates arguments, but I don't feel like digging
		   up the spec right now. */
		boolean drain = in.readBoolean();

		setAction(drain);
	}

	@Override
	public void fillUpdatePacket(DataOutputStream out, boolean isServer)
	throws IOException {
		if (isServer)
			return;

		out.writeBoolean(draining);
	}

}
