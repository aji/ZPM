package net.ajitek.mc.zpm.core;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.src.TileEntity;

public final class FillerRegistry {
	private static ArrayList<IFiller> fillers = new ArrayList<IFiller>();

	public static boolean add(IFiller filler) {
		if (!filler.initialize())
			return false;

		if (!fillers.contains(filler))
			fillers.add(0, filler);

		return true;
	}

	public static void fill(ZPMDirection dir, TileEntity tile) {
		fillOrDrain(dir, tile, true);
	}

	public static void drain(ZPMDirection dir, TileEntity tile) {
		fillOrDrain(dir, tile, false);
	}

	protected static void fillOrDrain(ZPMDirection dir, TileEntity tile, boolean filling) {
		Iterator<IFiller> iter = fillers.iterator();

		while (iter.hasNext()) {
			IFiller filler = iter.next();
			if (filler.canFill(tile)) {
				if (filling)
					filler.fill(dir, tile);
				else
					filler.drain(dir, tile);
			}
		}
	}
}
