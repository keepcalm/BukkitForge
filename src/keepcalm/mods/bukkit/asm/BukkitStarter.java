package keepcalm.mods.bukkit.asm;

import java.util.logging.Level;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.forgeHandler.ForgeEventHandler;
import keepcalm.mods.bukkit.forgeHandler.commands.BukkitCommandConsole;
import keepcalm.mods.bukkit.forgeHandler.commands.BukkitCommandHelp;
import keepcalm.mods.bukkit.forgeHandler.commands.BukkitCommandMVFix;
import keepcalm.mods.bukkit.forgeHandler.commands.BukkitCommandMods;
import keepcalm.mods.bukkit.forgeHandler.commands.BukkitCommandStop;
import keepcalm.mods.bukkit.forgeHandler.commands.CommandBukkitForge;
import keepcalm.mods.bukkit.forgeHandler.commands.CommandRequirementRegistry;
import keepcalm.mods.bukkit.forgeHandler.commands.CommandSetLevel;
import net.minecraft.command.CommandServerDeop;
import net.minecraft.command.CommandServerOp;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;

import org.bukkit.craftbukkit.CraftServer;

import cpw.mods.fml.common.FMLCommonHandler;

public class BukkitStarter implements Runnable {
	private MinecraftServer server;
	public BukkitStarter(MinecraftServer serv) {
		server = serv;
	}
	
	@Override
	public void run() {
		try {
			ServerCommandManager scm = (ServerCommandManager) server.getCommandManager();
			scm.registerCommand(new BukkitCommandHelp());
			scm.registerCommand(new BukkitCommandMVFix());
			scm.registerCommand(new BukkitCommandMods());
			scm.registerCommand(new CommandBukkitForge());
			if (!(server instanceof DedicatedServer)) {
				scm.registerCommand(new BukkitCommandConsole());
				scm.registerCommand(new CommandServerOp());
				scm.registerCommand(new CommandServerDeop());
				server.getConfigurationManager().addOp(server.getServerOwner().toLowerCase());
			}
			CommandRequirementRegistry.load();
			scm.registerCommand(new CommandSetLevel());
			
			BukkitContainer.bServer = new CraftServer(MinecraftServer.getServer());
			scm.registerCommand(new BukkitCommandStop());
		}
		catch (Exception e) {
			// disable nicely
			ForgeEventHandler.ready = false;
			FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, "Something real bad happened! The BukkitAPI will not be running for this Minecraft session.\n" +
					"You should *probably* report this bug to the developer(s) at https://github.com/keepcalm/BukkitForge/issues", e);
		}

	}
	
	

}
