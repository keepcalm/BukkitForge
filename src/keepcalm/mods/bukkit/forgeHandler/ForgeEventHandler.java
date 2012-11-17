package keepcalm.mods.bukkit.forgeHandler;

import keepcalm.mods.bukkit.bukkitAPI.BukkitChunk;
import keepcalm.mods.bukkit.bukkitAPI.event.BukkitEventFactory;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Item;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;

import org.bukkit.Bukkit;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
/**
 * 
 * @author keepcalm
 * 
 * TODO: Fix up more events - Forge doesn't include all the required events.
 *
 */
public class ForgeEventHandler {
	@ForgeSubscribe
	/**
	 * Can't cancel this
	 */
	public void onEntityJoinWorld(EntityJoinWorldEvent ev) {
		
		if (ev.entity instanceof EntityLiving) {// || ev.entity instanceof EntityPlayerMP) {
			
			BukkitEventFactory.callCreatureSpawnEvent((EntityLiving) ev.entity, SpawnReason.DEFAULT);
		}
	}
	@ForgeSubscribe
	public void onItemExpire(ItemExpireEvent ev) {
		BukkitEventFactory.callItemDespawnEvent(ev.entityItem);
	}
	@ForgeSubscribe
	public void onItemTossEvent(ItemTossEvent ev) {
		BukkitEventFactory.callItemSpawnEvent(ev.entityItem);
	}
	@ForgeSubscribe
	public void onLivingAttack(LivingAttackEvent ev) {
		if (BukkitEventFactory.callEntityDamageEvent(ev.source.getEntity(), ev.entity, DamageCause.valueOf(ev.source.getDamageType()), ev.ammount) == null) {
			
			ev.setCanceled(ev.isCancelable() ? true : false);
		}
	}
	@ForgeSubscribe
	public void onLivingDeathEvent(LivingDeathEvent ev) {
		BukkitEventFactory.callEntityDeathEvent(ev.entityLiving);
	}
	@ForgeSubscribe
	public void onLivingDropsEvent(LivingDropsEvent ev) {
		
		for (EntityItem i : ev.drops) {
			//BukkitEventFactory.callItemSpawnEvent(i);
		
		}
	}
	/*@ForgeSubscribe
	public void onLivingFall(LivingFallEvent ev) {
		BukkitEventFactory.callE
	}*/
	@ForgeSubscribe
	public void onLivingDamage(LivingHurtEvent ev) {
		BukkitEventFactory.callEntityDamageEvent(ev.source.getEntity(), ev.entity, DamageCause.valueOf(ev.source.getDamageType()), ev.ammount);
	}
	@ForgeSubscribe
	public void onTarget(LivingSetAttackTargetEvent ev) {
		BukkitEventFactory.callEntityTargetEvent(ev.entity, ev.target, TargetReason.CUSTOM);
	}
	/*@ForgeSubscribe
	public void specialSpawn(LivingSpecialSpawnEvent ev) {
		BukkitEventFactory.callCreatureSpawnEvent(ev.entityLiving, spawnReason)
	}
	@ForgeSubscribe
	public void onCartCollide(MinecartCollisionEvent ev) {
		BukkitEventFactory.
	}
	*/
	@ForgeSubscribe
	/**
	 * Only called when a player fires
	 * Forge doesn't give us the EntityArrow.
	 * @param ev
	 */
	public void bowFire(ArrowLooseEvent ev) {
		BukkitEventFactory.callEntityShootBowEvent(ev.entityPlayer, ev.bow, null, ev.charge);
	}
	
	@ForgeSubscribe
	public void playerVEntity(AttackEntityEvent ev) {
		BukkitEventFactory.callEntityDamageEvent(ev.entityPlayer, ev.target, DamageCause.ENTITY_ATTACK, ev.entityPlayer.inventory.getDamageVsEntity(ev.target));
	}
	/*
	@ForgeSubscribe
	public void bonemeal(BonemealEvent ev) {
	}*/
	@ForgeSubscribe
	public void playerSaysHai(PlayerInteractEvent ev) {
		if (ev.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
			
		}
	}

	@ForgeSubscribe
	public void pickUp(EntityItemPickupEvent ev) {
		BukkitEventFactory.callItemDespawnEvent(ev.item);
	}
	
	@ForgeSubscribe
	public void fillBukkit(FillBucketEvent ev) {
		BukkitEventFactory.callPlayerBucketFillEvent((EntityPlayerMP) ev.entityPlayer, ev.target.blockX, ev.target.blockY, ev.target.blockZ, ev.target.sideHit, ev.current,Item.itemsList[ev.result.itemID]);
	}
	
	@ForgeSubscribe
	public void interactEvent(PlayerInteractEvent ev) {
		BukkitEventFactory.callPlayerInteractEvent((EntityPlayerMP) ev.entityPlayer, Action.valueOf(ev.action.toString()), ev.entityPlayer.inventory.getCurrentItem());
	}
	/*
	@ForgeSubscribe
	public void goToSleep(PlayerSleepInBedEvent ev) {
		
	}*/
	@ForgeSubscribe
	public void chunkLoadEvent(ChunkEvent.Load ev) {
		org.bukkit.event.world.ChunkLoadEvent c = new org.bukkit.event.world.ChunkLoadEvent(new BukkitChunk(ev.getChunk()), false);
		Bukkit.getPluginManager().callEvent(c);
	}
	
	@ForgeSubscribe
	public void chunkUnloadEvent(ChunkEvent.Unload ev) {
		org.bukkit.event.world.ChunkUnloadEvent c = new org.bukkit.event.world.ChunkUnloadEvent(new BukkitChunk(ev.getChunk()));
		Bukkit.getPluginManager().callEvent(c);
	}
	
}
