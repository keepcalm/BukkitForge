package keepcalm.mods.bukkit.forgeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import keepcalm.mods.bukkit.CraftPlayerCache;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.messaging.StandardMessenger;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;


/**
 * This packet handler does not listen to any channels by default,
 * but when it is requested to be registered by a Craft plugin, the channel
 * will be listened for
 * 
 * @author keepcalm
 *
 */
public class ForgePacketHandler implements IPacketHandler {
	
	public static HashMap<String,List<String>> listeningChannels = Maps.newHashMap();
	
	private static ForgePacketHandler INSTANCE;
	
	public ForgePacketHandler() {
		this.INSTANCE = this;
	}
	
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			return; // nothing client-side here.
		}
		EntityPlayer fp = (EntityPlayer) player;
		if (this.listeningChannels.values().contains(packet.channel) && this.listeningChannels.get(fp.username).contains(packet.channel)) {
			((StandardMessenger)CraftServer.instance().getMessenger()).dispatchIncomingMessage(CraftPlayerCache.getCraftPlayer((EntityPlayerMP)player), packet.channel, packet.data);
			
		}
		
	}
	
	public static void registerChannel(String chan, EntityPlayer player) {
		
		if (!listeningChannels.containsKey(player.username)) {
			listeningChannels.put(player.username, new ArrayList<String>());
		}
		
		if (!listeningChannels.get(player.username).contains(chan)) {
			NetworkRegistry.instance().registerChannel(INSTANCE, chan, Side.SERVER);
			listeningChannels.get(player.username).add(chan);
		}
	}

}
