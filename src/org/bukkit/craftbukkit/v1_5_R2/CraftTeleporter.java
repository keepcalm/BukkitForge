package org.bukkit.craftbukkit.v1_5_R2;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
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
	public void placeInPortal(Entity par1Entity, double par1, double par2, double par3, float par4) {
        //super.func_85188_a(par1Entity);
        //super.placeInExistingPortal(par1Entity, par1, par2, par3, par4);
        int var9 = MathHelper.floor_double(par1Entity.posX);
        int var10 = MathHelper.floor_double(par1Entity.posY) - 1;
        int var11 = MathHelper.floor_double(par1Entity.posZ);
        par1Entity.setLocationAndAngles((double)var9, (double)var10, (double)var11, par1Entity.rotationYaw, 0.0F);
        par1Entity.motionX = par1Entity.motionY = par1Entity.motionZ = 0.0D;
		System.out.println("Placed " + par1Entity + " in portal." );
	}

}
