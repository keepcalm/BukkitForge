package keepcalm.mods.bukkit.bukkitAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.command.BukkitCommandMap;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitEntity;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import keepcalm.mods.bukkit.bukkitAPI.generator.NormalChunkGenerator;
import keepcalm.mods.bukkit.bukkitAPI.help.CommandHelpTopic;
import keepcalm.mods.bukkit.bukkitAPI.help.SimpleHelpMap;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitInventoryCustom;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitItemFactory;
import keepcalm.mods.bukkit.bukkitAPI.map.BukkitMapView;
import keepcalm.mods.bukkit.bukkitAPI.metadata.EntityMetadataStore;
import keepcalm.mods.bukkit.bukkitAPI.metadata.PlayerMetadataStore;
import keepcalm.mods.bukkit.bukkitAPI.metadata.WorldMetadataStore;
import keepcalm.mods.bukkit.bukkitAPI.scheduler.B4VScheduler;
import keepcalm.mods.bukkit.forgeHandler.ForgeEventHandler;
import keepcalm.mods.bukkit.forgeHandler.PlayerTracker;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.server.ConvertingProgressUpdate;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.server.management.BanEntry;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.EnumGameType;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeVersion;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Warning;
import org.bukkit.Warning.WarningState;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitWorker;
import org.bukkit.util.permissions.DefaultPermissions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.MarkedYAMLException;

import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.avaje.ebean.config.dbplatform.SQLitePlatform;
import com.avaje.ebeaninternal.server.lib.sql.TransactionIsolation;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
//import cpw.mods.fml.common.FMLCommonHandler;
//import jline.console.ConsoleReader;
import cpw.mods.fml.relauncher.RelaunchClassLoader;


public class BukkitServer implements Server {
	public static final String apiVer = "1.4.5-R0.2";
	public static final String version = "1.4.6";
	private static BukkitServer instance;
	private MinecraftServer theServer;
	//private BukkitServer bukkitServer;
	private ServerConfigurationManager configMan;
	private YamlConfiguration bukkitConfig;
	private Yaml yaml = new Yaml(new SafeConstructor());
	private B4VScheduler scheduler = new B4VScheduler();
	private Logger theLogger;
	private ServicesManager servicesManager = new SimpleServicesManager();

	private BukkitCommandMap commandMap = new BukkitCommandMap(this);
	private PluginManager pluginManager;// = new SimplePluginManager(this, commandMap);


	private BukkitClassLoader thePluginLoader;// = new BukkitClassLoader(((URLClassLoader) getClass().getClassLoader()).getURLs(), getClass().getClassLoader());
	//private BukkitScheduler scheduler = new BukkitScheduler();
	//	private ServicesManager servicesManager = new SimpleServicesManager();
	public Map<Integer,BukkitWorld> worlds = new LinkedHashMap<Integer,BukkitWorld>();
	private Map<String, OfflinePlayer> offlinePlayers = new HashMap<String, OfflinePlayer>();
	private StandardMessenger theMessenger;
	private SimpleHelpMap theHelpMap = new SimpleHelpMap(this);
	private final BukkitConsoleCommandSender console;// = (BukkitConsoleCommandSender) BukkitConsoleCommandSender.getInstance();
	private int monsterSpawn;
	private int animalSpawn;
	private int waterAnimalSpawn;

	private WarningState warningState;
	private EntityMetadataStore entityMetadata;
	private WorldMetadataStore worldMetadata;
	private PlayerMetadataStore playerMetadata;
	private static String cbBuild;
	private static Map<String,Boolean> fauxSleeping = new HashMap();

	/*public void setServer(MinecraftServer server) {
		if (server == null) {
			throw new RuntimeException("Server must be set before continuing!");
		}
		if (!server.isDedicatedServer()) {
			theLogger.warning("Not for use in singleplayer! Giving up...");
			return;
		}
		configMan = server.getConfigurationManager();
		theServer = (DedicatedServer) server;
	}*/


