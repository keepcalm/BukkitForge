package keepcalm.mods.bukkit.bukkitAPI.entity;

import java.util.List;
import java.util.UUID;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitWorld;
import keepcalm.mods.bukkit.bukkitAPI.BukkitPlayerCache;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingSand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.network.packet.Packet61DoorChange;
import net.minecraft.world.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
//import org.bukkit.entity.Entity;

public class BukkitEntity implements org.bukkit.entity.Entity {
	protected final BukkitServer server;
	protected Entity entity;
	private EntityDamageEvent lastDamageEvent;

	public BukkitEntity(final BukkitServer server, final Entity entity) {
		this.server = server;
		this.entity = entity;
	}

	public static BukkitEntity getEntity(BukkitServer server, Entity entity) {
		/**
		 * Order is *EXTREMELY* important -- keep it right! =D
		 */
		if (entity instanceof EntityLiving) {
			// Players
			if (entity instanceof EntityPlayer) {
				if (entity instanceof EntityPlayerMP) { return BukkitPlayerCache.getBukkitPlayer(server, (EntityPlayerMP) entity); }
				else { return new BukkitEntityHuman(server, (EntityPlayer) entity); }
			}
			else if (entity instanceof EntityCreature) {
				// Animals
				if (entity instanceof EntityAnimal) {
					if (entity instanceof EntityChicken) { return new BukkitChicken(server, (EntityChicken) entity); }
					else if (entity instanceof EntityCow) {
						if (entity instanceof EntityMooshroom) { return new BukkitMushroomCow(server, (EntityMooshroom) entity); }
						else { return new BukkitCow(server, (EntityCow) entity); }
					}
					else if (entity instanceof EntityPig) { return new BukkitPig(server, (EntityPig) entity); }
					else if (entity instanceof EntityTameable) {
						if (entity instanceof EntityWolf) { return new BukkitWolf(server, (EntityWolf) entity); }
						else if (entity instanceof EntityOcelot) { return new BukkitOcelot(server, (EntityOcelot) entity); }
					}
					else if (entity instanceof EntitySheep) { return new BukkitSheep(server, (EntitySheep) entity); }
					else  { return new BukkitAnimals(server, (EntityAnimal) entity); }
				}
				// Monsters
				else if (entity instanceof EntityMob) {
					if (entity instanceof EntityZombie) {
						if (entity instanceof EntityPigZombie) { return new BukkitPigZombie(server, (EntityPigZombie) entity); }
						else { return new BukkitZombie(server, (EntityZombie) entity); }
					}
					else if (entity instanceof EntityCreeper) { return new BukkitCreeper(server, (EntityCreeper) entity); }
					else if (entity instanceof EntityEnderman) { return new BukkitEnderman(server, (EntityEnderman) entity); }
					else if (entity instanceof EntitySilverfish) { return new BukkitSilverfish(server, (EntitySilverfish) entity); }
					else if (entity instanceof EntityGiantZombie) { return new BukkitGiant(server, (EntityGiantZombie) entity); }
					else if (entity instanceof EntitySkeleton) { return new BukkitSkeleton(server, (EntitySkeleton) entity); }
					else if (entity instanceof EntityBlaze) { return new BukkitBlaze(server, (EntityBlaze) entity); }
					else if (entity instanceof EntitySpider) {
						if (entity instanceof EntityCaveSpider) { return new BukkitCaveSpider(server, (EntityCaveSpider) entity); }
						else { return new BukkitSpider(server, (EntitySpider) entity); }
					}

					else  { return new BukkitMonster(server, (EntityMob) entity); }
				}
				// Water Animals
				else if (entity instanceof EntityWaterMob) {
					if (entity instanceof EntitySquid) { return new BukkitSquid(server, (EntitySquid) entity); }
					else { return new BukkitWaterMob(server, (EntityWaterMob) entity); }
				}
				else if (entity instanceof EntityGolem) {
					if (entity instanceof EntitySnowman) { return new BukkitSnowman(server, (EntitySnowman) entity); }
					else if (entity instanceof EntityIronGolem) { return new BukkitIronGolem(server, (EntityIronGolem) entity); }
				}
				else if (entity instanceof EntityVillager) { return new BukkitVillager(server, (EntityVillager) entity); }
				else { return new BukkitCreature(server, (EntityCreature) entity); }
			}
			// Slimes are a special (and broken) case
			else if (entity instanceof EntitySlime) {
				if (entity instanceof EntityMagmaCube) { return new BukkitMagmaCube(server, (EntityMagmaCube) entity); }
				else { return new BukkitSlime(server, (EntitySlime) entity); }
			}
			// Flying
			else if (entity instanceof EntityFlying) {
				if (entity instanceof EntityGhast) { return new BukkitGhast(server, (EntityGhast) entity); }
				else { return new BukkitFlying(server, (EntityFlying) entity); }
			}
			else if (entity instanceof EntityDragon) {
				if (entity instanceof EntityDragon) { return new BukkitEnderDragon(server, (EntityDragon) entity); }
			}
			else  { return new BukkitLivingEntity(server, (EntityLiving) entity); }
		}
		else if (entity instanceof EntityDragonPart) {
			EntityDragonPart part = (EntityDragonPart) entity;
			if (part.entityDragonObj instanceof EntityDragonPart) { return new BukkitEnderDragonPart(server, (EntityDragonPart) entity); }
			else { return new BukkitComplexPart(server, (EntityDragonPart) entity); }
		}
		else if (entity instanceof EntityXPOrb) { return new BukkitExperienceOrb(server, (EntityXPOrb) entity); }
		else if (entity instanceof EntityArrow) { return new BukkitArrow(server, (EntityArrow) entity); }
		else if (entity instanceof EntityBoat) { return new BukkitBoat(server, (EntityBoat) entity); }
		else if (entity instanceof EntityThrowable) {
			if (entity instanceof EntityEgg) { return new BukkitEgg(server, (EntityEgg) entity); }
			else if (entity instanceof EntitySnowball) { return new BukkitSnowball(server, (EntitySnowball) entity); }
			else if (entity instanceof EntityPotion) { return new BukkitThrownPotion(server, (EntityPotion) entity); }
			else if (entity instanceof EntityEnderPearl) { return new BukkitEnderPearl(server, (EntityEnderPearl) entity); }
			else if (entity instanceof EntityExpBottle) { return new BukkitThrownExpBottle(server, (EntityExpBottle) entity); }
		}
		else if (entity instanceof EntityFallingSand) { return new BukkitFallingSand(server, (EntityFallingSand) entity); }
		else if (entity instanceof EntityFireball) {
			if (entity instanceof EntitySmallFireball) { return new BukkitSmallFireball(server, (EntitySmallFireball) entity); }
			else { return new BukkitFireball(server, (EntityFireball) entity); }
		}
		//else if (entity instanceof EntityEnderSignal) { return new BukkitEnderSignal(server, (EntityEnderSignal) entity); }
		else if (entity instanceof EntityEnderCrystal) { return new BukkitEnderCrystal(server, (EntityEnderCrystal) entity); }
		else if (entity instanceof EntityFishHook) { return new BukkitFish(server, (EntityFishHook) entity); }
		else if (entity instanceof EntityItem) { return new BukkitItem(server, (EntityItem) entity); }
		else if (entity instanceof EntityWeatherEffect) {
			if (entity instanceof EntityLightningBolt) { return new BukkitLightningStrike(server, (EntityLightningBolt) entity); }
			else { return new BukkitWeather(server, (EntityWeatherEffect) entity); }
		}
		else if (entity instanceof EntityMinecart) {
			EntityMinecart mc = (EntityMinecart) entity;
			if (mc.minecartType == BukkitMinecart.Type.StorageMinecart.getId()) { return new BukkitStorageMinecart(server, mc); }
			else if (mc.minecartType == BukkitMinecart.Type.PoweredMinecart.getId()) { return new BukkitPoweredMinecart(server, mc); }
			else { return new BukkitMinecart(server, mc); }
		}
		else if (entity instanceof EntityPainting) { return new BukkitPainting(server, (EntityPainting) entity); }
		else if (entity instanceof EntityTNTPrimed) { return new BukkitTNTPrimed(server, (EntityTNTPrimed) entity); }
		else if (entity instanceof EntityWitch) { return new BukkitWitch(server, (EntityWitch) entity); }
		else if (entity instanceof EntityWitherSkull) { return new BukkitWitherSkull(server, (EntityWitherSkull) entity); }
		else if (entity instanceof EntityWither) { return new BukkitWither(server, (EntityWither) entity); }
		else if (entity instanceof EntityItemFrame) { return new BukkitItemFrame(server, (EntityItemFrame) entity); }
		else if (entity instanceof EntityAmbientCreature) {
			if (entity instanceof EntityBat) {
				return new BukkitBat(server, (EntityBat) entity);
			}
			return new BukkitAmbient(server, (EntityBat) entity);

		}
		// unknown, last resort for mod entities
		if (entity == null) {
			System.out.println("WARNING - Null entity used. Things will begin exploding shortly.");
		}

		if (BukkitContainer.DEBUG) 
			BukkitServer.instance().getLogger().warning("Unknown entity: " + entity.getClass().getCanonicalName() + " - returning a dummy instance of BukkitEntity...");

		return new BukkitEntity(server,entity);
	}

