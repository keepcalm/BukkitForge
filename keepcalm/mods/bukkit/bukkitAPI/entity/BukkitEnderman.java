package keepcalm.mods.bukkit.bukkitAPI.entity;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import net.minecraft.src.EntityEnderman;

import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.material.MaterialData;
//import org.bukkit.craftbukkit.CraftServer;

public class BukkitEnderman extends BukkitMonster implements Enderman {
    public BukkitEnderman(BukkitServer server, EntityEnderman entity) {
        super(server, entity);
    }

    public MaterialData getCarriedMaterial() {
        return Material.getMaterial(getHandle().getCarried()).getNewData((byte) getHandle().getCarryingData());
    }

    public void setCarriedMaterial(MaterialData data) {
        getHandle().setCarried(data.getItemTypeId());
        getHandle().setCarryingData(data.getData());
    }

    @Override
    public EntityEnderman getHandle() {
        return (EntityEnderman) entity;
    }

    @Override
    public String toString() {
        return "BukkitEnderman";
    }

    public EntityType getType() {
        return EntityType.ENDERMAN;
    }
}
