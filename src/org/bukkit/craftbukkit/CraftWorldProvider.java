package org.bukkit.craftbukkit;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.WorldProvider;
import org.bukkit.craftbukkit.generator.CustomChunkGenerator;
import org.bukkit.craftbukkit.generator.InternalChunkGenerator;
import org.bukkit.craftbukkit.generator.NormalChunkGenerator;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftWorldProvider extends WorldProvider
{
    String dimName;
    ChunkGenerator generator;

    @Override
    public String getDimensionName() {
        return (dimName != null)?dimName:getSaveFolder();
    }

    @Override
    public String getSaveFolder()    {
        //return getDimName();
        return dimName;
    }

    public String getDimName()
    {
        return "DIM_ZZZ_BF" + dimensionId;
    }

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

    @Override
    public net.minecraft.world.chunk.IChunkProvider createChunkGenerator()
    {
        if( generator != null )
        {
            if(generator instanceof CustomChunkGenerator )
            {
                return (CustomChunkGenerator)generator;
            }
            else if(generator instanceof NormalChunkGenerator )
            {
                return (NormalChunkGenerator)generator;
            }
            else
            {
                return new CustomChunkGenerator(worldObj, this.getSeed(), generator);
            }
        }
        return null;
    }



    @Override
    public void setDimension(int dim)
    {
        if( tempGens.containsKey(dim))
        {
            generator = tempGens.get(dim).get();
        }

        if( tempNames.containsKey(dim))
        {
            dimName = tempNames.get(dim);
        }

        super.setDimension(dim);
    }


    public static int ProviderID = 101;

    private static Map<Integer, WeakReference<ChunkGenerator>> tempGens = new HashMap<Integer, WeakReference<ChunkGenerator>>();
    private static Map<Integer, String> tempNames = new HashMap<Integer, String>();

    public List<BlockPopulator> getPopulators(CraftWorld world)
    {
        if( generator != null )
        {
            return (generator.getDefaultPopulators(world));
        }
        else
        {
            return null;
        }
    }

    public static void registerChunkGenerator( int dimension, ChunkGenerator gen )
    {
        tempGens.put(dimension, new WeakReference<ChunkGenerator>(gen));
    }

    public static void registerNameForDimension(int dimension, String name)
    {
        tempNames.put(dimension, name);
    }

}

