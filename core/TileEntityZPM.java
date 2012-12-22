package net.ajitek.mc.zpm.core;

import net.ajitek.mc.zpm.proxy.*;
import net.minecraft.src.TileEntity;
import net.minecraft.src.ic2.api.Direction;
import net.minecraft.src.ic2.api.IEnergyStorage;
import net.minecraft.src.ic2.api.IEnergySink;
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
	private boolean redstone;

	public TileEntityZPM() {
		super();
		initReflection();
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		/* read metadata every update until we find a better way to go about this */
		readMetadata();

		chargeToward(Direction.XN);
		chargeToward(Direction.XP);
		chargeToward(Direction.YN);
		chargeToward(Direction.YP);
		chargeToward(Direction.ZN);
		chargeToward(Direction.ZP);
	}

	public void chargeToward(Direction dir) {
		TileEntity tile;
		int energy, pax;

		tile = dir.applyToTileEntity(this);

		if (tile == null)
			return;

		if (isStorage(tile)) {
			if (draining) {
				setEnergy(tile, 0);
			} else {
				setEnergy(tile, getMaxStorage(tile));
			}

			return;
		}

		if (tile instanceof IEnergySink) {
			energy = Common.DEFAULT_ENERGY;
			pax = Common.DEFAULT_PACKET_SIZE;
			IEnergySink sink = (IEnergySink)tile;

			while (energy > 0 && sink.demandsEnergy()) {
				sink.injectEnergy(dir.getInverse(), pax);
				energy -= pax;
			}
		}
	}

	public void setAction(boolean drain, boolean red) {
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, (drain?1:0) + (red?2:0));

		draining = drain;
		redstone = red;
	}

	public void readMetadata() {
		int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);

		draining = (meta & 1) == 1;
		redstone = (meta & 2) == 2;
	}

	public void setDraining(boolean drain) {
		setAction(drain, redstone);
	}

	public void setRedstone(boolean red) {
		setAction(draining, red);
	}

	public boolean getDraining() {
		return draining;
	}

	public boolean getRedstone() {
		return redstone;
	}

	/* Network stuff */

	@Override
	public String getGuiClassName() {
		return "net.ajitek.mc.zpm.proxy.GuiZPM";
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
		boolean red = in.readBoolean();

		setAction(drain, red);
	}

	@Override
	public void fillUpdatePacket(DataOutputStream out, boolean isServer)
	throws IOException {
		if (isServer)
			return;

		out.writeBoolean(draining);
		out.writeBoolean(redstone);
	}


	/* Reflection, the Java equivalent of longjmp... */

	private static Class TEEB = null; /* TileEntityElectricBlock */
	private static Field TEEB_output;
	private static Field TEEB_maxStorage;
	private static Field TEEB_energy;

	private static boolean isStorage(TileEntity tile) {
		if (TEEB == null)
			return false;

		return TEEB.isInstance(tile);
	}

	private static int getOutput(TileEntity tile) {
		try {
			return TEEB_output.getInt(tile);
		} catch (IllegalAccessException e) {
			return Common.DEFAULT_PACKET_SIZE;
		}
	}

	private static int getMaxStorage(TileEntity tile) {
		try {
			return TEEB_maxStorage.getInt(tile);
		} catch (Exception e) {
			return Common.DEFAULT_ENERGY;
		}
	}

	private static int getEnergy(TileEntity tile) {
		try {
			return TEEB_energy.getInt(tile);
		} catch (Exception e) {
			return 0;
		}
	}

	private static void setEnergy(TileEntity tile, int energy) {
		try {
			TEEB_energy.setInt(tile, energy);
		} catch (Exception e) {
			/* bleh */
		}
	}

	private static void initReflection() {
		if (TEEB != null)
			return;

		try {
			TEEB = Class.forName("ic2.common.TileEntityElectricBlock");
			TEEB_output = TEEB.getField("output");
			TEEB_maxStorage = TEEB.getField("maxStorage");
			TEEB_energy = TEEB.getField("energy");
		} catch (Exception e) {
			System.out.println("IC2 does not seem to be loaded");
			TEEB = null;
		}
	}
}
