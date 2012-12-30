package net.ajitek.mc.zpm.core;

import java.lang.reflect.Field;
import net.minecraft.src.ic2.api.Direction;
import net.minecraft.src.ic2.api.IEnergySink;
import net.minecraft.src.TileEntity;

public class FillerIC2 implements IFiller {

	private Class TEEB; /* TileEntityElectricBlock */
	private Field TEEB_output;
	private Field TEEB_maxStorage;
	private Field TEEB_energy;

	public boolean initialize() {
		try {
			TEEB = Class.forName("ic2.common.TileEntityElectricBlock");
			TEEB_output = TEEB.getField("output");
			TEEB_maxStorage = TEEB.getField("maxStorage");
			TEEB_energy = TEEB.getField("energy");
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	private boolean isStorage(TileEntity tile) {
		return TEEB.isInstance(tile);
	}

	private int getOutput(TileEntity tile) {
		try {
			return TEEB_output.getInt(tile);
		} catch (Exception e) {
			return Common.DEFAULT_PACKET_SIZE;
		}
	}

	private int getMaxStorage(TileEntity tile) {
		try {
			return TEEB_maxStorage.getInt(tile);
		} catch (Exception e) {
			return Common.DEFAULT_ENERGY;
		}
	}

	private int getEnergy(TileEntity tile) {
		try {
			return TEEB_energy.getInt(tile);
		} catch (Exception e) {
			return 0;
		}
	}

	private void setEnergy(TileEntity tile, int energy) {
		try {
			TEEB_energy.setInt(tile, energy);
		} catch (Exception e) {
			/* ... */
		}
	}

	private Direction toIC2Dir(ZPMDirection dir) {
		switch (dir.index) {
		case 0: return Direction.XN;
		case 1: return Direction.XP;
		case 2: return Direction.YN;
		case 3: return Direction.YP;
		case 4: return Direction.ZN;
		case 5: return Direction.ZP;
		}

		/* wat */
		return Direction.XN;
	}

	public boolean canFill(TileEntity tile) {
		return isStorage(tile) || (tile instanceof IEnergySink);
	}

	public void fill(ZPMDirection dir, TileEntity tile) {
		int energy, pax;

		if (isStorage(tile)) {
			setEnergy(tile, getMaxStorage(tile));
			return;
		}

		if (!(tile instanceof IEnergySink))
			return;

		energy = Common.DEFAULT_ENERGY;
		pax = Common.DEFAULT_PACKET_SIZE;
		IEnergySink sink = (IEnergySink)tile;

		while (energy > 0 && sink.demandsEnergy()) {
			sink.injectEnergy(toIC2Dir(dir.invert()), pax);
			energy -= pax;
		}
	}

	public void drain(ZPMDirection dir, TileEntity tile) {
		if (isStorage(tile)) {
			setEnergy(tile, 0);
		}
	}
}
