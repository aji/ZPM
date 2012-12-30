package net.ajitek.mc.zpm.proxy;

import net.ajitek.mc.zpm.core.*;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ModLoader;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.Property;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.MinecraftForgeClient;
import net.minecraft.src.forge.NetworkMod;
import net.minecraft.src.forge.IConnectionHandler;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.forge.IPacketHandler;
import net.minecraft.src.forge.MessageManager;
import cpw.mods.fml.client.FMLClientHandler;
import java.io.File;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class mod_ZPM extends NetworkMod implements IConnectionHandler, IGuiHandler, IPacketHandler {
	private static mod_ZPM INSTANCE = null;

	public static mod_ZPM instance() {
		return INSTANCE;
	}

	@Override
	public void load() {
		mod_ZPM.INSTANCE = this;

		Common.initConfig(new File(new File(Minecraft.getMinecraftDir(), "config"), "ZPM.conf"));
		Common.initBlock();

		MinecraftForgeClient.preloadTexture(Common.BLOCK_PNG);
		//MinecraftForgeClient.preloadTexture(Common.GUI_PNG);

		MinecraftForge.setGuiHandler(this, this);
		MinecraftForge.registerConnectionHandler(this);

		Common.blockZPM.setModelId(FMLClientHandler.instance().obtainBlockModelIdFor(this, true));
	}

	@Override
	public String getVersion() {
		return "v" + Common.VERSION;
	}


	/* Packeting */

	public void handlePacket(NetworkManager net, DataInputStream in) throws IOException {
		World world = FMLClientHandler.instance().getClient().theWorld;

		switch (in.readByte()) {
		case 0: /* tile entity update */
			int x = in.readInt();
			int y = in.readInt();
			int z = in.readInt();
			TileEntity tile = world.getBlockTileEntity(x, y, z);

			if (tile != null && tile instanceof TileEntityBase)
				((TileEntityBase)tile).handleUpdatePacket(net, in, false);
		}
	}

	public void sendPacketToServer(Packet pkt) {
		FMLClientHandler.instance().sendPacket(pkt);
	}

	public void sendPacketToPlayer(String player, Packet pkt) {
	}


	/* Rendering */

	public boolean renderWorldBlock(RenderBlocks renderer, IBlockAccess world, int x, int y, int z,
					Block block, int modelId) {
		renderer.renderStandardBlock(block, x, y, z);
		Common.blockZPM.config(true, false);
		renderer.renderStandardBlock(block, x, y, z);
		Common.blockZPM.config(false, false);

		return true;
	}

	public void renderInvBlock(RenderBlocks renderer, Block block, int metadata, int modelId) {
		Common.blockZPM.config(false, true);
		renderer.renderBlockAsItem(block, metadata, 1.0F);
		Common.blockZPM.config(true, true);
		renderer.renderBlockAsItem(block, metadata, 1.0F);
		Common.blockZPM.config(false, false);
	}


	/* IGuiHandler */

	public Object getGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity te;

		if (!world.blockExists(x, y, z))
			return null;

		te = world.getBlockTileEntity(x, y, z);

		if (te == null || !(te instanceof TileEntityBase))
			return null;

		TileEntityBase teb = (TileEntityBase)te;
		String guiClassName = teb.getGuiClassName();
		Class guiClass;
		Object guiObject;

		if (guiClassName == null)
			return null;

		try {
			guiClass = Class.forName(guiClassName);
			guiObject = guiClass.newInstance();
		} catch (Exception e) {
			/* TODO: be smarter about this */
			return null;
		}

		if (!(guiObject instanceof GuiBase))
			return null;

		((GuiBase)guiObject).setTileEntity(teb);

		if (world.isRemote)
			teb.clientGuiReinit();

		return guiObject;
	}


	/* IPacketHandler */

	public void onPacketData(NetworkManager net, String channel, byte[] data) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

		if (!channel.equals(Common.CHANNEL))
			return; /* shouldn't happen, but just in case */

		try {
			handlePacket(net, in);
		} catch (Exception e) {
			return;
		}
	}


	/* IConnectionHandler */

	public void onConnect(NetworkManager net) {}

	public void onLogin(NetworkManager net, Packet1Login login) {
		MessageManager.getInstance().registerChannel(net, this, Common.CHANNEL);
	}

	public void onDisconnect(NetworkManager net, String message, Object[] args) {
		MessageManager.getInstance().unregisterChannel(net, this, Common.CHANNEL);
	}
}
