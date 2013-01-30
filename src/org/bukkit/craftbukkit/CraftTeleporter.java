package org.bukkit.craftbukkit;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * Dummy teleporter which does nothing
 * @author keepcalm
 *
 */
public class CraftTeleporter extends Teleporter {

	public CraftTeleporter(WorldServer par1WorldServer) {
		super(par1WorldServer);
	}
	
	@Override
	public void placeInPortal(Entity en, double par1, double par2, double par3, float par4) {
		
		
		
	}

}
