package net.ajitek.mc.zpm.core;

import net.minecraft.src.TileEntity;
import net.minecraft.src.ic2.api.Direction;
import net.minecraft.src.ic2.api.IEnergyStorage;
import net.minecraft.src.ic2.api.IEnergySink;
import net.minecraft.src.NBTTagCompound;

import java.lang.reflect.Field;

public class TileEntityZPM extends TileEntity {
	public int someData = -1;

	public TileEntityZPM() {
		super();
		initReflection();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if (nbt.hasKey("someData")) {
			someData = nbt.getInteger("someData");
		} else {
			someData = 50;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("someData", someData);
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
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

		if (tile instanceof IEnergySink) {
			energy = Common.DEFAULT_ENERGY;
			pax = Common.DEFAULT_PACKET_SIZE;
			IEnergySink sink = (IEnergySink)tile;

			if (isStorage(tile)) {
				int maxStorage = getMaxStorage(tile);
				energy = maxStorage - getEnergy(tile);
				pax = getOutput(tile);

				if (energy > maxStorage / 20)
					energy = maxStorage / 20;
			}

			while (energy > 0 && sink.demandsEnergy()) {
				sink.injectEnergy(dir.getInverse(), pax);
				energy -= pax;
			}
		}
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
