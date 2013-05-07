package org.bukkit.craftbukkit.v1_5_R2.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import keepcalm.mods.bukkit.BukkitContainer;

import keepcalm.mods.bukkitforge.BukkitForgePlayerCache;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.packet.Packet42RemoveEntityEffect;
import net.minecraft.util.DamageSource;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_5_R2.CraftServer;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R2.inventory.CraftEntityEquipment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
//import net.minecraft.src.PotionEffect;

public class CraftLivingEntity extends CraftEntity implements LivingEntity {
	private CraftEntityEquipment equipment;
	public CraftLivingEntity(final CraftServer server, final EntityLiving entity) {
		super(server, entity);
		
		if (!(this instanceof HumanEntity)) {
            equipment = new CraftEntityEquipment(this);
        }
	}

	public int getHealth() {
		return getHandle().getHealth();
	}

	public void setHealth(int health) {
		if ((health < 0) || (health > getMaxHealth())) {
			throw new IllegalArgumentException("Health must be between 0 and " + getMaxHealth());
		}

		if (entity instanceof EntityPlayer && health == 0) {
			entity.setDead();//(DamageSource.generic);
		}

		getHandle().setEntityHealth(health);
	}

	public int getMaxHealth() {
		return getHandle().getMaxHealth();
	}

	@Deprecated
	public Egg throwEgg() {
		return launchProjectile(Egg.class);
	}

	@Deprecated
	public Snowball throwSnowball() {
		return launchProjectile(Snowball.class);
	}

	public double getEyeHeight() {
		return 1.0D;
	}

	public double getEyeHeight(boolean ignoreSneaking) {
		return getEyeHeight();
	}

