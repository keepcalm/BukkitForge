package keepcalm.mods.bukkit.forgeHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitEntity;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import net.minecraft.src.DedicatedServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.NetHandler;
import net.minecraft.src.Packet3Chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cpw.mods.fml.common.network.IChatListener;

public class ChatHandler implements IChatListener {

	@Override
	public Packet3Chat clientChat(NetHandler arg0, Packet3Chat arg1) {
		return arg1;
	}

	@Override
	public Packet3Chat serverChat(NetHandler handler, Packet3Chat message) {
		Set<Player> guys = new HashSet();
		for (Object j : DedicatedServer.getServer().getConfigurationManager().playerEntityList) {
			guys.add((Player) BukkitEntity.getEntity((BukkitServer) Bukkit.getServer(), (EntityPlayerMP) j));
		}
		org.bukkit.event.player.AsyncPlayerChatEvent ev = new AsyncPlayerChatEvent(false, new BukkitPlayer((EntityPlayerMP) handler.getPlayer()), message.message, guys  );
		Bukkit.getPluginManager().callEvent(ev);
		if (ev.isCancelled()) {
			return null;
		}
		message.message = ev.getMessage();
		return message;
	}

}