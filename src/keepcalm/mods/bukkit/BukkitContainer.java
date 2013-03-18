package keepcalm.mods.bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import keepcalm.mods.bukkit.asm.BukkitStarter;
import keepcalm.mods.bukkit.common.CommonProxy;
import keepcalm.mods.bukkit.forgeHandler.BlockBreakEventHandler;
import keepcalm.mods.bukkit.forgeHandler.BukkitCraftingHandler;
import keepcalm.mods.bukkit.forgeHandler.BukkitCrashCallable;
import keepcalm.mods.bukkit.forgeHandler.ConnectionHandler;
import keepcalm.mods.bukkit.forgeHandler.ForgeEventHandler;
import keepcalm.mods.bukkit.forgeHandler.ForgePacketHandler;
import keepcalm.mods.bukkit.forgeHandler.PlayerTracker;
import keepcalm.mods.bukkit.forgeHandler.SchedulerTickHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.gui.ServerGUI;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.bukkit.craftbukkit.utils.Versioning;

import com.google.common.base.Joiner;
import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
//import net.minecraftforge.event.EventBus;
//import net.minecraftforge.event.EventBus;

@Mod(modid="BukkitForge",name="BukkitForge",version="Unknown",certificateFingerprint="")
@NetworkMod(clientSideRequired=false,serverSideRequired=false,connectionHandler=ConnectionHandler.class,serverPacketHandlerSpec=@SidedPacketHandler(channels={},packetHandler=ForgePacketHandler.class))
public class BukkitContainer {
	public static Properties users;
	
	public static final String BF_FULL_VERSION = Versioning.getBFVersion();
	
	public static CraftServer bServer;
	public File myConfigurationFile;
	public static boolean allowAnsi;
	public String pluginFolder;

	private Thread updateCheckerThread;
	public static boolean showAllLogs;
	public static boolean isDediServer;
	public static String serverUUID;
	public static boolean overrideVanillaCommands;
	public static Logger bukkitLogger ;//.getLogger("[Bukkit API]");
	public static boolean DEBUG = false;// ClassLoader.getSystemResourceAsStream("/net/minecraft/item") == null;
	// hehe
	public static boolean IGNORE_CONNECTION_RECEIVED = false;
	public static String MOD_USERNAME = "[Mod]";
	public static EntityPlayerMP MOD_PLAYER;
	public static String[] pluginsInPath;
	public static String CRAFT_VERSION;
	public static String LOADING_KICK_MESSAGE;
	
	private static File propsFile;
	
	private static boolean isGuiEnabled = false;
	
	@SidedProxy(clientSide="keepcalm.mods.bukkit.client.ClientProxy",serverSide="keepcalm.mods.bukkit.common.CommonProxy")
	public static CommonProxy theProxy;

	@Metadata("BukkitForge")
	public static ModMetadata meta;

	@Instance("BukkitForge")
	public static BukkitContainer instance;
	private static Thread bThread;

	public static int UPDATE_CHECK_INTERVAL;
	public static boolean ENABLE_UPDATE_CHECK;
	/**
	 * 1 for console-only, 0 for broadcast, -1 for both
	 */
	public static int UPDATE_ANNOUNCE_METHOD;

	static {
		System.out.println("THIS SERVER IS RUNNING BUKKITFORGE " + BF_FULL_VERSION + ". JUST IN CASE SOMEONE ASKS!");
	}
	