	private List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance, int maxLength) {
		if (maxDistance > 120) {
			maxDistance = 120;
		}
		ArrayList<Block> blocks = new ArrayList<Block>();
		Iterator<Block> itr = new BlockIterator(this, maxDistance);
		while (itr.hasNext()) {
			Block block = itr.next();
			blocks.add(block);
			if (maxLength != 0 && blocks.size() > maxLength) {
				blocks.remove(0);
			}
			int id = block.getTypeId();
			if (transparent == null) {
				if (id != 0) {
					break;
				}
			} else {
				if (!transparent.contains((byte) id)) {
					break;
				}
			}
		}
		return blocks;
	}

	public List<Block> getLineOfSight(HashSet<Byte> transparent, int maxDistance) {
		return getLineOfSight(transparent, maxDistance, 0);
	}

	public Block getTargetBlock(HashSet<Byte> transparent, int maxDistance) {
		List<Block> blocks = getLineOfSight(transparent, maxDistance, 1);
		return blocks.get(0);
	}

	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> transparent, int maxDistance) {
		return getLineOfSight(transparent, maxDistance, 2);
	}

	@Deprecated
	public Arrow shootArrow() {
		return launchProjectile(Arrow.class);
	}

	public int getRemainingAir() {
		return getHandle().getAir();
	}

	public void setRemainingAir(int ticks) {
		getHandle().setAir(ticks);
	}

	public int getMaximumAir() {
		return getHandle().getAir();
	}

	public void setMaximumAir(int ticks) {
		getHandle().setAir(ticks);
	}

	public void damage(int amount) {
		damage(amount, null);
	}

	public void damage(int amount, org.bukkit.entity.Entity source) {
		DamageSource reason = DamageSource.generic;

		if (source instanceof HumanEntity) {
			reason = DamageSource.causePlayerDamage(((CraftHumanEntity) source).getHandle());
		} else if (source instanceof LivingEntity) {
			reason = DamageSource.causeMobDamage(((CraftLivingEntity) source).getHandle());
		}

		if (entity instanceof EntityDragon) {
			entity.attackEntityFrom(reason, amount);
			//((EntityDragon) entity).da(reason, amount);
		} else {
			entity.attackEntityFrom(reason, amount);
		}
	}

	public Location getEyeLocation() {
		Location loc = getLocation();
		loc.setY(loc.getY() + getEyeHeight());
		return loc;
	}

	public int getMaximumNoDamageTicks() {
		return getHandle().maxHurtResistantTime;
	}

	public void setMaximumNoDamageTicks(int ticks) {
		getHandle().maxHurtResistantTime = ticks;
	}

	public int getLastDamage() {
		return getHandle().lastDamage;
	}

	public void setLastDamage(int damage) {
		getHandle().lastDamage = damage;
	}

	public int getNoDamageTicks() {
		return getHandle().hurtResistantTime;
	}

	public void setNoDamageTicks(int ticks) {
		getHandle().hurtResistantTime = ticks;
	}

	@Override
	public EntityLiving getHandle() {
		return (EntityLiving) entity;
	}

	public void setHandle(final EntityLiving entity) {
		super.setHandle(entity);
	}

	@Override
	public String toString() {
		return "BukkiyLivingEntity{" + "id=" + getEntityId() + '}';
	}

	public Player getKiller() {
		EntityPlayerMP fp;
		if (getHandle().attackingPlayer instanceof EntityPlayerMP) {
			fp = (EntityPlayerMP) getHandle().attackingPlayer;
		}
		else {
			fp = BukkitContainer.MOD_PLAYER;
		}
		return getHandle().attackingPlayer == null && getHandle().isDead ? null : BukkitForgePlayerCache.getCraftPlayer(fp);
	}

	public boolean addPotionEffect(PotionEffect effect) {
		return addPotionEffect(effect, false);
	}

	public boolean addPotionEffect(PotionEffect effect, boolean force) {
		if (hasPotionEffect(effect.getType())) {
			if (!force) {
				return false;
			}
			removePotionEffect(effect.getType());
		}
		getHandle().addPotionEffect(new net.minecraft.potion.PotionEffect(effect.getType().getId(), effect.getDuration(), effect.getAmplifier()));
		return true;
	}

	public boolean addPotionEffects(Collection<PotionEffect> effects) {
		boolean success = true;
		for (PotionEffect effect : effects) {
			success &= addPotionEffect(effect);
		}
		return success;
	}

	public boolean hasPotionEffect(PotionEffectType type) {
		return getHandle().isPotionActive(net.minecraft.potion.Potion.potionTypes[type.getId()]);
	}

	public void removePotionEffect(PotionEffectType type) {
		getHandle().removePotionEffect(type.getId());
		//getHandle().potion = true;
		if (getHandle() instanceof EntityPlayer) {
			if (((EntityPlayerMP) getHandle()).playerNetServerHandler == null) return;
			((EntityPlayerMP) getHandle()).playerNetServerHandler.handleRemoveEntityEffect(new Packet42RemoveEntityEffect(getHandle().entityId, new net.minecraft.potion.PotionEffect(type.getId(), 0, 0)));
		}
	}

	public Collection<PotionEffect> getActivePotionEffects() {
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		for (Object raw : getHandle().getActivePotionEffects()) {
			if (!(raw instanceof PotionEffect))
				continue;
			PotionEffect handle = (PotionEffect) raw;
			effects.add(new PotionEffect(PotionEffectType.getById(handle.getType().getId()), handle.getDuration(), handle.getAmplifier()));
		}
		return effects;
	}

	@SuppressWarnings("unchecked")
	public <T extends Projectile> T launchProjectile(Class<? extends T> projectile) {
		net.minecraft.world.World world = ((CraftWorld) getWorld()).getHandle();
		net.minecraft.entity.Entity launch = null;

		if (Snowball.class.isAssignableFrom(projectile)) {
			launch = new EntitySnowball(world, getHandle());
		} else if (Egg.class.isAssignableFrom(projectile)) {
			launch = new EntityEgg(world, getHandle());
		} else if (EnderPearl.class.isAssignableFrom(projectile)) {
			launch = new EntityEnderPearl(world, getHandle());
		} else if (Arrow.class.isAssignableFrom(projectile)) {
			launch = new EntityArrow(world, getHandle(), 1);
		} else if (Fireball.class.isAssignableFrom(projectile)) {
			if (SmallFireball.class.isAssignableFrom(projectile)) {
				launch = new EntitySmallFireball(world);
			}
			else {
				launch = new EntityLargeFireball(world);
			}

			Location location = getEyeLocation();
			Vector direction = location.getDirection().multiply(10);

			((EntityFireball)launch).accelerationX = direction.getX();
			((EntityFireball)launch).accelerationY = direction.getY();
			((EntityFireball)launch).accelerationZ = direction.getZ();
		}
		//(direction.getX(), direction.getY(), direction.getZ());

		Location location = getEyeLocation();
		Vector direction = location.getDirection().multiply(10);
		launch.motionX = direction.getX();
		launch.motionY = direction.getY();
		launch.motionZ = direction.getZ();
		
		launch.setPositionAndRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		Validate.notNull(launch, "Projectile not supported");

		world.spawnEntityInWorld(launch);
		return (T) CraftLivingEntity.getEntity(this.server, launch);
	}

	public EntityType getType() {
		return EntityType.UNKNOWN;
	}

	public boolean hasLineOfSight(Entity other) {
		return getHandle().getEntitySenses().canSee(((CraftEntity) other).getHandle()); // am should be getEntitySenses
	}

	@Override
	public boolean getRemoveWhenFarAway() {
		return getHandle() instanceof EntityMob || getHandle() instanceof EntityBat;
	}

	@Override
	public void setRemoveWhenFarAway(boolean remove) {
		// unimplemented
	}

	@Override
	public EntityEquipment getEquipment() {
        return equipment;
    }

	@Override
	public void setCanPickupItems(boolean pickup) {
		getHandle().captureDrops = pickup;
	}

	@Override
	public boolean getCanPickupItems() {
		return getHandle().captureDrops;//instanceof EntityPlayer;
	}

	@Override
	public void setMaxHealth(int health) {
		// TODO
	}

	@Override
	public void resetMaxHealth() {
		// TODO

	}
	
	public void setCustomName(String name) {
        if (name == null) {
            name = "";
        }

        // Names cannot be more than 64 characters due to DataWatcher limitations
        if (name.length() > 64) {
            name = name.substring(0, 64);
        }

        getHandle().func_94058_c(name);
    }

    public String getCustomName() {
        String name = getHandle().func_94057_bL();

        if (name == null || name.length() == 0) {
            return null;
        }

        return name;
    }

    public void setCustomNameVisible(boolean flag) {
        getHandle().func_94061_f(flag);
    }

    public boolean isCustomNameVisible() {
        return getHandle().func_94062_bN();
    }
}
