package org.bukkit.craftbukkit.v1_5_R3.entity;

import java.util.Set;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
//import net.minecraft.src.EntityDragon;
//import org.bukkit.craftbukkit.v1_5_R3.CraftServer;

public class CraftEnderDragon extends CraftComplexLivingEntity implements EnderDragon {
    public CraftEnderDragon(CraftServer server, EntityDragon entity) {
        super(server, entity);
    }

    public Set<ComplexEntityPart> getParts() {
        Builder<ComplexEntityPart> builder = ImmutableSet.builder();

        for (net.minecraft.entity.Entity p : getHandle().getParts()) {
        	EntityDragonPart part = (EntityDragonPart) p;
            builder.add((ComplexEntityPart) CraftEnderDragon.getEntity(this.server ,part));
        }

        return builder.build();
    }

    @Override
    public EntityDragon getHandle() {
        return (EntityDragon) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderDragon";
    }

    public EntityType getType() {
        return EntityType.ENDER_DRAGON;
    }
}
