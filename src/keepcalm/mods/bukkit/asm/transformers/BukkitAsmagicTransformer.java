package keepcalm.mods.bukkit.asm.transformers;

import cpw.mods.fml.relauncher.IClassTransformer;
import com.eoware.asm.asmagic.AsmagicClassTransformer;
import keepcalm.mods.bukkit.asm.replacements.CommandHandler_BukkitForge;
import keepcalm.mods.bukkit.asm.replacements.DimensionManager_BukkitForge;
import keepcalm.mods.bukkit.asm.replacements.EntityTracker_BukkitForge;
import keepcalm.mods.bukkit.asm.replacements.WorldManager_MultiVerse;

import java.util.HashMap;

public class BukkitAsmagicTransformer implements IClassTransformer {

    static protected HashMap<String,String> classes = createClassesToTransform();

    static protected HashMap<String,String> createClassesToTransform() {
        HashMap<String,String> classes = new HashMap<String,String>();

        addClassNameAndAlias(classes, "net.minecraft.common.CommandHandler", "x", CommandHandler_BukkitForge.class);
        addClassNameAndAlias(classes, "net.minecraftforge.common.DimensionManager", null, DimensionManager_BukkitForge.class);
        addClassNameAndAlias(classes, "com.onarandombox.MultiverseCore.utils.WorldManager", null, WorldManager_MultiVerse.class);
        addClassNameAndAlias(classes, "net.minecraft.entity.EntityTracker", "ii", EntityTracker_BukkitForge.class);


        return classes;
    }

    static protected void addClassNameAndAlias( HashMap<String,String> map, String className, String obfName, Class clss )
    {
        map.put(className, clss.getName());
        if( obfName != null )
        {
            map.put(obfName, clss.getName());
        }
    }

    static AsmagicClassTransformer act = new AsmagicClassTransformer( classes );

    @Override
    public byte[] transform(String s, byte[] bytes) {

        if( s.contains("Asmagic") ) return bytes;

        if( !classes.containsKey(s) ) return bytes;

        System.out.println( "Transforming " + s + " using " + classes.get(s));
        return act.transform(s, bytes);
    }
}
