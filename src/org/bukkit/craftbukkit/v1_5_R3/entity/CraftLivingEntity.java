package org.bukkit.craftbukkit.v1_5_R3.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import keepcalm.mods.bukkit.BukkitContainer;

import keepcalm.mods.bukkitforge.BukkitForgePlayerCache;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
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
import org.bukkit.craftbukkit.v1_5_R3.CraftServer;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R3.inventory.CraftEntityEquipment;
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
	
	public CraftLivingEntity(final CraftServer server, final EntityLivingBase entity) {
		super(server, entity);
		
		if (entity instanceof net.minecraft.entity.EntityLiving) {
            equipment = new CraftEntityEquipment(this);
        }
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
	
	public void damage(double amount) {
        damage(amount, null);
    }

    public void damage(double amount, org.bukkit.entity.Entity source) {
        net.minecraft.util.DamageSource reason = net.minecraft.util.DamageSource.generic;

        if (source instanceof HumanEntity) {
            reason = net.minecraft.util.DamageSource.causePlayerDamage(((CraftHumanEntity) source).getHandle());
        } else if (source instanceof LivingEntity) {
            reason = net.minecraft.util.DamageSource.causeMobDamage(((CraftLivingEntity) source).getHandle());
        }

        if (entity instanceof net.minecraft.entity.boss.EntityDragon) {
        	entity.attackEntityFrom(reason, (float) amount);
        } else {
            entity.attackEntityFrom(reason, (float) amount);
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

	public double getLastDamage() {
        return getHandle().field_110153_bc;
    }

	public int getNoDamageTicks() {
		return getHandle().hurtResistantTime;
	}

	public void setNoDamageTicks(int ticks) {
		getHandle().hurtResistantTime = ticks;
	}

	@Override
	public net.minecraft.entity.EntityLivingBase getHandle() {
        return (net.minecraft.entity.EntityLivingBase) entity;
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
		getHandle().removePotionEffect(type.getId()); // Should be removeEffect.
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
        return getHandle() instanceof net.minecraft.entity.EntityLiving && ((net.minecraft.entity.EntityLiving) getHandle()).getEntitySenses().canSee(((CraftEntity) other).getHandle());
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
	
	public double getHealth() {
        return Math.min(Math.max(0, getHandle().func_110143_aJ()), getMaxHealth());
    }

    public void setHealth(double health) {
        if ((health < 0) || (health > getMaxHealth())) {
            throw new IllegalArgumentException("Health must be between 0 and " + getMaxHealth());
        }

        if (entity instanceof net.minecraft.entity.player.EntityPlayerMP && health == 0) {
            ((net.minecraft.entity.player.EntityPlayerMP) entity).onDeath(net.minecraft.util.DamageSource.generic);
        }

        getHandle().setEntityHealth((float) health);
    }

    public double getMaxHealth() {
        return getHandle().func_110138_aP();
    }

    public void setMaxHealth(double amount) {
        Validate.isTrue(amount > 0, "Max health must be greater than 0");

        getHandle().func_110148_a(net.minecraft.entity.SharedMonsterAttributes.field_111267_a).func_111128_a(amount);

        if (getHealth() > amount) {
            setHealth(amount);
        }
    }

    public void resetMaxHealth() {
        setMaxHealth(getHandle().func_110138_aP());
    }
	
	public void setCustomName(String name) {
        if (!(getHandle() instanceof net.minecraft.entity.EntityLiving)) {
            return;
        }

        if (name == null) {
            name = "";
        }

        // Names cannot be more than 64 characters due to DataWatcher limitations
        if (name.length() > 64) {
            name = name.substring(0, 64);
        }

        ((net.minecraft.entity.EntityLiving) getHandle()).setCustomNameTag(name);
    }

    public String getCustomName() {
        if (!(getHandle() instanceof net.minecraft.entity.EntityLiving)) {
            return null;
        }

        String name = ((net.minecraft.entity.EntityLiving) getHandle()).getCustomNameTag();

        if (name == null || name.length() == 0) {
            return null;
        }

        return name;
    }

    public void setCustomNameVisible(boolean flag) {
    	if (getHandle() instanceof net.minecraft.entity.EntityLiving) {
            ((net.minecraft.entity.EntityLiving) getHandle()).setAlwaysRenderNameTag(flag);
        }
    }

    public boolean isCustomNameVisible() {
    	 return getHandle() instanceof net.minecraft.entity.EntityLiving && ((net.minecraft.entity.EntityLiving) getHandle()).getAlwaysRenderNameTag();
    }

	public void _INVALID_setMaxHealth(int health) {
        setMaxHealth(health);
    }

	public void setLastDamage(double damage) {
        getHandle().field_110153_bc = (float) damage;
    }

	public void _INVALID_setLastDamage(int damage) {
        setLastDamage(damage);
	}

}
