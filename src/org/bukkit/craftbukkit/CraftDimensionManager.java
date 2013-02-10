package org.bukkit.craftbukkit;

import keepcalm.mods.bukkit.BukkitContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.*;

import net.minecraft.world.chunk.storage.AnvilSaveHandler;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.generator.CustomChunkGenerator;
import org.bukkit.craftbukkit.generator.InternalChunkGenerator;
import org.bukkit.craftbukkit.generator.NormalChunkGenerator;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import net.minecraft.world.chunk.IChunkProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.*;

public class CraftDimensionManager
{
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

        for(int i = DimensionOffset; i < DimensionOffset + 1000; i++)
        {
            if(nbt.hasKey(Integer.toString(i)))
            {
                nbt.setString(Integer.toString(i), "");
                return i;
            }
        }

        return DimensionOffset;
    }

    public static int DimensionOffset = 1000;

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
        for(int i = DimensionOffset; i < DimensionOffset + 1000; i++)
        {
            if(nbt.hasKey(Integer.toString(i)))
            {
                list.add(i);
            }
        }

        return ArrayUtils.toPrimitive( list.toArray(new Integer[0]) );
    }

    public static WorldServer createWorld(CraftServer craft, WorldCreator creator, File worldContainer, String name, int dimension, Profiler theProfiler) {

        DimensionManager.registerDimension(dimension, 0);

        WorldServer ws = new CraftWorldServer( craft.getHandle(), new AnvilSaveHandler(craft.getWorldContainer(), name, false), name, dimension, theProfiler, creator, craft.worlds.get(0).getHandle());
        ws.addWorldAccess( (IWorldAccess) new WorldManager(craft.getHandle(), ws));

        CraftWorld cw = new CraftWorld(ws);
        cw.getPopulators().addAll(creator.generator().getDefaultPopulators(cw));

        return ws;  //To change body of created methods use File | Settings | File Templates.
    }
}

