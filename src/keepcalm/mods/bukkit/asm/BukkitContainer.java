package keepcalm.mods.bukkit.asm;

import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.forgeHandler.BlockBreakEventHandler;
import keepcalm.mods.bukkit.forgeHandler.BukkitCrashCallable;
import keepcalm.mods.bukkit.forgeHandler.ConnectionHandler;
import keepcalm.mods.bukkit.forgeHandler.ForgeEventHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.gui.ServerGUI;
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
	private static Thread bThread;
	public BukkitContainer() {
		super(new ModMetadata());
		
		
		
		/*if (MinecraftServer.getServer().getGuiEnabled())
			FMLRelaunchLog.log.getLogger().addHandler(new BukkitLogHandler());*/
		if (MinecraftServer.getServer().getGuiEnabled()) {
			ServerGUI.logger.severe("BukkitForge plugins may misbehave when using the gui! Run the server with 'nogui'!");
		}
		instance = this;
		/*BukkitContainer.bukkitLogger = FMLRelaunchLog.log.getLogger();
		if (bukkitLogger == null) {
			bukkitLogger = FMLCommonHandler.instance().getFMLLogger();
		}*/

		ModMetadata meta = this.getMetadata();
		meta.modId = "BukkitForge";
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
		
		
		
		//ystem.out.println("CONTAINER - END");
		
	}
	public boolean registerBus(EventBus bus, LoadController controller) {
		//System.out.println("RegisterBus");
		bus.register(this);
		return true;
	}
	@Subscribe
	public void preInit(FMLPreInitializationEvent ev) {
		bukkitLogger = ev.getModLog();
		bukkitLogger.setParent(FMLCommonHandler.instance().getFMLLogger());
		
		
		
		//ServerGUI.logger = bukkitLogger;
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
		bukkitLogger.info("Set UUID to " + suuid.value);
		suuid.comment = "The UUID of the server. Don't touch this or it might break your plugins.";
		this.serverUUID = suuid.value;
		
		/*Property showAllLogs = config.get(Configuration.CATEGORY_GENERAL, "printForgeLogToGui", false);
		showAllLogs.comment = "Print stuff that's outputted to the logs to the GUI as it happens.";
		this.showAllLogs = showAllLogs.getBoolean(false);*/
		
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
		FMLCommonHandler.instance().registerCrashCallable(new BukkitCrashCallable());
		bukkitLogger.info("Complete! Registering handlers...");
		NetworkRegistry.instance().registerConnectionHandler(new ConnectionHandler());
		if (Loader.isModLoaded("BlockBreak")) {
			try {
				MinecraftForge.EVENT_BUS.register(new BlockBreakEventHandler()) ;
			}
			catch (Exception e) {
				bukkitLogger.log(Level.FINE, "BlockBreak is present, but not!", e);
			}
		}
		
		try {
			MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
		}
		catch (Throwable e) {
			FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE,"[Bukkit API]: FAILED to add event handers:", e);
			//e.printStackTrace();
		}
		bukkitLogger.info("Done!");
	}
	@Subscribe
	//@SideOnly(Side.SERVER)
	public void serverStarting(FMLServerStartingEvent ev) {
		//System.out.println("Starting!");
		if (ev.getServer().isDedicatedServer() == false) {
			return;
		}
		ThreadGroup theThreadGroup = new ThreadGroup("BukkitForge");
		this.bThread = new Thread(theThreadGroup, new BukkitStarter(ev.getServer()), "BukkitCoreAPI-0");
		//bThread.setDaemon(false);
		bThread.start();
		//System.out.println("Done!");
		
		//ForgeEventHandler.ready = true;
		
		
		//TickRegistry.registerTickHandler(new DefferedTaskHandler(), Side.SERVER);
		
		/*bukkitLogger.info("Allowing the API to load, please wait a few seconds...");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {}*/
		
		//System.out.println("Hello!");
		//bServer.resetRecipes();
	}
	
}
