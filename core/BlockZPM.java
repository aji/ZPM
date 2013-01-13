package net.ajitek.mc.zpm.core;

import net.ajitek.mc.zpm.proxy.*;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;
import net.minecraft.src.EntityPlayer;

import java.util.ArrayList;

public class BlockZPM extends BlockContainer {
    private int texBase = 0;
    private int renderType = 0;
    private int modelId = 0;

    public BlockZPM(int id, Material mat) {
        super(id, mat);
        setHardness(2.0F);
        setStepSound(Block.soundMetalFootstep);
        setBlockName("ajitek.zpm");
        setRequiresSelfNotify();
        config(false, false);
    }

    public void config(boolean glowing, boolean item) {
        if (!glowing) {
            texBase = 0;
            setLightValue(0.0F);
        } else {
            texBase = 16;
            setLightValue(0.8F);
        }

        if (item) {
            renderType = 0;
        } else {
            renderType = modelId;
        }
    }

    @Override
    public int getRenderType() {
        return renderType;
    }

    public void setModelId(int id) {
        modelId = id;
        renderType = modelId;
    }

    @Override
    public int getBlockTextureFromSideAndMetadata(int side, int meta) {
        if (side < 2)
            return texBase;
        return texBase + meta + 1;
    }

    @Override
    public boolean blockActivated(World w, int x, int y, int z, EntityPlayer ep) {
        TileEntity tile;
        TileEntityZPM zpm;

        if (ep.isSneaking())
            return false;

        tile = w.getBlockTileEntity(x, y, z);
        if (!(tile instanceof TileEntityZPM))
            return false;

        //ep.openGui(mod_ZPM.instance(), 0, w, x, y, z);

        zpm = (TileEntityZPM)tile;
        zpm.setDraining(!zpm.getDraining());

        if (!w.isRemote) {
            if (zpm.getDraining()) {
                ep.addChatMessage("ajitek.zpm.draining");
            } else {
                ep.addChatMessage("ajitek.zpm.filling");
            }
        }

        return true;
    }

    public TileEntity getBlockEntity() {
        return new TileEntityZPM();
    }

    @Override
    public String getTextureFile() {
        return Common.BLOCK_PNG;
    }

    @Override
    public void addCreativeItems(ArrayList itemList) {
        itemList.add(new ItemStack(this));
    }
}
