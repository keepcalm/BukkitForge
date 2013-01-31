package org.bukkit.craftbukkit;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.WorldProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class CraftWorldProvider extends WorldProvider
{
    String dimName;

    public void setName( String name )
    {
        dimName = name;
    }

    @Override
    public String getDimensionName() {
        // TODO: Fix this to get stored dimension name
        return (dimName != null)?dimName:getSaveFolder();
    }

    @Override
    public String getSaveFolder()    {
        return getDimName();
    }

    public String getDimName()
    {
        return "DIM_BF" + dimensionId;
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

    public static int ProviderID = 101;
}