	public BukkitContainer() {
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			isDediServer = true;
			if (MinecraftServer.getServer() != null && MinecraftServer.getServer().getGuiEnabled()) {
				isGuiEnabled = true;
			}
		}
		else
			isDediServer = false;
		instance = this;
		

	}
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
	@PreInit
	public void preInit(FMLPreInitializationEvent ev) {
		bukkitLogger = ev.getModLog();
		bukkitLogger.setParent(FMLCommonHandler.instance().getFMLLogger());
		try {
			bukkitLogger.addHandler(new FileHandler("server.log"));
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		meta.modId = "BukkitForge";
		meta.name = "BukkitForge";
		meta.version = BF_FULL_VERSION;
		meta.authorList = Arrays.asList(new String[]{"keepcalm"});
		meta.description = "An implementation Bukkit API for vanilla Minecraft.";
		
		meta.autogenerated = false;

		//theProxy.registerKeyBindings();
		BukkitContainer.bukkitLogger.info("Initializing configuration...");
		myConfigurationFile = ev.getSuggestedConfigurationFile();

		Configuration config = new Configuration(myConfigurationFile);
		config.addCustomCategoryComment("consoleConfig", "Configuration for the server console");
		config.addCustomCategoryComment("dontTouchThis", "Things which are best left untouched");

		Property ignore = config.get(Configuration.CATEGORY_GENERAL, "ignoreConnectionHandler", false);
		ignore.comment = "Ignore connection events. If you get kicked from the server when output has stopped from the console, turn this ON";
		BukkitContainer.IGNORE_CONNECTION_RECEIVED = ignore.getBoolean(false);
		
		Property override = config.get(Configuration.CATEGORY_GENERAL, "overrideVanillaCommands", false);
		override.comment = "Override vanilla commands (/me etc) with Bukkit defaults (won't stop plugins from overriding)";
		BukkitContainer.overrideVanillaCommands = override.getBoolean(false);
		
		Property build = config.get(Configuration.CATEGORY_GENERAL, "bukkitVersionString", "git-Bukkit-1.4.5-R1.0-b3000jnks (Really: BukkitForge for MC " + Loader.instance().getMinecraftModContainer().getDisplayVersion() + ")");
		build.comment = "The CraftBukkit version to pretend to be";
		BukkitContainer.CRAFT_VERSION = build.value;
		
		Property plugins = config.get(Configuration.CATEGORY_GENERAL, "pluginsToLoad", "");
		plugins.comment = "Comma-separated list of plugins which are in the classpath to load. Only developers need use this option.";
		BukkitContainer.pluginsInPath = plugins.value.isEmpty() ? new String[] {} : plugins.value.split(",");
		
		Property kickMsg = config.get(Configuration.CATEGORY_GENERAL, "kickMessage", "Patience, my padawan! BukkitForge is still loading.\nTry again in a few moments...");
		kickMsg.comment = "Message to kick players with if they try to join before BukkitForge is loaded. \n makes a new line";
		BukkitContainer.LOADING_KICK_MESSAGE = kickMsg.value;
		
		Property debug = config.get("consoleConfig", "debug", false);
		debug.comment = "Print debug messages";
		BukkitContainer.DEBUG = debug.getBoolean(false);
		
		Property colour = config.get("consoleConfig", "enablecolour", isGuiEnabled ? false : true);
		colour.comment = "Enable coloured ANSI console output";
		BukkitContainer.allowAnsi = colour.getBoolean(false);

		Property pluginDir = config.get(Configuration.CATEGORY_GENERAL, "pluginDir", "plugins");
		pluginDir.comment = "The folder to look for plugins in.";
		this.pluginFolder = pluginDir.value;

		Property suuid = config.get("dontTouchThis", "serverUUID", this.genUUID());
		bukkitLogger.info("Set UUID to " + suuid.value);
		suuid.comment = "The UUID of the server. Don't touch this or it might break your plugins.";
		BukkitContainer.serverUUID = suuid.value;
		
		Property modActionName = config.get(Configuration.CATEGORY_GENERAL, "modActionUserName", "[Mod]");
		modActionName.comment = "The name of the player to use when passing events from mods (such as block breaks) to plugins";
		BukkitContainer.MOD_USERNAME = modActionName.value;
		
		config.addCustomCategoryComment("updatechecking", "Update-related stuff");
		
		Property enableUpdateThread = config.get("updatechecking", "allowUpdateChecking", true);
		enableUpdateThread.comment = "Allow BukkitForge to automatically notify you when there are updates.";
		BukkitContainer.ENABLE_UPDATE_CHECK = enableUpdateThread.getBoolean(true);
		
		Property updateThreadInterval = config.get("updatechecking", "updateInterval", 216000);
		updateThreadInterval.comment = "Number of seconds between checks - min 3000, max 2^31-1";
		BukkitContainer.UPDATE_CHECK_INTERVAL = updateThreadInterval.getInt();
		if (UPDATE_CHECK_INTERVAL < 3000) {
			UPDATE_CHECK_INTERVAL = 3000;
		}
		
		Property updateMode = config.get("updatechecking", "updateAnnounceMode", "-1");
		updateMode.comment = "Mode to announce new updates. 0 is broadcast a message on the server, 1 is print to console, -1 is both.";
		BukkitContainer.UPDATE_ANNOUNCE_METHOD = updateMode.getInt(-1);
		if (UPDATE_ANNOUNCE_METHOD < -1 || UPDATE_ANNOUNCE_METHOD > 1) {
			UPDATE_ANNOUNCE_METHOD = -1;
		}
		/*Property showAllLogs = config.get(Configuration.CATEGORY_GENERAL, "printForgeLogToGui", false);
		showAllLogs.comment = "Print stuff that's outputted to the logs to the GUI as it happens.";
		this.showAllLogs = showAllLogs.getBoolean(false);*/

		config.save();
		GameRegistry.registerPlayerTracker(new PlayerTracker());
		GameRegistry.registerCraftingHandler(new BukkitCraftingHandler());
		/*if (ENABLE_UPDATE_CHECK) {
			this.updateCheckerThread = new Thread(new HttpUpdateCheckerThread(), "BukkitForge-HttpChecker");
			updateCheckerThread.start();
		}*/
		FileInputStream fis;
		propsFile = null;
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			propsFile = new File("users.properties");
			try {
				propsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
		}
		else {
			// it's always this
			Class<?> mcClazz;
			try {
				mcClazz = Class.forName("net.minecraft.client.Minecraft");
			} catch (ClassNotFoundException e) {
				bukkitLogger.warning("FAILED to load minecraft main class!");
				return;
			}
			for (Field i : mcClazz.getFields()) {
				if (i.getModifiers() == Modifier.STATIC + Modifier.PRIVATE) {
					if (i.getType().equals(File.class)) {
						System.out.println("Found mc file field: private static " + mcClazz.getCanonicalName() + "." + i.getName());
						i.setAccessible(true);
						try {
							propsFile = new File((File) i.get(null), "users.properties");
						} catch (Exception e) {
							e.printStackTrace();
							return;
						}
						
					}
				}
			}
		}

        Packet3Chat.maxChatLength = 32767;
		
		BukkitContainer.users = new Properties();
		if (propsFile == null) return;
		try {
			fis = new FileInputStream(propsFile);
			users.load(fis);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		//System.out.println("Successfully loaded users: " + Joiner.on(' ').join(users.keySet()));
	}

	public static BukkitContainer getInstance() {
		return instance;
	}


	private String genUUID() {
		String res = "" + System.currentTimeMillis();
		res += new Random().nextInt();
		res += "-" + CraftServer.version;
		return res;
	}


	@Init
	public void init(FMLInitializationEvent ev) {
		ItemInWorldManager.class.desiredAssertionStatus();
		FMLCommonHandler.instance().registerCrashCallable(new BukkitCrashCallable());
		
		TickRegistry.registerTickHandler(new SchedulerTickHandler(), Side.SERVER);
		
		NetworkRegistry.instance().registerConnectionHandler(new ConnectionHandler());
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
			FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE,"[Bukkit API]: FAILED to add event handlers:", e);
			//e.printStackTrace();
		}
		bukkitLogger.info("Done!");
	}


	@ServerStarting
	public void serverStarting(FMLServerStartingEvent ev) {
		ThreadGroup theThreadGroup = new ThreadGroup("BukkitForge");
		BukkitContainer.bThread = new Thread(theThreadGroup, new BukkitStarter(ev.getServer()), "BukkitCoreAPI-0");
		bThread.start();
	}
	
	@ServerStarted
	public void serverStarted(FMLServerStartedEvent ev) {
		BukkitContainer.MOD_PLAYER = new EntityPlayerMP(MinecraftServer.getServer(), MinecraftServer.getServer().worldServerForDimension(0), MOD_USERNAME, new ItemInWorldManager(MinecraftServer.getServer().worldServerForDimension(0)));
		System.out.println(MOD_PLAYER);
		
	}
	@ServerStopping
	public void serverStopping(FMLServerStoppingEvent ev) {
		// reset for potential next launch (if on client)
		
		BukkitContainer.bServer.shutdown();
		CraftScheduler.currentTick = -1;
		ForgeEventHandler.ready = false;
		SchedulerTickHandler.tickOffset = 0;
		if (propsFile == null) {
			return;
		}
		
		try {
			FileOutputStream fis = new FileOutputStream(propsFile);
			users.store(fis, "BukkitForge seen users. THIS IS TEMPORARY. Although it may become permanent.");
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}

}
