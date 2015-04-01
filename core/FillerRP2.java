package net.ajitek.mc.zpm.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.src.TileEntity;

public class FillerRP2 implements IFiller {
    private Class TBB; /* TileBatteryBox */
    private Field TBB_Storage;
    private Method TBB_getMaxStorage;

    public boolean initialize() {
        try {
            TBB = Class.forName("eloraam.machine.TileBatteryBox");
            TBB_Storage = TBB.getField("Storage");
            TBB_getMaxStorage = TBB.getMethod("getMaxStorage");
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private int getMaxStorage(TileEntity tile) {
        try {
            return ((Integer)TBB_getMaxStorage.invoke(tile)).intValue();
        } catch (Exception e) {
            return 6000;
        }
    }

    private int getStorage(TileEntity tile) {
        try {
            return TBB_Storage.getInt(tile);
        } catch (Exception e) {
            return 0;
        }
    }

    private void setStorage(TileEntity tile, int s) {
        try {
            TBB_Storage.setInt(tile, s);
        } catch (Exception e) {
            /* ... */
        }
    }

    public boolean canFill(TileEntity tile) {
        return TBB.isInstance(tile);
    }

    public void fill(ZPMDirection dir, TileEntity tile) {
        setStorage(tile, getMaxStorage(tile));
    }

    public void drain(ZPMDirection dir, TileEntity tile) {
        setStorage(tile, 0);
    }
}
