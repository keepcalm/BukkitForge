package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.util.IntHashMap;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class EntityTracker_BukkitForge {

    private final WorldServer a = null;
    private Set b = new HashSet();
    private IntHashMap c = new IntHashMap();
    private int d;

    @AsmagicReplaceMethod
    public void a(Entity par1Entity, int par2, int par3, boolean par4)
    {
        if (par2 > d)
        {
            par2 = d;
        }

        if (c.containsItem(par1Entity.entityId))
        {
            System.out.println("Entity is already tracked! -- Logged instead of exception");
            //throw new IllegalStateException("Entity is already tracked!");
        }

        EntityTrackerEntry var5 = new EntityTrackerEntry(par1Entity, par2, par3, par4);
        b.add(var5);
        c.addKey(par1Entity.entityId, var5);
        var5.sendEventsToPlayers(a.playerEntities);
    }
}
