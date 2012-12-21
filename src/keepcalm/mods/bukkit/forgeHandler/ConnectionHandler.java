package keepcalm.mods.bukkit.forgeHandler;

import java.util.HashMap;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ConnectionHandler implements IConnectionHandler {
	public static HashMap<String,NetHandler> serverHandlers = new HashMap();
	public static INetworkManager netman;
	
	@Override
	public void clientLoggedIn(NetHandler clientHandler,
			INetworkManager manager, Packet1Login login) {

	}

	@Override
	public void connectionClosed(INetworkManager manager) {

	}

	@Override
	public void connectionOpened(NetHandler netClientHandler,
			MinecraftServer server, INetworkManager manager) {

	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server,
			int port, INetworkManager manager) {

	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler,
			INetworkManager manager) {
		return null;
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler,
			INetworkManager manager) {
		EntityPlayerMP guy = (EntityPlayerMP) player;
		//this.serverHandlers.put(guy.username, netHandler);
		this.netman = manager;
		BukkitServer.setPlayerFauxSleeping(guy.username, false);

	}

}