	public Location getLocation() {
		Location loc = new Location(getWorld(), entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
		return loc;
	}

	public Vector getVelocity() {
		return new Vector(entity.motionX, entity.motionY, entity.motionZ);
	}

	public void setVelocity(Vector vel) {
		entity.motionX = vel.getX();
		entity.motionY = vel.getY();
		entity.motionZ = vel.getZ();
		entity.velocityChanged = true;
	}

	public World getWorld() {
		return BukkitServer.instance().getWorld(entity.worldObj.getWorldInfo().getDimension());
	}

	public boolean teleport(Location location) {
		return teleport(location, TeleportCause.PLUGIN);
	}

	public boolean teleport(Location location, TeleportCause cause) {
		if (!location.getWorld().equals(getWorld())) {
			if (this instanceof BukkitPlayer) {
				EntityPlayerMP fp = (EntityPlayerMP) entity;
				server.getHandle().getConfigurationManager().transferPlayerToDimension(fp, 1);
			}
			else {
				//System.out.println("[BukkitForge temp debug - will be removed in next build] TP " + this + " from " + getWorld() + " to " + location.getWorld());
				server.getHandle().getConfigurationManager().transferEntityToWorld(entity, 1, (WorldServer) entity.worldObj, ((BukkitWorld)location.getWorld()).getHandle());
			}
		}
		entity.setLocationAndAngles(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		// entity.setLocation() throws no event, and so cannot be cancelled
		return true;
	}

	public boolean teleport(org.bukkit.entity.Entity destination) {
		return teleport(destination.getLocation());
	}

	public boolean teleport(org.bukkit.entity.Entity destination, TeleportCause cause) {
		return teleport(destination.getLocation(), cause);
	}

	public List<org.bukkit.entity.Entity> getNearbyEntities(double x, double y, double z) {
		@SuppressWarnings("unchecked")
		List<Entity> notchEntityList = entity.worldObj.getEntitiesWithinAABB(Entity.class, entity.boundingBox.addCoord(x, y, z));
		List<org.bukkit.entity.Entity> bukkitEntityList = new java.util.ArrayList<org.bukkit.entity.Entity>(notchEntityList.size());

		for (Entity e : notchEntityList) {
			bukkitEntityList.add(BukkitEntity.getEntity(this.server, e));
		}
		return bukkitEntityList;
	}

	public int getEntityId() {
		return entity.entityId;
	}

	public int getFireTicks() {
		return entity.fire;
	}

	public int getMaxFireTicks() {
		return 100000000;
	}

	public void setFireTicks(int ticks) {
		entity.setFire(ticks / 20);
	}

	public void remove() {
		entity.setDead();
	}

	public boolean isDead() {
		return !entity.isEntityAlive();
	}

	public boolean isValid() {
		return entity.isEntityAlive();
	}

	public Server getServer() {
		return server;
	}

	public Vector getMomentum() {
		return getVelocity();
	}

	public void setMomentum(Vector value) {
		setVelocity(value);
	}

	public org.bukkit.entity.Entity getPassenger() {
		if (isEmpty()) {
			return null;
		}
		else {
			return BukkitEntity.getEntity(this.server, getHandle().ridingEntity);
		}
	}

	public boolean setPassenger(org.bukkit.entity.Entity passenger) {
		if (passenger instanceof BukkitEntity) {
			((BukkitEntity) passenger).getHandle().ridingEntity = (getHandle());
			return true;
		} else {
			return false;
		}
	}

	public boolean isEmpty() {
		return getHandle().riddenByEntity == null;
	}

	public boolean eject() {
		if (getHandle().riddenByEntity == null) {
			return false;
		}

		getHandle().riddenByEntity.ridingEntity = (null);
		return true;
	}

	public float getFallDistance() {
		return getHandle().fallDistance;
	}

	public void setFallDistance(float distance) {
		getHandle().fallDistance = distance;
	}

	public void setLastDamageCause(EntityDamageEvent event) {
		lastDamageEvent = event;
	}

	public EntityDamageEvent getLastDamageCause() {
		return lastDamageEvent;
	}

	public UUID getUniqueId() {
		return getHandle().getPersistentID();
	}

	public int getTicksLived() {
		return getHandle().ticksExisted;
	}

	public void setTicksLived(int value) {
		if (value <= 0) {
			throw new IllegalArgumentException("Age must be at least 1 tick");
		}
		getHandle().ticksExisted = value;
	}

	public Entity getHandle() {
		return entity;
	}

	public void playEffect(EntityEffect type) {
		//this.getHandle().worldObj.(getHandle(), type.getData());
		int posX = getHandle().serverPosX;
		int posY = getHandle().serverPosY;
		int posZ = getHandle().serverPosZ;

		Packet61DoorChange pack = new Packet61DoorChange(type.getData(), posX, posY, posZ, 0, false);
		BukkitServer.instance().getHandle().getConfigurationManager().sendToAllNear((double) posX, (double) posY, (double) posZ, 64.0D, getHandle().dimension, pack);
	}

	public void setHandle(final Entity entity) {
		this.entity = entity;
	}

	@Override
	public String toString() {
		return "BukkitEntity{" + "id=" + getEntityId() + '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final BukkitEntity other = (BukkitEntity) obj;
		return (this.getEntityId() == other.getEntityId());
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + this.getEntityId();
		return hash;
	}

	public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
		server.getEntityMetadata().setMetadata(this, metadataKey, newMetadataValue);
	}

	public List<MetadataValue> getMetadata(String metadataKey) {
		return server.getEntityMetadata().getMetadata(this, metadataKey);
	}

	public boolean hasMetadata(String metadataKey) {
		return server.getEntityMetadata().hasMetadata(this, metadataKey);
	}

	public void removeMetadata(String metadataKey, Plugin owningPlugin) {
		server.getEntityMetadata().removeMetadata(this, metadataKey, owningPlugin);
	}

	public boolean isInsideVehicle() {
		return getHandle().ridingEntity != null && getHandle().ridingEntity instanceof EntityMinecart;
	}

	public boolean leaveVehicle() {
		if (getHandle().ridingEntity == null || !(getHandle().ridingEntity instanceof EntityMinecart)) {
			return false;
		}

		getHandle().ridingEntity = (null);
		return true;
	}

	public org.bukkit.entity.Entity getVehicle() {
		if (getHandle().ridingEntity == null || !(getHandle().ridingEntity instanceof EntityMinecart)) {
			return null;
		}
		BukkitEntity j = BukkitEntity.getEntity(this.server, getHandle().ridingEntity);
		return j;
	}

	public boolean canSee(BukkitPlayer bukkitPlayer) {
		EntityPlayerMP j = bukkitPlayer.getHandle();
		if (j.canEntityBeSeen(getHandle())) {
			return true;
		}
		return false;
	}

	public Location getLocation(Location loc) {
		if (loc == null) return null;
		loc.setWorld(getWorld());
		loc.setX(getHandle().posX);
		loc.setY(getHandle().posY);
		loc.setZ(getHandle().posZ);
		return loc;
	}

	@Override
	public EntityType getType() {
		return EntityType.UNKNOWN;
	}
}
