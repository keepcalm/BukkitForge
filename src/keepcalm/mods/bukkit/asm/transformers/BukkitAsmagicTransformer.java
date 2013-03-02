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

        addClassNameAndAlias(classes, "net.minecraft.common.CommandHandler", "x", CommandHandler_BukkitForge.class);
        addClassNameAndAlias(classes, "net.minecraftforge.common.DimensionManager", null, DimensionManager_BukkitForge.class);
        addClassNameAndAlias(classes, "net.minecraft.server.management.ServerConfigurationManager", "gm", ServerConfigurationManager_BukkitForge.class);
        //addClassNameAndAlias(classes, "cpw.mods.fml.common.network.FMLNetworkHandler", null, FMLNetworkHandler_BukkitForge.class);
        //addClassNameAndAlias(classes, "cpw.mods.fml.common.network.NetworkRegistry", null, NetworkRegistry_BukkitForge.class);
        addClassNameAndAlias(classes, "net.minecraft.network.NetServerHandler", "iv", NetServerHandler_BukkitForge.class);
        addClassNameAndAlias(classes, "net.minecraft.entity.EntityTracker", "ii", EntityTracker_BukkitForge.class);

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
        //repl.put( "iv", "keepcalm/mods/bukkit/CraftNetServerHandler" );
        act = new AsmagicClassTransformer(classes, repl);
    }

    @Override
    public byte[] transform(String s, byte[] bytes) {

        if( s.contains("Asmagic") ) return bytes;

        if( !classes.containsKey(s) ) return bytes;

        System.out.println( "Transforming " + s + " using " + classes.get(s));
        byte[] newClass = act.transform(s, bytes);

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
