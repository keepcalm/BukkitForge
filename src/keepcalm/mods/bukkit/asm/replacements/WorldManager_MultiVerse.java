package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import org.bukkit.WorldType;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;

import java.io.File;

public class WorldManager_MultiVerse {

    @AsmagicReplaceMethod
    private boolean doLoad(String name) {

        CraftWorld cw = CraftServer.instance().worlds.get(name);

        if(cw==null) return false;

        if( !(new File(CraftServer.instance().getWorldContainer(), cw.getHandle().provider.getSaveFolder()).exists()))
        {
            return false;
        }

        return doLoad(name, true, null);
    }

    private boolean doLoad(String name, boolean ignoreExists, WorldType type) {
        return false;
    }
}
