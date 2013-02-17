package org.bukkit.craftbukkit;

import keepcalm.mods.bukkit.forgeHandler.DimensionManagerImpl;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.*;

import net.minecraftforge.common.DimensionManager;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.*;

public class CraftDimensionManager
{
    protected static HashMap<Integer,Integer> registeredDimensions = new HashMap<Integer, Integer>();

    private static void saveDimensionMapping()
    {
        writeNBTToFile( new File(CraftServer.instance().getWorldContainer(), "bfdims.dat" ), nbt );
    }

    private static void loadDimensionMapping()
    {
        nbt = readNBTFromFile(new File(CraftServer.instance().getWorldContainer(), "bfdims.dat"));
    }

    private static String worldNameToDimensionKey( String name )
    {
        return "bukkit.world." + name.toLowerCase() + ".dimension";
    }

    public static boolean hasDimensionIdForName( String name )
    {
        if(nbt == null)
        {
            loadDimensionMapping();
        }

        String dk = worldNameToDimensionKey( name );
        return nbt.hasKey(dk);
    }

    public static boolean hasNameForDimensionId( int dim )
    {
        if(nbt == null)
        {
            loadDimensionMapping();
        }

        return nbt.hasKey(Integer.toString(dim));
    }

    public static int getDimensionIdForName( String name )
    {
        if(nbt == null)
        {
            loadDimensionMapping();
        }

        String dk = worldNameToDimensionKey( name );

        if( nbt.hasKey(dk) )
        {
            return nbt.getInteger(dk);
        }
        else
        {
            return 0; // Default to overworld
        }
    }

    public static void setDimensionIdForName( String name, int dimension )
    {
        if(nbt == null)
        {
            loadDimensionMapping();
        }

        String dk = worldNameToDimensionKey( name );
        nbt.setInteger(dk, dimension);
        nbt.setString(Integer.toString(dimension), dk);
        saveDimensionMapping();
    }

    private static NBTTagCompound nbt = null;

    private static void writeNBTToFile( File file, NBTTagCompound nbtcmp ) {
        try {
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            CompressedStreamTools.writeCompressed(nbtcmp, fileoutputstream);
            fileoutputstream.close();
        }
        catch(Exception exception) {
            exception.printStackTrace();
        }
    }

    private static NBTTagCompound readNBTFromFile( File file ) {

        if(!file.exists()) return new NBTTagCompound();

        try {
            FileInputStream fileinputstream = new FileInputStream(file);
            NBTTagCompound temp = CompressedStreamTools.readCompressed(fileinputstream);
            fileinputstream.close();
            return temp;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static int getNextDimensionId()
    {
        loadDimensionMapping();

        return DimensionManager.getNextFreeDimId();
    }

    private static Map<Integer, WeakReference<ChunkGenerator>> tempGens = new HashMap<Integer, WeakReference<ChunkGenerator>>();
    private static Map<Integer, String> tempNames = new HashMap<Integer, String>();

    public static void registerNameForDimension(int dimension, String name)
    {
        tempNames.put(dimension, name);
    }

    public static File getWorldFolder(String worldName) {
        return new File(CraftServer.instance().getWorldContainer(), worldName );
    }

    public static int[] getIDs() {
        loadDimensionMapping();

        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < 2000; i++)
        {
            if(nbt.hasKey(Integer.toString(i)))
            {
                list.add(i);
            }
        }

        return ArrayUtils.toPrimitive(list.toArray(new Integer[0]));
    }

    public static int getWorldProviderTypeForEnvironment( World.Environment env )
    {
        if( env == World.Environment.THE_END ) return 1;

        if( env == World.Environment.NETHER ) return -1;

        if( env == World.Environment.NORMAL ) return 0;

        return 0;
    }

    public static WorldServer createWorld(CraftServer craft, WorldCreator creator, File worldContainer, String name, Profiler theProfiler) {

        int dimension = -1000;

        int providerType = getWorldProviderTypeForEnvironment(creator.environment());

        if( CraftDimensionManager.hasDimensionIdForName( name ) )
        {
            dimension = CraftDimensionManager.getDimensionIdForName(name);
        }
        else
        {
            dimension = CraftDimensionManager.getNextDimensionId();
            CraftDimensionManager.setDimensionIdForName(name, dimension);
        }

        if(registeredDimensions.containsKey(dimension))
        {
            DimensionManagerImpl.getInstance().registerCraftDimension(dimension, providerType, creator);
            registeredDimensions.put(dimension, 0);
        }
        else
        {
            DimensionManagerImpl.getInstance().registerCraftDimension(dimension, creator);
        }
        DimensionManager.initDimension(dimension);

        WorldServer ws = DimensionManager.getWorld(dimension);
        ws.addWorldAccess( (IWorldAccess) new WorldManager(craft.getHandle(), ws));

        CraftWorld cw = new CraftWorld(ws);
        cw.getPopulators().addAll(creator.generator().getDefaultPopulators(cw));

        return ws;  //To change body of created methods use File | Settings | File Templates.
    }
}

