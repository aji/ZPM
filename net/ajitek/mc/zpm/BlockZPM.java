package net.ajitek.mc.zpm;

import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;

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
