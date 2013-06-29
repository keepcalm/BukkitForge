package org.bukkit.craftbukkit.v1_5_R3.entity;

import net.minecraft.entity.monster.EntitySkeleton;

import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftSkeleton extends CraftMonster implements Skeleton {

    public CraftSkeleton(CraftServer server, EntitySkeleton entity) {
        super(server, entity);
    }

    @Override
    public EntitySkeleton getHandle() {
        return (EntitySkeleton) entity;
    }

    @Override
    public String toString() {
        return "CraftSkeleton";
    }

    public EntityType getType() {
        return EntityType.SKELETON;
    }

	@Override
	public SkeletonType getSkeletonType() {
		return SkeletonType.getType(getHandle().getSkeletonType());
	}

	@Override
	public void setSkeletonType(SkeletonType type) {
		getHandle().setSkeletonType(type.getId());
	}
}
