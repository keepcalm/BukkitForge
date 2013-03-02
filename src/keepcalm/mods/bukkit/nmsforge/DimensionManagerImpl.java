package keepcalm.mods.bukkit.nmsforge;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import keepcalm.mods.bukkit.CraftWorldProvider;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.Hashtable;

public class DimensionManagerImpl {

    private Hashtable<Integer, Class<? extends WorldProvider>> providers = new Hashtable();
    private Hashtable<Integer, Boolean> spawnSettings = new Hashtable();
    private Hashtable<Integer, WorldServer> worlds = new Hashtable();
    private boolean hasInit = false;
    private Hashtable<Integer, Integer> dimensions = new Hashtable();
    private Map<World, ListMultimap<ChunkCoordIntPair, String>> persistentChunkStore = Maps.newHashMap();
    private ArrayList<Integer> unloadQueue = new ArrayList();
    private BitSet dimensionMap = new BitSet(1024);

    private Hashtable<Integer, WorldCreator> bukkitDims = new Hashtable<Integer, WorldCreator>();

    protected static DimensionManagerImpl impl = null;

    public static DimensionManagerImpl getInstance() {
        if( impl == null ) {
            impl = new DimensionManagerImpl();
            impl.init();
        }
        return impl;
    }

    public boolean registerProviderType(int id, Class<? extends WorldProvider> provider, boolean keepLoaded)
    {
        if (providers.containsKey(Integer.valueOf(id)))
        {
            return false;
        }
        providers.put(Integer.valueOf(id), provider);
        spawnSettings.put(Integer.valueOf(id), Boolean.valueOf(keepLoaded));
        return true;
    }

    public DimensionManagerImpl()
    {
    }

    public void init()
    {
        if (hasInit)
        {
            return;
        }

        hasInit = true;

        registerProviderType(0, WorldProviderSurface.class, true);
        registerProviderType(-1, WorldProviderHell.class, true);
        registerProviderType(1, WorldProviderEnd.class, false);
        registerDimension(0, 0);
        registerDimension(-1, -1);
        registerDimension(1, 1);
    }

    public void registerDimension(int id, int providerType)
    {
        if (!providers.containsKey(Integer.valueOf(providerType)))
        {
            throw new IllegalArgumentException(String.format("Failed to register dimension for id %d, provider type %d does not exist", new Object[] { Integer.valueOf(id), Integer.valueOf(providerType) }));
        }
        if (dimensions.containsKey(Integer.valueOf(id)))
        {
            throw new IllegalArgumentException(String.format("Failed to register dimension for id %d, One is already registered", new Object[] { Integer.valueOf(id) }));
        }
        dimensions.put(Integer.valueOf(id), Integer.valueOf(providerType));
        if (id >= 0)
        {
            dimensionMap.set(id);
        }
    }

    public void registerCraftDimension(int id, int baseProviderType, WorldCreator creator )
    {
        registerDimension(id, baseProviderType);
        bukkitDims.put(id, creator);
    }

    public boolean isCraftWorld(int id)
    {
        return bukkitDims.containsKey(id);
    }

    public void unregisterDimension(int id)
    {
        if (!dimensions.containsKey(Integer.valueOf(id)))
        {
            throw new IllegalArgumentException(String.format("Failed to unregister dimension for id %d; No provider registered", new Object[] { Integer.valueOf(id) }));
        }
        dimensions.remove(Integer.valueOf(id));
    }

    public int getProviderType(int dim)
    {
        if (!dimensions.containsKey(Integer.valueOf(dim)))
        {
            throw new IllegalArgumentException(String.format("Could not get provider type for dimension %d, does not exist", new Object[] { Integer.valueOf(dim) }));
        }
        return ((Integer)dimensions.get(Integer.valueOf(dim))).intValue();
    }

    public WorldProvider getProvider(int dim)
    {
        return getWorld(dim).provider;
    }

    public Integer[] getIDs()
    {
        return (Integer[])worlds.keySet().toArray(new Integer[worlds.size()]);
    }

    public void setWorld(int id, WorldServer world)
    {
        if (world != null) {
            worlds.put(Integer.valueOf(id), world);
            MinecraftServer.getServer().worldTickTimes.put(Integer.valueOf(id), new long[100]);
            FMLLog.info("Loading dimension %d (%s) (%s)", new Object[]{Integer.valueOf(id), world.getWorldInfo().getWorldName(), world.getMinecraftServer()});
        } else {
            worlds.remove(Integer.valueOf(id));
            MinecraftServer.getServer().worldTickTimes.remove(Integer.valueOf(id));
            FMLLog.info("Unloading dimension %d", new Object[] { Integer.valueOf(id) });
        }

        ArrayList tmp = new ArrayList();
        if (worlds.get(Integer.valueOf(0)) != null)
            tmp.add(worlds.get(Integer.valueOf(0)));
        if (worlds.get(Integer.valueOf(-1)) != null)
            tmp.add(worlds.get(Integer.valueOf(-1)));
        if (worlds.get(Integer.valueOf(1)) != null) {
            tmp.add(worlds.get(Integer.valueOf(1)));
        }
        for (Map.Entry entry : worlds.entrySet())
        {
            int dim = ((Integer)entry.getKey()).intValue();
            if ((dim < -1) || (dim > 1))
            {
                tmp.add(entry.getValue());
            }
        }
        MinecraftServer.getServer().worldServers = ((WorldServer[])tmp.toArray(new WorldServer[tmp.size()]));
    }

