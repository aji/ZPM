package net.ajitek.mc.zpm.core;

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
	private final int texBase = 0;

	public BlockZPM(int id, Material mat) {
		super(id, mat);
		setHardness(0.5F);
		setStepSound(Block.soundMetalFootstep);
		setBlockName("ajitek.zpm");
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		if (side < 2)
			return texBase;
		return texBase + 1;
	}

	@Override
	public boolean blockActivated(World w, int x, int y, int z, EntityPlayer ep) {
		if (ep.isSneaking())
			return false;

		ep.openGui(mod_ZPM.getInstance(), 0, w, x, y, z);
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
