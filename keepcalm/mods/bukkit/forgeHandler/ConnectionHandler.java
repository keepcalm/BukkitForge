package keepcalm.mods.bukkit.forgeHandler;

import java.util.HashMap;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet1Login;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class ConnectionHandler implements IConnectionHandler {
	public static HashMap<String,NetHandler> serverHandlers = new HashMap();
	public static NetworkManager netman;
	
	@Override
	public void clientLoggedIn(NetHandler clientHandler,
			NetworkManager manager, Packet1Login login) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionClosed(NetworkManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionOpened(NetHandler netClientHandler,
			MinecraftServer server, NetworkManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server,
			int port, NetworkManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler,
			NetworkManager manager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler,
			NetworkManager manager) {
		EntityPlayerMP guy = (EntityPlayerMP) player;
		//this.serverHandlers.put(guy.username, netHandler);
		this.netman = manager;
		BukkitServer.instance().setPlayerFauxSleeping(guy.username, false);

	}

}
