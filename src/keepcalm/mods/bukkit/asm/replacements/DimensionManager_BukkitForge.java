package keepcalm.mods.bukkit.asm.replacements;

import keepcalm.mods.bukkit.asm.asmext.AsmExtFieldAdd;
import keepcalm.mods.bukkit.asm.asmext.AsmExtMethodReplace;
import keepcalm.mods.bukkit.forgeHandler.DimensionManagerImpl;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.*;
import java.io.File;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/12/13
 * Time: 10:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class DimensionManager_BukkitForge {

    @AsmExtFieldAdd
    public static DimensionManagerImpl impl = new DimensionManagerImpl();

    @AsmExtMethodReplace
    public static boolean registerProviderType(int id, Class<? extends WorldProvider> provider, boolean keepLoaded)
    {
        return impl.registerProviderType(id, provider, keepLoaded);
    }

    @AsmExtMethodReplace
    public static void init()
    {
        impl.init();
    }

    @AsmExtMethodReplace
    public static void registerDimension(int id, int providerType)
    {
        impl.registerDimension(id, providerType);
    }

    @AsmExtMethodReplace
    public static void unregisterDimension(int id)
    {
        impl.unregisterDimension(id);
    }

    @AsmExtMethodReplace
    public static int getProviderType(int dim)
    {
        return impl.getProviderType(dim);
    }

    @AsmExtMethodReplace
    public static WorldProvider getProvider(int dim)
    {
        return impl.getProvider(dim);
    }

    @AsmExtMethodReplace
    public static Integer[] getIDs()
    {
        return impl.getIDs();
    }

    @AsmExtMethodReplace
    public static void setWorld(int id, WorldServer world)
    {
        impl.setWorld(id,world);
    }

    @AsmExtMethodReplace
    public static void initDimension(int dim) {
        impl.initDimension(dim);
    }

    @AsmExtMethodReplace
    public static WorldServer getWorld(int id)
    {
        return impl.getWorld(id);
    }

    @AsmExtMethodReplace
    public static WorldServer[] getWorlds()
    {
        return impl.getWorlds();
    }

    @AsmExtMethodReplace
    public static boolean shouldLoadSpawn(int dim)
    {
        return impl.shouldLoadSpawn(dim);
    }

    @AsmExtMethodReplace
    public static Integer[] getStaticDimensionIDs()
    {
        return impl.getStaticDimensionIDs();
    }

    @AsmExtMethodReplace
    public static WorldProvider createProviderFor(int dim)
    {
        return impl.createProviderFor(dim);
    }

    @AsmExtMethodReplace
    public static void unloadWorld(int id) {
        impl.unloadWorld(id);
    }

    @AsmExtMethodReplace
    public static void unloadWorlds(Hashtable<Integer, long[]> worldTickTimes)
    {
        impl.unloadWorlds(worldTickTimes);
    }

    @AsmExtMethodReplace
    public static int getNextFreeDimId()
    {
        return impl.getNextFreeDimId();
    }

    @AsmExtMethodReplace
    public static NBTTagCompound saveDimensionDataMap()
    {
        return impl.saveDimensionDataMap();
    }

    @AsmExtMethodReplace
    public static void loadDimensionDataMap(NBTTagCompound compoundTag)
    {
        impl.loadDimensionDataMap(compoundTag);
    }

    @AsmExtMethodReplace
    static File getCurrentSaveRootDirectory()
    {
        return impl.getCurrentSaveRootDirectory();
    }
}
