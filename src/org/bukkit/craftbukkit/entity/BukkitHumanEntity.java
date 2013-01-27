package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.EntityLiving;

import org.bukkit.craftbukkit.BukkitServer;

public class BukkitHumanEntity extends BukkitEntityHuman
{

	public BukkitHumanEntity(BukkitServer server, EntityLiving entity) 
	{
		super(server, entity);
	}

}
