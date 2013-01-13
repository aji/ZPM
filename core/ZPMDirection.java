package net.ajitek.mc.zpm.core;

import net.minecraft.src.TileEntity;

public class ZPMDirection {
    public static final ZPMDirection EAST   = new ZPMDirection( 1,  0,  0);
    public static final ZPMDirection WEST   = new ZPMDirection(-1,  0,  0);
    public static final ZPMDirection UP     = new ZPMDirection( 0,  1,  0);
    public static final ZPMDirection DOWN   = new ZPMDirection( 0, -1,  0);
    public static final ZPMDirection SOUTH  = new ZPMDirection( 0,  0,  1);
    public static final ZPMDirection NORTH  = new ZPMDirection( 0,  0, -1);

    public final int index, dx, dy, dz;

    private ZPMDirection(int x, int y, int z) {
        dx = x;
        dy = y;
        dz = z;

        /* stfu, I know this is terrible */
        if (dx ==  1) index = 0;
        else if (dx == -1) index = 1;
        else if (dy ==  1) index = 2;
        else if (dy == -1) index = 3;
        else if (dz ==  1) index = 4;
        else index = 5;
    }

    public TileEntity apply(TileEntity t) {
        return t.worldObj.getBlockTileEntity(t.xCoord + dx, t.yCoord + dy, t.zCoord + dz);
    }

    public ZPMDirection invert() {
        return new ZPMDirection(-dx, -dy, -dz);
    }
}
