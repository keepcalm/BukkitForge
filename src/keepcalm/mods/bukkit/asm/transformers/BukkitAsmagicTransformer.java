package keepcalm.mods.bukkit.asm.transformers;

import cpw.mods.fml.relauncher.IClassTransformer;
import com.eoware.asm.asmagic.AsmagicClassTransformer;
import keepcalm.mods.bukkit.asm.replacements.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BukkitAsmagicTransformer implements IClassTransformer {

    protected HashMap<String,String> classes = null;

    protected HashMap<String,String> createClassesToTransform() {
        HashMap<String,String> classes = new HashMap<String,String>();

        repl = new HashMap<String, String>();

        //addClassNameAndAlias(classes, "net.minecraft.command.CommandHandler", "y", CommandHandler_BukkitForge.class);
        addClassNameAndAlias(classes, "net.minecraftforge.common.DimensionManager", null, DimensionManager_BukkitForge.class);
        //addClassNameAndAlias(classes, "net.minecraft.server.management.ServerConfigurationManager", "gu", ServerConfigurationManager_BukkitForge.class);
        //addClassNameAndAlias(classes, "cpw.mods.fml.common.network.FMLNetworkHandler", null, FMLNetworkHandler_BukkitForge.class);
        //addClassNameAndAlias(classes, "cpw.mods.fml.common.network.NetworkRegistry", null, NetworkRegistry_BukkitForge.class);
        addClassNameAndAlias(classes, "net.minecraft.network.NetServerHandler", "jh", NetServerHandler_BukkitForge.class);
        addClassNameAndAlias(classes, "net.minecraft.entity.EntityTracker", "it", EntityTracker_BukkitForge.class);
        addClassNameAndAlias(classes, "net.minecraft.world.WorldServer", "iz", WorldServer_BukkitForge.class);
        addClassNameAndAlias(classes, "net.minecraft.world.Explosion", "zw", Explosion_BukkitForge.class);
        
        return classes;
    }

    protected HashMap<String,String> repl = null;

    protected void addClassNameAndAlias( HashMap<String,String> map, String className, String obfName, Class clss )
    {
        if( obfName != null )
        {
            map.put(obfName, clss.getName());
            //repl.put(clss.getCanonicalName().replace(".", "/"), obfName.replace(".", "/"));
        }
        else
        {
            map.put(className, clss.getName());
            //repl.put(clss.getCanonicalName().replace(".", "/"), className.replace( ".", "/"));
        }
    }

    AsmagicClassTransformer act = null;

    public BukkitAsmagicTransformer()
    {
        classes = createClassesToTransform();
        System.out.println("Got this Far!");
        //repl.put( "iv", "keepcalm/mods/bukkit/CraftNetServerHandler" );
        act = new AsmagicClassTransformer(classes, new HashMap<String, String>(), new HashMap<String, String>());
    }

    @Override
    public byte[] transform(String s, String transformedName, byte[] bytes) {

        if( s.contains("Asmagic") ) return bytes;

        if( !classes.containsKey(s) ) return bytes;

        System.out.println( "Transforming " + s + " using " + classes.get(s) + " !test!");
        byte[] newClass = act.transform(s, bytes);

        if( bytes.length == newClass.length )
        {
            System.out.println( "Transforming " + s + " using " + classes.get(s) + " -- Size unchanged, likely not changed" );
        }
        return newClass;
    }

    public static void writeByteArrayToFile( String strFilePath, byte[] bytes ) {

        try
        {
            FileOutputStream fos = new FileOutputStream(strFilePath);
            fos.write(bytes);
            fos.close();
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("FileNotFoundException : " + ex);
        }
        catch(IOException ioe)
        {
            System.out.println("IOException : " + ioe);
        }
    }
}