	public BukkitServer(MinecraftServer server) {
		this.instance = this;
		cbBuild = "git-BukkitForge-1.4.5-R1.0-b" + BukkitContainer.CRAFT_BUILD_NUMBER +  "jnks (Really: BukkitForge for MC " + version + ")";
		configMan = server.getConfigurationManager();
		theServer = server;
		List<Integer> ids = Arrays.asList(DimensionManager.getIDs());
		Iterator<Integer> _ = ids.iterator();


		thePluginLoader = new BukkitClassLoader(getClass().getClassLoader());
		try {
			System.out.println("This is a test of the SPM Loader!");
			// this *should* load simplepluginamanger via BukkitClassLoader
			Class<?> pluginMan = thePluginLoader.loadClass("org.bukkit.plugin.SimplePluginManager");
			System.out.println("Loaded class: " + pluginMan.getCanonicalName() + " via " + pluginMan.getClassLoader().getClass().getCanonicalName());
			Method insn = pluginMan.getMethod("newInstance");
			insn.setAccessible(true);
			this.pluginManager = (PluginManager) insn.invoke(null);


		} catch (Exception e1) {
			throw new RuntimeException("BukkitForge encountered an error (most likely it  was installed incorrectly!)", e1);
		}
		
		//pluginManager = new SimplePluginManager(this, commandMap);
		bukkitConfig = new YamlConfiguration();
		YamlConfiguration yml = new YamlConfiguration();
		try {
			yml.load(getClass().getClassLoader().getResourceAsStream("configurations/bukkit.yml"));
			if (!new File("bukkit.yml").exists()) {
				new File("bukkit.yml").createNewFile();
				yml.save("bukkit.yml");
			}
			bukkitConfig.load("bukkit.yml");
			bukkitConfig.addDefaults(yml);
			bukkitConfig.save("bukkit.yml");

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		while(_.hasNext()) {
			int i = _.next();
			WorldServer x = theServer.worldServerForDimension(i);
			worlds.put(i, new BukkitWorld(x, this.getGenerator(x.getWorldInfo().getWorldName()), this.wtToEnv(x)));
		}
		this.theLogger = BukkitContainer.bukkitLogger;
		theLogger.info("Bukkit API for Vanilla, version " + apiVer + " starting up...");
		Bukkit.setServer(this);
		this.theHelpMap = new SimpleHelpMap(this);
		this.theMessenger = new StandardMessenger();
		//theLogger.info("Testing the bukkit Logger!");
		this.entityMetadata = new EntityMetadataStore();
		this.playerMetadata = new PlayerMetadataStore();
		this.worldMetadata = new WorldMetadataStore();
		this.warningState = Warning.WarningState.DEFAULT;
		this.console = (BukkitConsoleCommandSender) BukkitConsoleCommandSender.getInstance();
		// wait until server start

		/*try {
			Thread.currentThread().wait();
		} catch (InterruptedException e) {
			theLogger.log(Level.FINE, "The server was interrupted, it might explode!", e);
		}*/
		theLogger.info("Completing load...");
		// fix for the 'mod recipes disappear' bug
		BukkitModRecipeHelper.saveCraftingManagerRecipes();
		//configMan = theServer.getConfigurationManager();
		//theServer = (DedicatedServer) server;
		HelpTopic myHelp = new CommandHelpTopic("bexec", "Run a command forcibly bukkit aliases", "", "");
		Bukkit.getServer().getHelpMap().addTopic(myHelp);
		loadPlugins();
		enablePlugins(PluginLoadOrder.STARTUP);

		theLogger.info("Loading PostWorld plugins...");
		enablePlugins(PluginLoadOrder.POSTWORLD);
		theLogger.info("Loaded plugins: ");
		for (Plugin i : pluginManager.getPlugins()) {
			theLogger.info(i.getName() + "- Enabled: " +  i.isEnabled());
		}
		ForgeEventHandler.ready = true;
		commandMap.doneLoadingPlugins((ServerCommandManager) theServer.getCommandManager());
		if (!theServer.isDedicatedServer()) {
			EntityPlayer par0 = theServer.getConfigurationManager().getPlayerForUsername(theServer.getServerOwner());
			if (par0 != null) {
				par0.sendChatToPlayer(ChatColor.GREEN + "BukkitForge has finished loading! You may now enjoy a (relatively) lag-free game!");
				theServer.getCommandManager().executeCommand(par0, "/plugins");
				(new PlayerTracker()).onPlayerLogin(par0);
			}

		}

	}

	private Environment wtToEnv(WorldServer x) {

		IChunkProvider wp = x.theChunkProviderServer.currentChunkProvider;

		if (wp instanceof ChunkProviderEnd) {
			return Environment.THE_END;
		}
		else if (wp instanceof ChunkProviderHell) {
			return Environment.NETHER;
		}
		else {
			return Environment.NORMAL;
		}

	}

	public MinecraftServer getHandle() {
		return this.theServer;
	}
	@Override
	public void sendPluginMessage(Plugin source, String channel, byte[] message) {
		StandardMessenger.validatePluginMessage(getMessenger(), source, channel, message);
		for (Player player : getOnlinePlayers()) {
			player.sendPluginMessage(source, channel, message);
		}

	}

	@Override
	public Set<String> getListeningPluginChannels() {

		Set<String> result = new HashSet<String>();

		for (Player player : getOnlinePlayers()) {
			result.addAll(player.getListeningPluginChannels());
		}

		return result;

	}
	public EntityMetadataStore getEntityMetadata() {
		return entityMetadata;
	}

	public PlayerMetadataStore getPlayerMetadata() {
		return playerMetadata;
	}

	public WorldMetadataStore getWorldMetadata() {
		return worldMetadata;
	}
	@Override
	public String getName() {

		return String.format("Minecraft Server %s, using FML %s, MinecraftForge %s, %s", new Object[] {Loader.instance().getMinecraftModContainer().getVersion(), Loader.instance().getFMLVersionString(), ForgeVersion.getVersion(), Loader.instance().getMCPVersionString()});
	}

	@Override
	public String getVersion() {
		// towny fix?
		return cbBuild;
	}

	@Override
	public String getBukkitVersion() {

		return this.apiVer;
	}

	@Override
	public Player[] getOnlinePlayers() {
		List<Player> players= new ArrayList<Player>();
		for (Object i : theServer.getConfigurationManager().playerEntityList) {
			players.add(BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) i));
		}
		return players.toArray(new Player[0]);
	}

	@Override
	public int getMaxPlayers() {

		return configMan.getMaxPlayers();
	}

	@Override
	public int getPort() {

		return theServer.getServerPort();
	}

	@Override
	public int getViewDistance() {

		return configMan.getViewDistance();
	}

	@Override
	public String getIp() {

		return theServer.getHostname();
	}

	@Override
	public String getServerName() {

		return theServer.getServerModName();
	}

	@Override
	public String getServerId() {
		return BukkitContainer.serverUUID;
	}

	@Override
	public String getWorldType() {

		return theServer.worldServerForDimension(0).getProviderName();
	}

	@Override
	public boolean getGenerateStructures() {

		return theServer.canStructuresSpawn();
	}

	@Override
	public boolean getAllowEnd() {
		// hardcoded?
		return true;
	}

	@Override
	public boolean getAllowNether() {

		return theServer.getAllowNether();
	}

	@Override
	public boolean hasWhitelist() {

		return configMan.isWhiteListEnabled();
	}

	@Override
	public void setWhitelist(boolean value) {
		configMan.setWhiteListEnabled(value);

	}

	@Override
	public Set<OfflinePlayer> getWhitelistedPlayers() {
		Set<OfflinePlayer> ret = new HashSet<OfflinePlayer>();
		// strings
		for (Object i : configMan.getWhiteListedPlayers()) {
			String name = (String) i;
			ret.add(new BukkitOfflinePlayer(this, name));
		}

		//Set<OfflinePlayer> d = (Set<OfflinePlayer>) configMan.getWhiteListedPlayers();
		//Set<OfflinePlayer> j = (Set<OfflinePlayer>) new ArrayList<OfflinePlayer>();

		return ret;
	}

	@Override
	public void reloadWhitelist() {
		configMan.loadWhiteList();

	}

	@Override
	public int broadcastMessage(String message) {
		return broadcast(message, BROADCAST_CHANNEL_USERS);
		//return 0;
	}

	@Override
	public String getUpdateFolder() {
		return "plugins/updates";

	}

	@Override
	public File getUpdateFolderFile() {

		return new File("plugins/updates");
	}

	@Override
	public long getConnectionThrottle() {

		return this.bukkitConfig.getInt("settings.connection-throttle");
	}

	@Override
	public int getTicksPerAnimalSpawns() {

		return this.bukkitConfig.getInt("ticks-per.animal-spawns");
	}

	@Override
	public int getTicksPerMonsterSpawns() {

		return this.bukkitConfig.getInt("ticks-per.animal-spawn");
	}

	@Override
	public Player getPlayer(String name) {
		Player j;
		for (Object i : configMan.playerEntityList) {
			EntityPlayer guy = (EntityPlayer) i;
			if (guy.username.toLowerCase().startsWith(name)) {
				return (Player) BukkitEntity.getEntity(this, guy);
			}
		}
		//return (Player) configMan.getPlayerForUsername(name);
		return null;
	}

	@Override
	public Player getPlayerExact(String name) {

		OfflinePlayer player = new BukkitOfflinePlayer(this, name);
		if (player.isOnline()) {
			return player.getPlayer();
		}
		else {
			return null;
		}

	}

	@Override
	public List<Player> matchPlayer(String partialName) {
		List<Player> matchedPlayers = new ArrayList<Player>();

		for (Player iterPlayer : this.getOnlinePlayers()) {
			String iterPlayerName = iterPlayer.getName();

			if (partialName.equalsIgnoreCase(iterPlayerName)) {
				// Exact match
				matchedPlayers.clear();
				matchedPlayers.add(iterPlayer);
				break;
			}
			if (iterPlayerName.toLowerCase().contains(partialName.toLowerCase())) {
				// Partial match
				matchedPlayers.add(iterPlayer);
			}
		}

		return matchedPlayers;
		//return null;
	}

	@Override
	public PluginManager getPluginManager() {
		return this.pluginManager;
	}

	@Override
	public BukkitScheduler getScheduler() {
		return scheduler;
	}

	@Override
	public ServicesManager getServicesManager() {

		return servicesManager;
	}

	@Override
	public List<World> getWorlds() {

		return new ArrayList<World>(worlds.values());
	}

	@Override
	public World createWorld(WorldCreator creator) {
		if (creator == null) {
			throw new IllegalArgumentException("Creator may not be null");
		}

		String name = creator.name();
		ChunkGenerator generator = creator.generator();
		File folder = new File(getWorldContainer().getParentFile(), name);
		int dimension = DimensionManager.getNextFreeDimId();
		World world = getWorld(dimension);
		WorldType type = WorldType.parseWorldType(creator.type().getName());
		boolean generateStructures = creator.generateStructures();

		if (world != null) {
			return world;
		}

		if ((folder.exists()) && (!folder.isDirectory())) {
			throw new IllegalArgumentException("File exists with the name '" + name + "' and isn't a folder");
		}

		if (generator == null) {
			generator = getGenerator(name);
		}

		AnvilSaveConverter converter = new AnvilSaveConverter(getWorldContainer());
		if (converter.isOldMapFormat(name)) {
			getLogger().info("Converting world '" + name + "'");
			converter.convertMapFormat(name, new ConvertingProgressUpdate(theServer));
		}


		boolean hardcore = false;

		WorldServer internal = new WorldServer(theServer, new AnvilSaveHandler(getWorldContainer().getParentFile(), name, true), name, dimension, new WorldSettings(creator.seed(), EnumGameType.getByID(getDefaultGameMode().getValue()), generateStructures, hardcore, type), theServer.theProfiler);

		if (!(worlds.containsKey(dimension))) {
			return null;
		}

		//internal.getWorldInfo().get = console.worldServerForDimension(0).worldMaps;

		//internal. = new EntityTracker(internal); // CraftBukkit
		internal.addWorldAccess((IWorldAccess) new WorldManager(theServer, internal));
		internal.difficultySetting = 1;
		//internal.(true, true);
		//theServer.worldServers[theServer.worldServers.length] = internal;
		DimensionManager.setWorld(dimension, internal);
		this.worlds.put(dimension, new BukkitWorld(internal, creator.generator(), creator.environment()));
		if (generator != null) {
			(worlds.get(dimension)).getPopulators().addAll(generator.getDefaultPopulators(worlds.get(dimension)));
		}

		pluginManager.callEvent(new WorldInitEvent((worlds.get(dimension))));
		System.out.print("Preparing start region for level " + (theServer.worldServers.length - 1) + " (Seed: " + internal.getSeed() + ")");

		if (DimensionManager.shouldLoadSpawn(dimension)) {
			short short1 = 196;
			long i = System.currentTimeMillis();
			for (int j = -short1; j <= short1; j += 16) {
				for (int k = -short1; k <= short1; k += 16) {
					long l = System.currentTimeMillis();

					if (l < i) {
						i = l;
					}

					if (l > i + 1000L) {
						int i1 = (short1 * 2 + 1) * (short1 * 2 + 1);
						int j1 = (j + short1) * (short1 * 2 + 1) + k + 1;

						System.out.println("Preparing spawn area for " + name + ", " + (j1 * 100 / i1) + "%");
								i = l;
					}

					ChunkCoordinates chunkcoordinates = internal.getSpawnPoint();
					internal.theChunkProviderServer.provideChunk(chunkcoordinates.posX + j >> 4, chunkcoordinates.posZ + k >> 4);

					//while (internal.updateLights()) {
					//  ;
				}//
			}
		}
		pluginManager.callEvent( new WorldLoadEvent(worlds.get(dimension)));
		return worlds.get(dimension);
	}

	private ChunkGenerator getGenerator(String world) {
		ConfigurationSection section = bukkitConfig.getConfigurationSection("worlds");
		ChunkGenerator result = null;

		if (section != null) {
			section = section.getConfigurationSection(world);

			if (section != null) {
				String name = section.getString("generator");

				if ((name != null) && (!name.equals(""))) {
					String[] split = name.split(":", 2);
					String id = (split.length > 1) ? split[1] : null;
					Plugin plugin = pluginManager.getPlugin(split[0]);

					if (plugin == null) {
						getLogger().severe("Could not set generator for default world '" + world + "': Plugin '" + split[0] + "' does not exist");
					} else if (!plugin.isEnabled()) {
						getLogger().severe("Could not set generator for default world '" + world + "': Plugin '" + split[0] + "' is not enabled yet (is it load:STARTUP?)");
					} else {
						result = plugin.getDefaultWorldGenerator(world, id);
					}
				}
			}
		}

		return result;
	}

	@Override
	public boolean unloadWorld(String name, boolean save) {

		return unloadWorld(getWorld(name), save);
	}

	@Override
	public boolean unloadWorld(World world, boolean save) {
		WorldServer handle = ((BukkitWorld) world).getHandle();
		WorldUnloadEvent ev = new WorldUnloadEvent(world);
		getPluginManager().callEvent(ev);
		if (ev.isCancelled())
			return false; // cancelled
		DimensionManager.unloadWorld(handle.getWorldInfo().getDimension());
		return true;

	}

	@Override
	public World getWorld(String name) {
		String[] parts = name.split("@");
		if (parts.length == 1) {
			theLogger.warning("A plugin is trying to access a world without specifying its name correctly! You need to remove configs from build 44 and older");
			if (BukkitContainer.DEBUG) {
				System.out.println("here is a stack trace:");
				try {
					throw new Exception("potato");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		try {
			int dim = Integer.parseInt(parts[1]);
			return getWorld(dim);
		}
		catch (NumberFormatException e) {
			theLogger.warning("Apparently " + parts[1] + " isn't an integer! Interesting!");
			return getWorld(0);
		}/*
		for (WorldServer w : theServer.worldServers) {
			if (w.getWorldInfo().getWorldName().equals(name)) {
				return this.getWorld(w.getWorldInfo().getDimension());
			}
		}
		 */
		//return null;
	}

	public World getWorld(int dimID) {
		if (worlds.containsKey(dimID))
			return worlds.get(dimID);
		else if (!worlds.containsKey(dimID) && Arrays.asList(DimensionManager.getIDs()).contains(dimID)) {
			// dim there but not registered with us.
			WorldServer internal = DimensionManager.getWorld(dimID);
			int dim = internal.getWorldInfo().getDimension();
				System.out.println("Registering dimension with BukkitForge: " + dim + "..." );
				WorldProvider w = internal.provider;
				
				Environment env = w.isHellWorld ? Environment.NETHER : Environment.NORMAL;
				ChunkGenerator cg = new NormalChunkGenerator(internal);//(((WorldServer)ev.world).theChunkProviderServer);
				BukkitWorld bukkit = new BukkitWorld(internal, cg, env);
				BukkitServer.instance().worlds.put(dim, bukkit);
				return bukkit;
			
		}
		return null;
	}

	@Override
	public World getWorld(UUID uid) {
		for (WorldServer w : theServer.worldServers) {
			//return null;
			UUID wUUID = new UUID(w.getSeed(), w.getWorldInfo().getDimension());
			if (wUUID == uid) {
				return worlds.get(w.getWorldInfo().getDimension());
			}
			//if (w.getWorldInfo().)
		}
		return null;
	}

	/**
	 * @author keepcalm
	 * 
	 * WARNING: This assumes overworld. Use getMap(id, world) to do other worlds.
	 * @param id - the id of the map to get
	 * 
	 * @return the map of that ID in dimension 0
	 */
	@Override
	public MapView getMap(short id) {
		return this.getMap(id, this.getWorld(0));
	}

	/**
	 * @author keepcalm
	 * 
	 * @param id - the id of the map
	 * @param world - the world of the map
	 * 
	 * @return the map of that ID in that world.
	 */
	private MapView getMap(short id, World world) {
		MapData d = ItemMap.getMPMapData(id, ((BukkitWorld) world).getHandle());
		return new BukkitMapView(d);
	}

	@Override
	public MapView createMap(World world) {
		net.minecraft.item.ItemStack stack = new net.minecraft.item.ItemStack(Item.map, 1, -1);


		MapData worldmap = Item.map.getMPMapData((short) stack.getItemDamage(), ((BukkitWorld)world).getHandle());
		return (MapView) worldmap;
	}

	@Override
	public void reload() {
		bukkitConfig = YamlConfiguration.loadConfiguration(new File("bukkit.yml"));
		//

		if (theServer instanceof DedicatedServer) {
			PropertyManager config = new PropertyManager(theServer.getFile("server.properties"));
			((DedicatedServer) theServer).settings = config;
		}
		
		//

		boolean animals = theServer.getCanSpawnAnimals();
		boolean monsters = theServer.worldServerForDimension(0).difficultySetting > 0;
		int difficulty = theServer.worldServerForDimension(0).difficultySetting;

		//theServer.pro
		/*theServer.setOnlineMode(theServer.isServerInOnlineMode());
		theServer.setCanSpawnAnimals(config.getBooleanProperty("spawn-animals", theServer.getCanSpawnAnimals()));
		theServer.setAllowPvp(config.getBooleanProperty("pvp", theServer.isPVPEnabled()));
		theServer.setAllowFlight(config.getBooleanProperty("allow-flight", theServer.isFlightAllowed()));
		theServer.setMOTD(config.getProperty("motd", theServer.getMOTD()));*/
		monsterSpawn = bukkitConfig.getInt("spawn-limits.monsters");
		animalSpawn = bukkitConfig.getInt("spawn-limits.animals");
		waterAnimalSpawn = bukkitConfig.getInt("spawn-limits.water-animals");
		warningState = WarningState.value(bukkitConfig.getString("settings.deprecated-verbose"));
		// = bukkitConfig.getInt("ticks-per.autosave");

		for (WorldServer world : theServer.worldServers) {
			world.difficultySetting = difficulty;
			world.spawnHostileMobs = monsters;
			world.spawnPeacefulMobs = animals;
			/*if (this.getTicksPerAnimalSpawns() < 0) {
	                world.ticksPerAnimalSpawns = 400;
	            } else {
	                world.ticksPerAnimalSpawns = this.getTicksPerAnimalSpawns();
	            }

	            if (this.getTicksPerMonsterSpawns() < 0) {
	                world.ticksPerMonsterSpawns = 1;
	            } else {
	                world.ticksPerMonsterSpawns = this.getTicksPerMonsterSpawns();
	            }*/
		}

		pluginManager.clearPlugins();
		commandMap.clearCommands();
		resetRecipes();

		int pollCount = 0;

		// Wait for at most 2.5 seconds for plugins to close their threads
		while (pollCount < 50 && getScheduler().getActiveWorkers().size() > 0) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			pollCount++;
		}

		List<BukkitWorker> overdueWorkers = getScheduler().getActiveWorkers();
		for (BukkitWorker worker : overdueWorkers) {
			Plugin plugin = worker.getOwner();
			String author = "<NoAuthorGiven>";
			if (plugin.getDescription().getAuthors().size() > 0) {
				author = plugin.getDescription().getAuthors().get(0);
			}
			getLogger().log(Level.SEVERE, String.format(
					"Nag author: '%s' of '%s' about the following: %s",
					author,
					plugin.getDescription().getName(),
					"This plugin is not properly shutting down its async tasks when it is being reloaded.  This may cause conflicts with the newly loaded version of the plugin"
					));
		}
		loadPlugins();
		enablePlugins(PluginLoadOrder.STARTUP);
		enablePlugins(PluginLoadOrder.POSTWORLD);

	}
	/**
	 * @author CraftBukkit
	 */
	public void loadPlugins() {
		pluginManager.registerInterface(JavaPluginLoader.class);

		File pluginFolder = theServer.getFile("plugins");

		if (pluginFolder.exists()) {
			this.theLogger.info("Plugins are being loaded...");
			Plugin[] plugins = pluginManager.loadPlugins(pluginFolder);
			for (Plugin plugin : plugins) {
				try {
					String message = String.format("Loading %s", plugin.getDescription().getFullName());
					plugin.getLogger().info(message);
					plugin.onLoad();
				} catch (Throwable ex) {
					Logger.getLogger(BukkitServer.class.getName()).log(Level.SEVERE, ex.getMessage() + " initializing " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
				}
			}
		} else {
			theLogger.info("Plugin folder doesn't exist: " + pluginFolder.getAbsolutePath());
			pluginFolder.mkdir();
		}
	}
	/**
	 * @author CraftBukkit
	 */
	public void enablePlugins(PluginLoadOrder type) {
		if (type == PluginLoadOrder.STARTUP) {
			theHelpMap.clear();
			theHelpMap.initializeGeneralTopics();
		}

		Plugin[] plugins = pluginManager.getPlugins();

		for (Plugin plugin : plugins) {
			if ((!plugin.isEnabled()) && (plugin.getDescription().getLoad() == type)) {
				loadPlugin(plugin);
			}
		}

		if (type == PluginLoadOrder.POSTWORLD) {
			//this.theAliasMap.registerServerAliases();
			loadCustomPermissions();
			DefaultPermissions.registerCorePermissions();
			//theHelpMap.initializeCommands();
		}
	}
	/*
	 * @author CraftBukkit
	 */
	private void loadPlugin(Plugin plugin) {
		try {
			pluginManager.enablePlugin(plugin);

			List<Permission> perms = plugin.getDescription().getPermissions();

			for (Permission perm : perms) {
				try {
					pluginManager.addPermission(perm);
				} catch (IllegalArgumentException ex) {
					getLogger().log(Level.WARNING, "Plugin " + plugin.getDescription().getFullName() + " tried to register permission '" + perm.getName() + "' but it's already registered", ex);
				}
			}
		} catch (Throwable ex) {
			Logger.getLogger(BukkitServer.class.getName()).log(Level.SEVERE, ex.getMessage() + " loading " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
		}
	}
	@SuppressWarnings("finally")
	public void loadCustomPermissions() {
		theLogger.info("Going to load perms file: " + bukkitConfig.getString("settings.permissions-file", "permissions.yml"));
		File file = new File(bukkitConfig.getString("settings.permissions-file", "permissions.yml"));
		FileInputStream stream;

		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException ex) {
			try {
				file.createNewFile();
			} finally {
				return;
			}
		}

		Map<String, Map<String, Object>> perms;

		try {
			perms = (Map<String, Map<String, Object>>) yaml.load(stream);
		} catch (MarkedYAMLException ex) {
			getLogger().log(Level.WARNING, "Server permissions file " + file + " is not valid YAML: " + ex.toString());
			return;
		} catch (Throwable ex) {
			getLogger().log(Level.WARNING, "Server permissions file " + file + " is not valid YAML.", ex);
			return;
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {}
		}

		if (perms == null) {
			getLogger().log(Level.INFO, "Server permissions file " + file + " is empty, ignoring it");
			return;
		}

		List<Permission> permsList = Permission.loadPermissions(perms, "Permission node '%s' in " + file + " is invalid", Permission.DEFAULT_PERMISSION);

		for (Permission perm : permsList) {
			try {
				pluginManager.addPermission(perm);
			} catch (IllegalArgumentException ex) {
				getLogger().log(Level.SEVERE, "Permission in " + file + " was already defined", ex);
			}
		}
	}
	@Override
	public Logger getLogger() {

		return this.theLogger;
	}

	@Override
	public PluginCommand getPluginCommand(String name) {
		Command command = commandMap.getCommand(name);

		if (command instanceof PluginCommand) {
			return (PluginCommand) command;
		} else {
			return null;
		}
	}

	@Override
	public void savePlayers() {
		configMan.saveAllPlayerData();

	}

	@Override
	public boolean dispatchCommand(CommandSender sender, String commandLine)
			throws CommandException {
		if (sender instanceof BukkitConsoleCommandSender) {
			theServer.executeCommand(commandLine);
		}

		else {
			ICommandSender ics = ((BukkitPlayer) sender).getHandle();
			theServer.getCommandManager().executeCommand(ics, commandLine);
		}

		return true;
	}

	@Override
	public void configureDbConfig(ServerConfig config) {
		DataSourceConfig ds = new DataSourceConfig();
		ds.setDriver(bukkitConfig.getString("database.driver"));
		ds.setUrl(bukkitConfig.getString("database.url"));
		ds.setUsername(bukkitConfig.getString("database.username"));
		ds.setPassword(bukkitConfig.getString("database.password"));
		ds.setIsolationLevel(TransactionIsolation.getLevel(bukkitConfig.getString("database.isolation")));

		if (ds.getDriver().contains("sqlite")) {
			config.setDatabasePlatform(new SQLitePlatform());
			config.getDatabasePlatform().getDbDdlSyntax().setIdentity("");
		}
		else if (ds.getDriver().contains("mysql")) {
			theLogger.warning("MySQL is presently unsupported for BukkitForge");
		}

		config.setDataSourceConfig(ds);

	}

	@Override
	public boolean addRecipe(Recipe recipe) {
		GameRegistry.addRecipe((IRecipe) recipe);
		return true;
	}

	@Override
	public List<Recipe> getRecipesFor(ItemStack result) {
		//net.minecraft.src.ItemStack realStack = (net.minecraft.src.ItemStack) result;
		List<Recipe> res = new ArrayList<Recipe> ();
		net.minecraft.item.ItemStack stack = new net.minecraft.item.ItemStack(result.getTypeId(), result.getAmount(), result.getDurability());
		ShapedRecipes[] recipes = (ShapedRecipes[]) CraftingManager.getInstance().getRecipeList().toArray();
		for (ShapedRecipes i : recipes) {
			if (i.getRecipeOutput() == stack) {
				res.add((Recipe) i);
			}
		}
		return res;
	}

	@Override
	public Iterator<Recipe> recipeIterator() {

		return CraftingManager.getInstance().getRecipeList().iterator();
	}

	@Override
	@SuppressWarnings("all")
	public void clearRecipes() {
		CraftingManager.getInstance().recipes = new ArrayList();

	}

	@Override
	public void resetRecipes() {
		CraftingManager.instance = BukkitModRecipeHelper.getOriginalCraftingManager();

	}

	@Override
	public Map<String, String[]> getCommandAliases() {

		ConfigurationSection section = bukkitConfig.getConfigurationSection("aliases");
		Map<String, String[]> result = new LinkedHashMap<String, String[]>();

		if (section != null) {
			for (String key : section.getKeys(false)) {
				List<String> commands = null;

				if (section.isList(key)) {
					commands = section.getStringList(key);
				} else {
					commands = ImmutableList.<String>of(section.getString(key));
				}

				result.put(key, commands.toArray(new String[commands.size()]));
			}
		}

		return result;
	}

	@Override
	public int getSpawnRadius() {

		return theServer.getSpawnProtectionSize();
	}

	@Override
	public void setSpawnRadius(int value) {
		if (theServer instanceof DedicatedServer) {
			((DedicatedServer) theServer).settings.setProperty("spawn-radius", value);
		}
		else {
			theLogger.info("Can't set spawn protection radius on client!");
		}

	}

	@Override
	public boolean getOnlineMode() {

		return theServer.isServerInOnlineMode();
	}

	@Override
	public boolean getAllowFlight() {

		return theServer.isFlightAllowed();
	}

	@Override
	public boolean useExactLoginLocation() {

		return false;
	}

	@Override
	public void shutdown() {
		theLogger.info("Stopping the Bukkit API " + version);
		int pollCount = 0;
		while (pollCount < 50 && getScheduler().getActiveWorkers().size() > 0) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
			pollCount++;
		}

		List<BukkitWorker> overdueWorkers = getScheduler().getActiveWorkers();
		for (BukkitWorker worker : overdueWorkers) {
			Plugin plugin = worker.getOwner();
			String author = "<NoAuthorGiven>";
			if (plugin.getDescription().getAuthors().size() > 0) {
				author = plugin.getDescription().getAuthors().get(0);
			}
			getLogger().log(Level.SEVERE, String.format(
					"Nag author: '%s' of '%s' about the following: %s",
					author,
					plugin.getDescription().getName(),
					"This plugin is not properly shutting down its async tasks when it is being reloaded.  This may cause conflicts with the newly loaded version of the plugin"
					));
		}
		//theServer.stopServer();

	}

	@Override
	public int broadcast(String message, String permission) {
		int count = 0;
		Set<Permissible> permissibles = getPluginManager().getPermissionSubscriptions(permission);

		for (Permissible permissible : permissibles) {
			if (permissible instanceof CommandSender && permissible.hasPermission(permission)) {
				CommandSender user = (CommandSender) permissible;
				user.sendMessage(message);
				count++;
			}
		}

		return count;
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String name) {

		//EntityPlayerMP guy = new EntityPlayerMP();
		OfflinePlayer result = getPlayerExact(name);
		String lname = name.toLowerCase();

		if (result == null) {
			result = offlinePlayers.get(lname);

			if (result == null) {
				result = new BukkitOfflinePlayer(this, name);
				offlinePlayers.put(lname, result);
			}
		} else {
			offlinePlayers.remove(lname);
		}
		return result;

	}

	@Override
	public Set<String> getIPBans() {

		return theServer.getConfigurationManager().getBannedIPs().getBannedList().entrySet();
	}

	@Override
	public void banIP(String address) {
		BanEntry entry = new BanEntry(address);
		configMan.getBannedIPs().put(entry);

	}

	@Override
	public void unbanIP(String address) {
		configMan.getBannedIPs().remove(address);

	}

	@Override
	public Set<OfflinePlayer> getBannedPlayers() {
		Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();
		Set<String> banned = configMan.getBannedPlayers().getBannedList().entrySet();
		for (String guy : banned) {
			players.add(this.getOfflinePlayer(guy));
		}
		return players;
	}

	@Override
	public Set<OfflinePlayer> getOperators() {
		Set<OfflinePlayer> ops = new HashSet<OfflinePlayer>();
		Set<String> opers = configMan.getOps();
		for (String guy : opers) {
			ops.add(this.getOfflinePlayer(guy));
		}
		return ops;
	}

	@Override
	public GameMode getDefaultGameMode() {

		return GameMode.getByValue(theServer.getGameType().getID());
	}

	@Override
	public void setDefaultGameMode(GameMode mode) {
		theServer.setGameType(EnumGameType.getByID(mode.getValue()));

	}

	@Override
	public ConsoleCommandSender getConsoleSender() {
		return this.console;
	}

	@Override
	public File getWorldContainer() {
		return theServer.getFile(theServer.worldServerForDimension(0).getWorldInfo().getWorldName());
	}

	@Override
	public OfflinePlayer[] getOfflinePlayers() {


		String[] files = new File(this.getWorldContainer(), "/players").list(new keepcalm.mods.bukkit.utils.DatFileFilter());
		Set<OfflinePlayer> players = new HashSet<OfflinePlayer>();

		for (String file : files) {
			players.add(getOfflinePlayer(file.substring(0, file.length() - 4)));
		}
		players.addAll(Arrays.asList(getOnlinePlayers()));

		return players.toArray(new OfflinePlayer[players.size()]);
	}

	@Override
	public Messenger getMessenger() {

		return this.theMessenger;
	}

	@Override
	public HelpMap getHelpMap() {

		return this.theHelpMap;
	}

	@Override
	public Inventory createInventory(InventoryHolder owner, InventoryType type) {

		return new BukkitInventoryCustom(owner, type);
	}

	@Override
	public Inventory createInventory(InventoryHolder owner, int size) {
		Validate.isTrue(size % 9 ==0, "The size of a chest must be divisible by 9!");
		return new BukkitInventoryCustom(owner, size);
	}

	@Override
	public Inventory createInventory(InventoryHolder owner, int size,
			String title) {
		Validate.isTrue(size % 9 ==0, "The size of a chest must be divisible by 9!");
		return new BukkitInventoryCustom(owner, size, title);
	}

	@Override
	public int getMonsterSpawnLimit() {

		return monsterSpawn;
	}

	@Override
	public int getAnimalSpawnLimit() {

		return animalSpawn;
	}

	@Override
	public int getWaterAnimalSpawnLimit() {

		return waterAnimalSpawn;
	}

	@Override
	public boolean isPrimaryThread() {

		return false ; /* since we're a mod, i doubt that we are the main thread */
	}

	@Override
	public String getMotd() {

		return theServer.getMOTD();
	}

	@Override
	public WarningState getWarningState() {

		return this.warningState;//FMLCommonHandler.instance().getFMLLogger().getLevel();
	}

	public SimpleCommandMap getCommandMap() {
		return this.commandMap;
	}
	public BukkitCommandMap getRealCmdMap() {
		return this.commandMap;
	}
	public static BukkitServer instance() {
		return instance;

	}
	public static void setPlayerFauxSleeping(String username, boolean b) {
		fauxSleeping.put(username, b);

	}
	public boolean isFauxSleeping(String username) {
		return this.fauxSleeping.containsKey(username) ? this.fauxSleeping.get(username) : false;
	}
	@Override
	public boolean isHardcore() {
		return theServer.isHardcore();
	}
	@Override
	public int getAmbientSpawnLimit() {
		return -1;
	}

	@Override
	public String getShutdownMessage() {
		return "The server is shutting down";
	}

	@Override
	public ItemFactory getItemFactory() {
		return BukkitItemFactory.instance();
	}


}
