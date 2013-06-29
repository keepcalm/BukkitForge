package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.util.List;
import java.util.Set;

import keepcalm.mods.bukkit.ToBukkit;
import keepcalm.mods.bukkit.forgeHandler.ForgePacketHandler;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_5_R3.entity.CraftPlayer;

public class NetworkRegistry_BukkitForge {

    private Set<IConnectionHandler> connectionHandlers = Sets.newLinkedHashSet();
    
    private static final NetworkRegistry_BukkitForge INSTANCE = new NetworkRegistry_BukkitForge();

    public static NetworkRegistry_BukkitForge instance()
    {
        return INSTANCE;
    }

    public void playerLoggedIn(EntityPlayerMP player, NetServerHandler netHandler, INetworkManager manager)
    {
        generateChannelRegistration(player, netHandler, manager);
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.playerLoggedIn((Player)player, netHandler, manager);
        }
    }

    void generateChannelRegistration(EntityPlayer player, NetHandler netHandler, INetworkManager manager)
    {
    }
    
    /* To intercept REGISTER packets */
    @AsmagicReplaceMethod
    private void handleRegistrationPacket(Packet250CustomPayload packet, Player player)
    {
        List<String> channels = extractChannelList(packet);
        for (String channel : channels)
        {
            activateChannel(player, channel);
            //BukkitForge start
            if(Bukkit.getServer().getMessenger().getIncomingChannels().contains(channel))
            {
            	 ((CraftPlayer)ToBukkit.player((EntityPlayer)player)).addChannel(channel);
            	 NetworkRegistry.instance().registerChannel(ForgePacketHandler.instance(), channel);
            	 ForgePacketHandler.registerChannel(channel, (EntityPlayer)player);
            }
            //BukkitForge end
        }
    }
    
    /* To intercept UNREGISTER packets */
    @AsmagicReplaceMethod
    private void handleUnregistrationPacket(Packet250CustomPayload packet, Player player)
    {
        List<String> channels = extractChannelList(packet);
        for (String channel : channels)
        {
            deactivateChannel(player, channel);
            //BukkitForge start
            if(Bukkit.getServer().getMessenger().getIncomingChannels().contains(channel))
            {
            	((CraftPlayer)ToBukkit.player((EntityPlayer)player)).removeChannel(channel);
            }
            //BukkitForge end
        }
    }
    
    private List<String> extractChannelList(Packet250CustomPayload packet)
    {
    	return null;
    }
    
    void deactivateChannel(Player player, String channel)
    {
    	
    }
    
    void activateChannel(Player player, String channel)
    {
    	
    }
}
