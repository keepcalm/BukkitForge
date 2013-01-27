package org.bukkit.craftbukkit.entity;

import net.minecraft.entity.EntityLiving;

import org.bukkit.craftbukkit.CraftServer;

public class CraftHumanEntity extends CraftEntityHuman
{

	public CraftHumanEntity(CraftServer server, EntityLiving entity) 
	{
		super(server, entity);
	}

}
