package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicAddField;
import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import keepcalm.mods.bukkit.nmsforge.DimensionManagerImpl;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.*;

import java.io.File;
import java.util.*;

public class DimensionManager_BukkitForge {

    @AsmagicAddField
    public static DimensionManagerImpl impl = DimensionManagerImpl.getInstance();

    @AsmagicReplaceMethod
    public static boolean registerProviderType(int id, Class<? extends WorldProvider> provider, boolean keepLoaded)
    {
        return impl.registerProviderType(id, provider, keepLoaded);
    }

    @AsmagicReplaceMethod
    public static void init()
    {
        if( impl == null ) impl = DimensionManagerImpl.getInstance();
        impl.init();
    }

    @AsmagicReplaceMethod
    public static void registerDimension(int id, int providerType)
    {
        impl.registerDimension(id, providerType);
    }

    @AsmagicReplaceMethod
    public static void unregisterDimension(int id)
    {
        impl.unregisterDimension(id);
    }

    @AsmagicReplaceMethod
    public static int getProviderType(int dim)
    {
        return impl.getProviderType(dim);
    }

    @AsmagicReplaceMethod
    public static WorldProvider getProvider(int dim)
    {
        return impl.getProvider(dim);
    }

    @AsmagicReplaceMethod
    public static Integer[] getIDs()
    {
        return impl.getIDs();
    }

    @AsmagicReplaceMethod
    public static void setWorld(int id, WorldServer world)
    {
        impl.setWorld(id,world);
    }

    @AsmagicReplaceMethod
    public static void initDimension(int dim) {
        impl.initDimension(dim);
    }

    @AsmagicReplaceMethod
    public static WorldServer getWorld(int id)
    {
        return impl.getWorld(id);
    }

    @AsmagicReplaceMethod
    public static WorldServer[] getWorlds()
    {
        return impl.getWorlds();
    }

    @AsmagicReplaceMethod
    public static boolean shouldLoadSpawn(int dim)
    {
        return impl.shouldLoadSpawn(dim);
    }

    @AsmagicReplaceMethod
    public static Integer[] getStaticDimensionIDs()
    {
        return impl.getStaticDimensionIDs();
    }

    @AsmagicReplaceMethod
    public static WorldProvider createProviderFor(int dim)
    {
        return impl.createProviderFor(dim);
    }

    @AsmagicReplaceMethod
    public static void unloadWorld(int id) {
        impl.unloadWorld(id);
    }

    @AsmagicReplaceMethod
    public static void unloadWorlds(Hashtable<Integer, long[]> worldTickTimes)
    {
        impl.unloadWorlds(worldTickTimes);
    }

    @AsmagicReplaceMethod
    public static int getNextFreeDimId()
    {
        return impl.getNextFreeDimId();
    }

    @AsmagicReplaceMethod
    public static NBTTagCompound saveDimensionDataMap()
    {
        return impl.saveDimensionDataMap();
    }

    @AsmagicReplaceMethod
    public static void loadDimensionDataMap(NBTTagCompound compoundTag)
    {
        impl.loadDimensionDataMap(compoundTag);
    }

    @AsmagicReplaceMethod
    public static File getCurrentSaveRootDirectory()
    {
        return impl.getCurrentSaveRootDirectory();
    }
}
