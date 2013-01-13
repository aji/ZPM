package net.ajitek.mc.zpm.proxy;

import net.ajitek.mc.zpm.core.*;
import net.minecraft.src.Block;
import net.minecraft.src.Material;
import net.minecraft.src.Item;
import net.minecraft.src.ItemBlock;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.World;
import net.minecraft.src.NetServerHandler;
import net.minecraft.src.TileEntity;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Packet;
import net.minecraft.src.forge.Configuration;
import net.minecraft.src.forge.Property;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;
import net.minecraft.src.forge.IConnectionHandler;
import net.minecraft.src.forge.IGuiHandler;
import net.minecraft.src.forge.IPacketHandler;
import net.minecraft.src.forge.MessageManager;
import cpw.mods.fml.server.FMLServerHandler;
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

        Common.initConfig(new File(new File(".", "config"), "ZPM.conf"));
        Common.initBlock();

        MinecraftForge.setGuiHandler(this, this);
        MinecraftForge.registerConnectionHandler(this);
    }

    @Override
    public String getVersion() {
        return "v" + Common.VERSION;
    }


    /* Packeting */

    public void handlePacket(NetworkManager net, DataInputStream in) throws IOException {
        World world;

        if (!(net.getNetHandler() instanceof NetServerHandler))
            return;

        world = ((NetServerHandler)net.getNetHandler()).getPlayerEntity().worldObj;

        switch (in.readByte()) {
        case 0: /* tile entity update */
            int x = in.readInt();
            int y = in.readInt();
            int z = in.readInt();
            TileEntity tile = world.getBlockTileEntity(x, y, z);

            if (tile != null && tile instanceof TileEntityBase)
                ((TileEntityBase)tile).handleUpdatePacket(net, in, true);
        }
    }

    public void sendPacketToServer(Packet pkt) {
    }

    public void sendPacketToPlayer(String player, Packet pkt) {
        FMLServerHandler.instance().getServer().configManager.sendPacketToPlayer(player, pkt);
    }


    /* IGuiHandler */

    public Object getGuiElement(int ID, EntityPlayer p, World world, int x, int y, int z) {
        TileEntity te;
        EntityPlayerMP player;

        if (!(p instanceof EntityPlayerMP))
            return null;
        if (!world.blockExists(x, y, z))
            return null;

        player = (EntityPlayerMP)p;
        te = world.getBlockTileEntity(x, y, z);

        if (te != null && te instanceof TileEntityBase)
            ((TileEntityBase)te).sendUpdateToPlayer(p.getUsername());

        return null;
    }


    /* IPacketHandler */

    public void onPacketData(NetworkManager net, String channel, byte[] data) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

        if (!channel.equals(Common.CHANNEL))
            return;

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
