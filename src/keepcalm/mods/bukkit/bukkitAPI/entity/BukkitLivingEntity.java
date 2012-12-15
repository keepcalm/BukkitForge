package keepcalm.mods.bukkit.bukkitAPI.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.packet.Packet42RemoveEntityEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.DamageSource;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.block.Block;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
//import net.minecraft.src.PotionEffect;

public class BukkitLivingEntity extends BukkitEntity implements LivingEntity {
	public BukkitLivingEntity(final BukkitServer server, final EntityLiving entity) {
		super(server, entity);
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
		//FIXME
		return 200;
	}

	public void setMaximumAir(int ticks) {
		// FIXME
		//getHandle().maxAirTicks = ticks;
	}

	public void damage(int amount) {
		damage(amount, null);
	}

	public void damage(int amount, org.bukkit.entity.Entity source) {
		DamageSource reason = DamageSource.generic;

		if (source instanceof HumanEntity) {
			reason = DamageSource.causePlayerDamage(((BukkitEntityHuman) source).getHandle());
		} else if (source instanceof LivingEntity) {
			reason = DamageSource.causeMobDamage(((BukkitLivingEntity) source).getHandle());
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
		return "CraftLivingEntity{" + "id=" + getEntityId() + '}';
	}

	public Player getKiller() {
		return getHandle().attackingPlayer == null && getHandle().isDead ? null : (Player) BukkitEntity.getEntity(this.server, getHandle().attackingPlayer);
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
		List<net.minecraft.potion.PotionEffect> j = PotionHelper.getPotionEffects(type.getId(), false);
		return getHandle().isPotionApplicable(j.get(0));
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
		net.minecraft.world.World world = ((BukkitWorld) getWorld()).getHandle();
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
		}
		Location location = getEyeLocation();
		Vector direction = location.getDirection().multiply(10);

		launch.setPositionAndRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		((EntityFireball) launch).accelerationX = direction.getX();
		((EntityFireball) launch).accelerationY = direction.getY();
		((EntityFireball) launch).accelerationZ = direction.getZ();

		//(direction.getX(), direction.getY(), direction.getZ());

		Validate.notNull(launch, "Projectile not supported");

		world.spawnEntityInWorld(launch);
		return (T) this.getEntity(this.server, launch);
	}

	public EntityType getType() {
		return EntityType.UNKNOWN;
	}

	public boolean hasLineOfSight(Entity other) {
		return getHandle().getEntitySenses().canSee(((BukkitEntity) other).getHandle()); // am should be getEntitySenses
	}
}
