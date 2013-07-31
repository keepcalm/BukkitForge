package org.bukkit.craftbukkit.v1_6_R2.entity;

import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.util.AxisAlignedBB;

import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
//import org.bukkit.craftbukkit.v1_6_R2.CraftServer;

public class CraftComplexPart extends CraftEntity implements ComplexEntityPart {
	public CraftComplexPart(CraftServer server, EntityDragonPart entity) {
		super(server, entity);
	}

	public ComplexLivingEntity getParent() {
		return (ComplexLivingEntity) this.getEntity(this.server, getHandle().entityDragonObj);
	}

	private ComplexLivingEntity getEntity(CraftServer server,
			IEntityMultiPart entityDragonObj) {
		return new CraftEnderDragon(this.server, (EntityDragon) this.entity.worldObj.getEntitiesWithinAABB(EntityDragon.class, AxisAlignedBB.getAABBPool().getAABB(entity.posX + 15, entity.posY + 15, entity.posZ + 15, 0, 0, 0)).get(0));
	}

	@Override
	public void setLastDamageCause(EntityDamageEvent cause) {
		getParent().setLastDamageCause(cause);
	}

	@Override
	public EntityDamageEvent getLastDamageCause() {
		return getParent().getLastDamageCause();
	}

	@Override
	public EntityDragonPart getHandle() {
		return (EntityDragonPart) entity;
	}

	@Override
	public String toString() {
		return "CraftComplexPart";
	}

	public EntityType getType() {
		return EntityType.COMPLEX_PART;
	}
}
