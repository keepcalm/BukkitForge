package keepcalm.mods.bukkit.asm;

import java.io.File;
import java.nio.CharBuffer;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.forgeHandler.BlockBreakEventHandler;
import keepcalm.mods.bukkit.forgeHandler.ConnectionHandler;
import keepcalm.mods.bukkit.forgeHandler.ForgeEventHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.DedicatedServer;
import net.minecraft.src.ServerGUI;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.FMLRelaunchLog;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
//import net.minecraftforge.event.EventBus;
//import net.minecraftforge.event.EventBus;

public class BukkitContainer extends DummyModContainer {
	public static BukkitServer bServer;
	public File myConfigurationFile;
	public boolean allowAnsi;
	public String pluginFolder;
	public static boolean showAllLogs;
	public static String serverUUID;
	public static Logger bukkitLogger ;//.getLogger("[Bukkit API]");
	
	
	private static BukkitContainer instance;
	private static ThreadGroup theThreadGroup;
	
	public BukkitContainer() {
		super(new ModMetadata());
		if (MinecraftServer.getServer().getGuiEnabled())
			FMLRelaunchLog.log.getLogger().addHandler(new BukkitLogHandler());
		instance = this;
		/*BukkitContainer.bukkitLogger = FMLRelaunchLog.log.getLogger();
		if (bukkitLogger == null) {
			bukkitLogger = FMLCommonHandler.instance().getFMLLogger();
		}*/

		ModMetadata meta = this.getMetadata();
		meta.modId = "Bukkit4Vanilla";
		meta.name = "Bukkit For Vanilla";
		meta.version = BukkitServer.version + ", implementing Bukkit version " + BukkitServer.apiVer;
		meta.authorList = Arrays.asList(new String[]{"keepcalm"});
		meta.description = "An implementation Bukkit API for vanilla Minecraft.";
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
			super.setEnabledState(false);
			meta.description = meta.description + " - \u00A74Only for dedicated servers!\u00A7r";
			//meta.modId = null;
			//meta = null;
			
			return;
		}
		
		
		
		
		
	}
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
	@Subscribe
	public void preInit(FMLPreInitializationEvent ev) {
		bukkitLogger = ev.getModLog();
		bukkitLogger.setParent(ServerGUI.logger);
		//ServerGUI.logger = bukkitLogger;
		for (Handler i : MinecraftServer.logger.getHandlers()) {
			System.out.println(i.getClass().getName());
		}
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			
			FMLCommonHandler.instance().getFMLLogger().warning("Bukkit For Vanilla is currently only a server-side mod.");
			return;
		}
		else if (FMLCommonHandler.instance().getEffectiveSide() == Side.BUKKIT) {
			FMLCommonHandler.instance().getFMLLogger().severe("The bukkit API as a forge mod on bukkit? *mind blown*");
			return;
		}
		this.bukkitLogger.info("Initializing configuration...");
		myConfigurationFile = ev.getSuggestedConfigurationFile();
		
		Configuration config = new Configuration(myConfigurationFile);
		config.addCustomCategoryComment("consoleConfig", "Configuration for the server console");
		config.addCustomCategoryComment("dontTouchThis", "Things which are best left untouched");
		
		Property colour = config.get("consoleConfig", "enablecolour", DedicatedServer.getServer().getGuiEnabled() ? false : true);
		colour.comment = "Enable coloured ANSI console output";
		this.allowAnsi = colour.getBoolean(false);
		
		Property plugins = config.get(Configuration.CATEGORY_GENERAL, "pluginDir", "plugins");
		plugins.comment = "The folder to look for plugins in.";
		this.pluginFolder = plugins.value;
		
		Property suuid = config.get("dontTouchThis", "serverUUID", this.genUUID());
		System.out.println("[Bukkit API]: Set UUID to " + suuid.value);
		suuid.comment = "The UUID of the server. Don't touch this or it might break your plugins.";
		this.serverUUID = suuid.value;
		
		Property showAllLogs = config.get(Configuration.CATEGORY_GENERAL, "printForgeLogToGui", false);
		showAllLogs.comment = "Print stuff that's outputted to the logs to the GUI as it happens.";
		this.showAllLogs = showAllLogs.getBoolean(false);
		
		config.save();
		
		
		
		
		
	}
	
	public static BukkitContainer getInstance() {
		return instance;
	}
	private String genUUID() {
		String res = "" + System.currentTimeMillis();
		res += new Random().nextInt();
		res += "-" + BukkitServer.version;
		return res;
	}
	@Subscribe
	public void init(FMLInitializationEvent ev) {
		System.out.println("[Bukkit API]: Complete! Registering handlers...");
		NetworkRegistry.instance().registerConnectionHandler(new ConnectionHandler());
		PrintStream oldErr = System.err;
		
		if (Loader.isModLoaded("BlockBreak")) {
			try {
				MinecraftForge.EVENT_BUS.register(new BlockBreakEventHandler()) ;
			}
			catch (Exception e) {
				bukkitLogger.log(Level.FINE, "BlockBreak is present, but not!", e);
			}
		}
		
		// FIXME: For some reason this bugs forge...
		try {
			// get rid of those annoying messages!
			System.setErr(null);
			MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
			System.setErr(oldErr);
		}
		catch (Throwable e) {
			FMLCommonHandler.instance().getFMLLogger().severe("[Bukkit API]: FAILED to add event handers:");
			//e.printStackTrace();
		}
		System.out.println("Done!");
	}
	@Subscribe
	//@SideOnly(Side.SERVER)
	public void serverStarting(FMLServerStartingEvent ev) {
		if (ev.getServer().isDedicatedServer() == false) {
			return;
		}
		bukkitLogger.info("Starting the API, implementing Bukkit API version " + BukkitServer.version);
		this.theThreadGroup = new ThreadGroup("Bukkit4Vanilla");
		Thread bukkitThread = new Thread(theThreadGroup, new BukkitStarter(), "BukkitCoreAPI-0");
		bukkitThread.setDaemon(true);
		bukkitThread.start();
		
		//System.out.println("Hello!");
		//bServer.resetRecipes();
	}
}
