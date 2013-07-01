package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

public class CraftCreature extends CraftLivingEntity implements Creature {
    public CraftCreature(CraftServer server, EntityCreature entity) {
        super(server, entity);
    }

    public void setTarget(LivingEntity target) {
        EntityCreature entity = getHandle();
        if (target == null) {
            entity.setTarget(null);
        } else if (target instanceof CraftLivingEntity) {
            entity.setTarget(((CraftLivingEntity) target).getHandle());
            entity.setPathToEntity(entity.worldObj.getPathEntityToEntity(entity, entity.getAttackTarget(), 16.0F, true, false, false, true));
        }
    }

    public CraftLivingEntity getTarget() {
        if (getHandle().getAttackTarget() == null) return null;
        if (!(getHandle().getAttackTarget() instanceof EntityLiving)) return null;

        return (CraftLivingEntity) CraftEntity.getEntity(this.server, getHandle().getAttackTarget());
    }

    @Override
    public EntityCreature getHandle() {
        return (EntityCreature) entity;
    }

    @Override
    public String toString() {
        return "CraftCreature";
    }
}
