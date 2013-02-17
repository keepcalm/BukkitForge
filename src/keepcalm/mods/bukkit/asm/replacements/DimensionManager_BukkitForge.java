package keepcalm.mods.bukkit.asm.replacements;

import keepcalm.mods.bukkit.asm.asmagic.AsmagicFieldAdd;
import keepcalm.mods.bukkit.asm.asmagic.AsmagicMethodReplace;
import keepcalm.mods.bukkit.forgeHandler.DimensionManagerImpl;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.*;

import java.io.File;
import java.util.*;

public class DimensionManager_BukkitForge {

    @AsmagicFieldAdd
    public static DimensionManagerImpl impl = new DimensionManagerImpl();

    @AsmagicMethodReplace
    public static boolean registerProviderType(int id, Class<? extends WorldProvider> provider, boolean keepLoaded)
    {
        return impl.registerProviderType(id, provider, keepLoaded);
    }

    @AsmagicMethodReplace
    public static void init()
    {
        impl.init();
    }

    @AsmagicMethodReplace
    public static void registerDimension(int id, int providerType)
    {
        impl.registerDimension(id, providerType);
    }

    @AsmagicMethodReplace
    public static void unregisterDimension(int id)
    {
        impl.unregisterDimension(id);
    }

    @AsmagicMethodReplace
    public static int getProviderType(int dim)
    {
        return impl.getProviderType(dim);
    }

    @AsmagicMethodReplace
    public static WorldProvider getProvider(int dim)
    {
        return impl.getProvider(dim);
    }

    @AsmagicMethodReplace
    public static Integer[] getIDs()
    {
        return impl.getIDs();
    }

    @AsmagicMethodReplace
    public static void setWorld(int id, WorldServer world)
    {
        impl.setWorld(id,world);
    }

    @AsmagicMethodReplace
    public static void initDimension(int dim) {
        impl.initDimension(dim);
    }

    @AsmagicMethodReplace
    public static WorldServer getWorld(int id)
    {
        return impl.getWorld(id);
    }

    @AsmagicMethodReplace
    public static WorldServer[] getWorlds()
    {
        return impl.getWorlds();
    }

    @AsmagicMethodReplace
    public static boolean shouldLoadSpawn(int dim)
    {
        return impl.shouldLoadSpawn(dim);
    }

    @AsmagicMethodReplace
    public static Integer[] getStaticDimensionIDs()
    {
        return impl.getStaticDimensionIDs();
    }

    @AsmagicMethodReplace
    public static WorldProvider createProviderFor(int dim)
    {
        return impl.createProviderFor(dim);
    }

    @AsmagicMethodReplace
    public static void unloadWorld(int id) {
        impl.unloadWorld(id);
    }

    @AsmagicMethodReplace
    public static void unloadWorlds(Hashtable<Integer, long[]> worldTickTimes)
    {
        impl.unloadWorlds(worldTickTimes);
    }

    @AsmagicMethodReplace
    public static int getNextFreeDimId()
    {
        return impl.getNextFreeDimId();
    }

    @AsmagicMethodReplace
    public static NBTTagCompound saveDimensionDataMap()
    {
        return impl.saveDimensionDataMap();
    }

    @AsmagicMethodReplace
    public static void loadDimensionDataMap(NBTTagCompound compoundTag)
    {
        impl.loadDimensionDataMap(compoundTag);
    }

    @AsmagicMethodReplace
    public static File getCurrentSaveRootDirectory()
    {
        return impl.getCurrentSaveRootDirectory();
    }
}
