package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityTrackerEntry;
import net.minecraft.world.WorldServer;
import net.minecraft.world.World;

public class EntityTracker_BukkitForge extends EntityTracker {

    public EntityTracker_BukkitForge(WorldServer par1WorldServer) {
        super(par1WorldServer);
    }

    private int entityViewDistance;
    private World theWorld;

    @AsmagicReplaceMethod
    public void addEntityToTracker(Entity par1Entity, int par2, int par3, boolean par4)
    {
        if (par2 > entityViewDistance)
        {
            par2 = entityViewDistance;
        }

        if (trackedEntityIDs.containsItem(par1Entity.entityId))
        {
            System.out.println("Entity is already tracked! -- Logged instead of exception");
            //throw new IllegalStateException("Entity is already tracked!");
        }

        EntityTrackerEntry var5 = new EntityTrackerEntry(par1Entity, par2, par3, par4);
        trackedEntities.add(var5);
        trackedEntityIDs.addKey(par1Entity.entityId, var5);
        var5.sendEventsToPlayers(theWorld.playerEntities);
    }
}
