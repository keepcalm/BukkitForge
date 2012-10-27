package keepcalm.mods.bukkit.asm;

import java.io.File;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.forgeHandler.ConnectionHandler;
import keepcalm.mods.bukkit.forgeHandler.ForgeEventHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.ServerGUI;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Property;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.DirectoryDiscoverer;
import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLLoadEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import cpw.mods.fml.relauncher.FMLCorePlugin;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.RelaunchClassLoader;
//import net.minecraftforge.event.EventBus;
//import net.minecraftforge.event.EventBus;

@TransformerExclusions
public class BukkitContainer extends DummyModContainer {
	public static BukkitServer bServer;
	public File myConfigurationFile;
	public boolean allowAnsi;
	public String pluginFolder;
	public static String serverUUID;
	public static Logger bukkitLogger ;//.getLogger("[Bukkit API]");
	private static BukkitContainer instance;
	
	public BukkitContainer() {
		super(new ModMetadata());
		
		instance = this;
		BukkitContainer.bukkitLogger = Logger.getLogger("BukkitAPI");
		LogManager.getLogManager().addLogger(BukkitContainer.bukkitLogger);
		ModMetadata meta = this.getMetadata();
		meta.modId = "Bukkit4Vanilla";
		meta.name = "Bukkit For Vanilla";
		meta.version = BukkitServer.version;
		meta.authorList = Arrays.asList(new String[]{"KeepCalm"});
		meta.description = "An implementation Bukkit API for vanilla Minecraft.";
		if (FMLCommonHandler.instance().getEffectiveSide() != Side.SERVER) {
			super.setEnabledState(false);
			meta.modId = null;
			meta = null;
			//meta.description = "An implementation Bukkit API for vanilla Minecraft - SMP ONLY";
			return;
		}
		ServerGUI.logger = bukkitLogger;
		MinecraftServer.logger.setParent(bukkitLogger);
		//meta.url = "http://www.minecraftforum.net/topic/909223-";
	}
	
	public boolean registerBus(EventBus bus, LoadController controller) {
		bus.register(this);
		return true;
	}
	@Subscribe
	public void preInit(FMLPreInitializationEvent ev) {
		
		
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			
			FMLCommonHandler.instance().getFMLLogger().warning("Bukkit For Vanilla is currently only a server-side mod.");
			return;
		}
		else if (FMLCommonHandler.instance().getEffectiveSide() == Side.BUKKIT) {
			FMLCommonHandler.instance().getFMLLogger().severe("The bukkit API as a forge mod on bukkit? *mind blown*");
			return;
		}
		System.out.println("[Bukkit API]: Initializing configuration...");
		myConfigurationFile = ev.getSuggestedConfigurationFile();
		Configuration config = new Configuration(myConfigurationFile);
		config.addCustomCategoryComment("consoleConfig", "Configuration for the server console");
		config.addCustomCategoryComment("dontTouchThis", "Things which are best left untouched");
		Property colour = config.get("consoleConfig", "enablecolour", true);
		colour.comment = "Enable coloured ANSI console output";
		this.allowAnsi = colour.getBoolean(true);
		Property plugins = config.get(Configuration.CATEGORY_GENERAL, "pluginDir", "plugins");
		plugins.comment = "The folder to look for plugins in.";
		this.pluginFolder = plugins.value;
		Property suuid = config.get("dontTouchThis", "serverUUID", this.genUUID());
		System.out.println("[Bukkit API]: Set UUID to " + suuid.value);
		suuid.comment = "The UUID of the server. Don't touch this or it might break your plugins.";
		this.serverUUID = suuid.value;
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
		// FIXME: For some reason this bugs forge...
		try {
			MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
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
		System.out.println("[Bukkit API]: Starting the API, implementing Bukkit API version " + BukkitServer.version);
		//bServer = new BukkitServer(ev.getServer(), ev.getServer().getConfigurationManager());
		
		Thread bukkitThread = new Thread(new ThreadGroup("Bukkit4Vanilla"), new BukkitStarter(), "BukkitCoreAPI-0");
		bukkitThread.setDaemon(true);
		bukkitThread.start();
		System.out.println("Hello!");
		//bServer.resetRecipes();
	}
}
