package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.passive.EntityWolf;

import org.bukkit.DyeColor;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;
//import org.bukkit.craftbukkit.CraftServer;

public class CraftWolf extends CraftTameableAnimal implements Wolf {
    public CraftWolf(CraftServer server, EntityWolf wolf) {
        super(server, wolf);
    }

    public boolean isAngry() {
        return getHandle().isAngry();
    }

    public void setAngry(boolean angry) {
        getHandle().setAngry(angry);
    }

    @Override
    public EntityWolf getHandle() {
        return (EntityWolf) entity;
    }

    @Override
    public EntityType getType() {
        return EntityType.WOLF;
    }

	@Override
	public DyeColor getCollarColor() {
		return DyeColor.getByData((byte) getHandle().getCollarColor());
	}

	@Override
	public void setCollarColor(DyeColor color) {
		getHandle().setCollarColor(color.getData());
	}
}
