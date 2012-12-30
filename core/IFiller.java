package net.ajitek.mc.zpm.core;

import net.minecraft.src.TileEntity;

public interface IFiller {
	/* Set up the appropriate reflection. If this returns false,
	   the IFiller is omitted. */
	public boolean initialize();

	/* Checks if we can fill or drain the TE. The first IFiller to
	   return true gets the fill. */
	public boolean canFill(TileEntity te);

	public void fill(ZPMDirection dir, TileEntity te);

	public void drain(ZPMDirection dir, TileEntity te);
}
