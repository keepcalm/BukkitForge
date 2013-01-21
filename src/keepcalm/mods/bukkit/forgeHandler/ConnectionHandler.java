package keepcalm.mods.bukkit.forgeHandler;

import java.util.HashMap;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitPlayerCache;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.TcpConnection;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerLoginEvent;

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
		if (BukkitContainer.IGNORE_CONNECTION_RECEIVED) {
			return null;
		}
		if (!ForgeEventHandler.ready && (MinecraftServer.getServer() instanceof DedicatedServer)) {
			return BukkitContainer.LOADING_KICK_MESSAGE;
		}
		else if (!ForgeEventHandler.ready) {
			return null; // not single player - don't bother
		}
		boolean banned = MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isBanned(netHandler.clientUsername) || MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isBanned(netHandler.myTCPConnection.getSocket().getInetAddress().getHostAddress());
		boolean whitelisted = true;
		if (MinecraftServer.getServer().getConfigurationManager().isWhiteListEnabled())
			whitelisted = MinecraftServer.getServer().getConfigurationManager().getWhiteListedPlayers().contains(netHandler.clientUsername.toLowerCase());
		boolean shouldKick = MinecraftServer.getServer().getConfigurationManager().isAllowedToLogin(netHandler.clientUsername);

		boolean full = MinecraftServer.getServer().getMaxPlayers() <= MinecraftServer.getServer().getCurrentPlayerCount();
		Result wanted = full ? Result.KICK_FULL : banned ? Result.KICK_BANNED : !whitelisted ? Result.KICK_WHITELIST : shouldKick ? Result.KICK_OTHER : Result.ALLOWED;

		AsyncPlayerPreLoginEvent ev = new AsyncPlayerPreLoginEvent(netHandler.clientUsername, netHandler.myTCPConnection.getSocket().getInetAddress());
		ev.setLoginResult(wanted);
		ev.setKickMessage("");
		Bukkit.getPluginManager().callEvent(ev);

		if (ev.getLoginResult() != wanted) {
			// something different to what we originally had
			if (wanted == Result.ALLOWED) {
				// plugin kick reason
				return ev.getKickMessage();
			}
			else {
				// so we do allow them
				//netHandler.completeConnection(null);
				return null;
			}
		}

		if (ev.getLoginResult() == wanted) {
			if (ev.getLoginResult() == Result.ALLOWED) {
				return null;
			}
			else {
				if (ev.getKickMessage().isEmpty()) {
					// assume that FML will handle it
					return null;
				}
				else {
					// custom kick message
					return ev.getKickMessage();
				}
			}
		}

		return null;
	}

	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler,
			INetworkManager manager) {
		if (!(ForgeEventHandler.ready)) {
			return;
		}
		EntityPlayerMP guy = (EntityPlayerMP) player;
		//this.serverHandlers.put(guy.username, netHandler);
		EntityPlayerMP dude = (EntityPlayerMP) player;
		PlayerLoginEvent x;
		if (manager instanceof TcpConnection) {
			TcpConnection j = (TcpConnection) manager;
			x = new PlayerLoginEvent(BukkitPlayerCache.getBukkitPlayer(dude), MinecraftServer.getServer().getHostname(), j.getSocket().getInetAddress());
		}
		else {
			// logging in events on single player are presently broken, TODO
			dude.sendChatToPlayer(ChatColor.GREEN + "BukkitForge is loading, hold on a sec...");
			return;
			/*
			try {
				x = new PlayerLoginEvent(BukkitPlayerCache.getBukkitPlayer(dude), BukkitServer.instance().getServerName(), InetAddress.getLocalHost());
			} catch (Exception e) {
				BukkitServer.instance().getLogger().severe("FAILED to process login for player " + dude.username);
				return;
			}*/
		}
		Bukkit.getPluginManager().callEvent(x);
	}

}
