package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityCreeper;
import net.minecraft.src.NBTTagCompound;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreeperPowerEvent;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitCreeper extends BukkitMonster implements Creeper {

    public BukkitCreeper(BukkitServer server, EntityCreeper entity) {
        super(server, entity);
    }

    public boolean isPowered() {
        return getHandle().getPowered();
    }

    public void setPowered(boolean powered) {
        BukkitServer server = this.server;
        Creeper entity = (Creeper) this;

        if (powered) {
            CreeperPowerEvent event = new CreeperPowerEvent(entity, CreeperPowerEvent.PowerCause.SET_ON);
            server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                NBTTagCompound creep = new NBTTagCompound();
                getHandle().writeEntityToNBT(creep);
                creep.setBoolean("powered", true);
                getHandle().readEntityFromNBT(creep);
            }
        } else {
            CreeperPowerEvent event = new CreeperPowerEvent(entity, CreeperPowerEvent.PowerCause.SET_OFF);
            server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
            	NBTTagCompound creep = new NBTTagCompound();
                getHandle().writeEntityToNBT(creep);
                creep.setBoolean("powered", false);
                getHandle().readEntityFromNBT(creep);
            }
        }
    }

    @Override
    public EntityCreeper getHandle() {
        return (EntityCreeper) entity;
    }

    @Override
    public String toString() {
        return "BukkitCreeper";
    }

    public EntityType getType() {
        return EntityType.CREEPER;
    }
}