    public void initDimension(int dim) {
        WorldServer overworld = getWorld(0);
        if (overworld == null)
            throw new RuntimeException("Cannot Hotload Dim: Overworld is not Loaded!");
        try
        {
            getProviderType(dim);
        } catch (Exception e) {
            System.err.println("Cannot Hotload Dim: " + e.getMessage());
            return;
        }
        MinecraftServer mcServer = overworld.getMinecraftServer();
        ISaveHandler savehandler = overworld.getSaveHandler();
        WorldSettings worldSettings = new WorldSettings(overworld.getWorldInfo());

        WorldServer world = dim == 0 ? overworld : new WorldServerMulti(mcServer, savehandler, overworld.getWorldInfo().getWorldName(), dim, worldSettings, overworld, mcServer.theProfiler);
        world.addWorldAccess(new WorldManager(mcServer, world));

        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
        if (!mcServer.isSinglePlayer())
        {
            world.getWorldInfo().setGameType(mcServer.getGameType());
        }

        mcServer.setDifficultyForAllWorlds(mcServer.getDifficulty());

        if( !bukkitDims.containsKey(dim) )
        {
            Bukkit.getServer().getPluginManager().callEvent(new WorldLoadEvent(CraftServer.instance().getWorld(world.provider.dimensionId)));
        }
    }

    public WorldServer getWorld(int id)
    {
        return (WorldServer)worlds.get(Integer.valueOf(id));
    }

    public WorldServer[] getWorlds()
    {
        return (WorldServer[])worlds.values().toArray(new WorldServer[worlds.size()]);
    }

    public boolean shouldLoadSpawn(int dim)
    {
        int id = getProviderType(dim);
        return (spawnSettings.containsKey(Integer.valueOf(id))) && (((Boolean)spawnSettings.get(Integer.valueOf(id))).booleanValue());
    }

    public Integer[] getStaticDimensionIDs()
    {
        return (Integer[])dimensions.keySet().toArray(new Integer[dimensions.keySet().size()]);
    }

    public WorldProvider createProviderFor(int dim)
    {
        try {
            if (dimensions.containsKey(Integer.valueOf(dim)))
            {
                WorldProvider provider = (WorldProvider)((Class)providers.get(Integer.valueOf(getProviderType(dim)))).newInstance();

                if( bukkitDims.containsKey(dim) )
                {
                    CraftWorldProvider cwp = new CraftWorldProvider(provider, bukkitDims.get(dim));
                    cwp.setDimensionName( bukkitDims.get(dim).name() );
                    provider = cwp;
                }

                provider.setDimension(dim);

                return provider;
            }

            throw new RuntimeException(String.format("No WorldProvider bound for dimension %d", new Object[] { Integer.valueOf(dim) }));
        }
        catch (Exception e)
        {
            FMLCommonHandler.instance().getFMLLogger().log(Level.SEVERE, String.format("An error occured trying to create an instance of WorldProvider %d (%s)", new Object[] { Integer.valueOf(dim), ((Class)providers.get(Integer.valueOf(getProviderType(dim)))).getSimpleName() }), e);

            throw new RuntimeException(e);
        }
    }

    public void unloadWorld(int id) {
        unloadQueue.add(Integer.valueOf(id));
    }

    public void unloadWorlds(Hashtable<Integer, long[]> worldTickTimes) {
        for (int id : unloadQueue) {
            WorldServer w = worlds.get(id);
            try {
                if (w != null) {
                    w.saveAllChunks(true, null);
                } else {
                    FMLLog.warning("Unexpected world unload - world %d is already unloaded", id);
                }
            } catch (Exception e) {
                FMLLog.log(Level.SEVERE, e, "Exception saving chunks when unloading world " + w);
            } finally {
                if (w != null) {
                    try {
                        MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(w));
                    } catch (Throwable t) {
                        FMLLog.severe("A mod failed to handle a world unload", t);
                    }
                    w.flush();
                    setWorld(id, null);
                }
            }
        }
        unloadQueue.clear();
    }

    public int getNextFreeDimId()
    {
        int next = 0;
        while (true)
        {
            next = dimensionMap.nextClearBit(next);
            if (!dimensions.containsKey(Integer.valueOf(next)))
                break;
            dimensionMap.set(next);
        }

        return next;
    }

    public NBTTagCompound saveDimensionDataMap()
    {
        int[] data = new int[(dimensionMap.length() + 32 - 1) / 32];
        NBTTagCompound dimMap = new NBTTagCompound();
        for (int i = 0; i < data.length; i++)
        {
            int val = 0;
            for (int j = 0; j < 32; j++)
            {
                val |= (dimensionMap.get(i * 32 + j) ? 1 << j : 0);
            }
            data[i] = val;
        }
        dimMap.setIntArray("DimensionArray", data);
        return dimMap;
    }

    public void loadDimensionDataMap(NBTTagCompound compoundTag)
    {
        if (compoundTag == null)
        {
            dimensionMap.clear();
            for (Integer id : dimensions.keySet())
            {
                if (id.intValue() >= 0)
                {
                    dimensionMap.set(id.intValue());
                }
            }
        }
        else
        {
            int[] intArray = compoundTag.getIntArray("DimensionArray");
            for (int i = 0; i < intArray.length; i++)
            {
                for (int j = 0; j < 32; j++)
                {
                    dimensionMap.set(i * 32 + j, (intArray[i] & 1 << j) != 0);
                }
            }
        }
    }

    public File getCurrentSaveRootDirectory()
    {
        if (getWorld(0) != null)
        {
            return ((SaveHandler)getWorld(0).getSaveHandler()).getSaveDirectory();
        }

        return null;
    }

    public void registerCraftDimension(int dimension, WorldCreator creator) {
        bukkitDims.put(dimension, creator);
    }
}
