package keepcalm.mods.bukkit.forgeHandler;

import java.util.ArrayList;
import java.util.HashMap;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.bukkitAPI.BukkitChunk;
import keepcalm.mods.bukkit.bukkitAPI.BukkitPlayerCache;
import keepcalm.mods.bukkit.bukkitAPI.BukkitServer;
import keepcalm.mods.bukkit.bukkitAPI.block.BukkitBlock;
import keepcalm.mods.bukkit.bukkitAPI.block.BukkitBlockFake;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitItem;
import keepcalm.mods.bukkit.bukkitAPI.entity.BukkitPlayer;
import keepcalm.mods.bukkit.bukkitAPI.event.BukkitEventFactory;
import keepcalm.mods.bukkit.bukkitAPI.inventory.BukkitItemStack;
import keepcalm.mods.bukkit.events.BlockDestroyEvent;
import keepcalm.mods.bukkit.events.DispenseItemEvent;
import keepcalm.mods.bukkit.events.PlayerDamageBlockEvent;
import keepcalm.mods.bukkit.events.PlayerUseItemEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IRegistry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.ChunkEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.FMLCommonHandler;
/**
 * 
 * @author keepcalm
 * 
 * TODO: Fix up more events - Forge doesn't include all the required events.
 *
 */
public class ForgeEventHandler {

	public static HashMap<String, String> playerDisplayNames = new HashMap<String, String>();

	public static boolean ready = false;

	public static DamageCause getDamageCause(DamageSource ds) {

		DamageCause dc;
		if (ds == ds.anvil)
			dc = DamageCause.CUSTOM;
		else if (ds == ds.cactus)
			dc = DamageCause.CONTACT;
		else if (ds == ds.drown)
			dc = DamageCause.DROWNING;
		else if (ds == ds.explosion)
			dc = DamageCause.BLOCK_EXPLOSION;
		else if (ds == ds.fall)
			dc = DamageCause.FALL;
		else if (ds == ds.fallingBlock)
			dc = DamageCause.FALL;
		else if (ds == ds.field_76375_l)
			dc = DamageCause.ENTITY_EXPLOSION;
		else if (ds == ds.generic)
			dc = DamageCause.CUSTOM;
		else if (ds == ds.inFire)
			dc = DamageCause.FIRE;
		else if (ds == ds.inWall)
			dc = DamageCause.SUFFOCATION;
		else if (ds == ds.lava)
			dc = DamageCause.LAVA;
		else if (ds == ds.magic)
			dc = DamageCause.MAGIC;
		else if (ds == ds.onFire)
			dc = DamageCause.FIRE_TICK;
		else if (ds == ds.outOfWorld)
			dc = DamageCause.VOID;
		else if (ds == ds.starve)
			dc = DamageCause.STARVATION;
		else if (ds == ds.wither)
			dc = DamageCause.WITHER;
		else
			dc = DamageCause.CUSTOM;
		return dc;
	}

	@ForgeSubscribe
	/**
	 * Can't cancel this
	 */
	public void onEntityJoinWorld(EntityJoinWorldEvent ev) {
		if (!ready || FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		if (ev.entity instanceof EntityLiving && !(ev.entity instanceof EntityPlayer)) {// || ev.entity instanceof EntityPlayerMP) {

			BukkitEventFactory.callCreatureSpawnEvent((EntityLiving) ev.entity, SpawnReason.DEFAULT);
		}
	}
	@ForgeSubscribe
	public void onItemExpire(ItemExpireEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		BukkitEventFactory.callItemDespawnEvent(ev.entityItem);
	}
	@ForgeSubscribe
	public void onItemTossEvent(ItemTossEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		BukkitEventFactory.callItemSpawnEvent(ev.entityItem);
	}
	@ForgeSubscribe
	public void onLivingAttack(LivingAttackEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		if (BukkitEventFactory.callEntityDamageEvent(ev.entityLiving.getLastAttackingEntity(),
				ev.entity, 
				getDamageCause(ev.source), 
				ev.ammount)
				.isCancelled()) {

			ev.setCanceled(true);
		}
	}
	@ForgeSubscribe
	public void onLivingDeathEvent(LivingDeathEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		BukkitEventFactory.callEntityDeathEvent(ev.entityLiving);
	}

	/*@ForgeSubscribe
	public void onLivingFall(LivingFallEvent ev) {
		BukkitEventFactory.callE
	}*/
	@ForgeSubscribe
	public void onLivingDamage(LivingHurtEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		DamageCause dc = getDamageCause(ev.source);


		BukkitEventFactory.callEntityDamageEvent(ev.source.getEntity(), ev.entity, dc, ev.ammount);
	}
	@ForgeSubscribe
	public void onTarget(LivingSetAttackTargetEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
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
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		
		//BukkitEventFactory.callEntityShootBowEvent(ev.entityPlayer, ev.bow, null, ev.charge);
	}

	@ForgeSubscribe
	public void playerVEntity(AttackEntityEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		BukkitEventFactory.callEntityDamageEvent(ev.entityPlayer, ev.target, DamageCause.ENTITY_ATTACK, ev.entityPlayer.inventory.getDamageVsEntity(ev.target));
	}
	/*
	@ForgeSubscribe
	public void bonemeal(BonemealEvent ev) {
	}*/
	@ForgeSubscribe
	public void onPlayerInteraction(final PlayerInteractEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		if (ev.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
			if (!ev.entityPlayer.isSneaking() && ev.entityPlayer.worldObj.blockHasTileEntity(ev.x, ev.y, ev.z)) {
				return;
			}
			if (ev.entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemBlock) {
				if (BukkitContainer.DEBUG)
					System.out.println("PLACE!");
				final BukkitItemStack itemInHand = new BukkitItemStack(ev.entityPlayer.inventory.getCurrentItem());
				final int blockX = ev.x + ForgeDirection.getOrientation(ev.face).offsetX;
				final int blockY = ev.y + ForgeDirection.getOrientation(ev.face).offsetY;
				final int blockZ = ev.z + ForgeDirection.getOrientation(ev.face).offsetZ;
				final BukkitPlayer thePlayer = new BukkitPlayer((EntityPlayerMP) ev.entityPlayer);
				final BukkitBlock beforeBlock = new BukkitBlock(new BukkitChunk(ev.entity.worldObj.getChunkFromBlockCoords(ev.x, ev.y)), blockX, blockY, blockZ);
				WorldServer world = (WorldServer) ev.entity.worldObj;
				int minX = world.getSpawnPoint().posX;
				int minY = world.getSpawnPoint().posY;
				int minZ = world.getSpawnPoint().posZ;
				int sps = MinecraftServer.getServer().getSpawnProtectionSize();
				int maxX = minX + sps;
				int maxY = minY + sps;
				int maxZ = minZ + sps;

				AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
				final boolean canBuild;
				if (aabb.isVecInside(Vec3.vec3dPool.getVecFromPool(blockX, blockY, blockZ))) {
					canBuild = false;
				}
				else {
					canBuild = true;
				}




				BukkitBlock placedBlock = new BukkitBlockFake(new BukkitChunk(ev.entity.worldObj.getChunkFromBlockCoords(ev.x, ev.y)), blockX, blockY, blockZ, itemInHand.getTypeId(), itemInHand.getDurability());
				BlockPlaceEvent bev = new BlockPlaceEvent(placedBlock, beforeBlock.getState(), placedBlock, itemInHand, thePlayer, canBuild);

				Bukkit.getPluginManager().callEvent(bev);

				if (bev.isCancelled() || !bev.canBuild()) {
					ev.setCanceled(true);
				}

			}
		}
	}

	@ForgeSubscribe
	public void pickUp(EntityItemPickupEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		// assume all picked up at the same time
		PlayerPickupItemEvent bev = new PlayerPickupItemEvent(new BukkitPlayer((EntityPlayerMP) ev.entityPlayer), new BukkitItem(BukkitServer.instance(), ev.item), 0);
		bev.setCancelled(ev.entityLiving.captureDrops);
		Bukkit.getPluginManager().callEvent(bev);
		if (bev.isCancelled()) {
			ev.setCanceled(true);
			ev.setResult(Result.DENY);
		}
	}

	@ForgeSubscribe
	public void fillBukkit(FillBucketEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		if (BukkitEventFactory.callPlayerBucketFillEvent((EntityPlayerMP) ev.entityPlayer, ev.target.blockX, ev.target.blockY, ev.target.blockZ, ev.target.sideHit, ev.current,Item.itemsList[ev.result.itemID]).isCancelled())
			ev.setCanceled(true);
	}

	@ForgeSubscribe
	public void interactEvent(PlayerInteractEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		Action act;
		switch (ev.action) 
		{
		case LEFT_CLICK_BLOCK:
			act = Action.LEFT_CLICK_BLOCK;
			break;
		case RIGHT_CLICK_AIR:
			act = Action.RIGHT_CLICK_AIR;
			break;
		case RIGHT_CLICK_BLOCK:
			act = Action.RIGHT_CLICK_BLOCK;
			break;
		default:
			act= Action.PHYSICAL;
		}
		BukkitBlock bb = new BukkitBlock(new BukkitChunk(ev.entity.worldObj.getChunkFromBlockCoords(ev.x, ev.z)), ev.x,ev.y,ev.z);
		BlockFace face = BukkitBlock.notchToBlockFace(ev.face);
		org.bukkit.event.player.PlayerInteractEvent bev = 
				new org.bukkit.event.player.PlayerInteractEvent(BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) ev.entityPlayer), act, new BukkitItemStack(ev.entityPlayer.inventory.getCurrentItem()), bb, face);

		Bukkit.getPluginManager().callEvent(bev);

		if (bev.isCancelled()) {
			ev.setCanceled(true);
		}

		//BukkitEventFactory.callPlayerInteractEvent((EntityPlayerMP) ev.entityPlayer, act, ev.entityPlayer.inventory.getCurrentItem());

	}

	@ForgeSubscribe
	public void playerGoToSleep(PlayerSleepInBedEvent ev) {
		org.bukkit.event.player.PlayerBedEnterEvent bev = new PlayerBedEnterEvent(new BukkitPlayer((EntityPlayerMP) ev.entityPlayer), new BukkitBlock(new BukkitChunk(ev.entityPlayer.worldObj.getChunkFromBlockCoords(ev.x, ev.z)), ev.x, ev.y, ev.z));

		Bukkit.getPluginManager().callEvent(bev);
	}
	@ForgeSubscribe
	public void chunkLoadEvent(ChunkEvent.Load ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

		final org.bukkit.event.world.ChunkLoadEvent c = new org.bukkit.event.world.ChunkLoadEvent(new BukkitChunk(ev.getChunk()), false);

		Bukkit.getPluginManager().callEvent(c);
	}

	@ForgeSubscribe
	public void chunkUnloadEvent(ChunkEvent.Unload ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		org.bukkit.event.world.ChunkUnloadEvent c = new org.bukkit.event.world.ChunkUnloadEvent(new BukkitChunk(ev.getChunk()));
		Bukkit.getPluginManager().callEvent(c);
	}

	@ForgeSubscribe
	public void serverChat(ServerChatEvent ev) {
		if (!ready)
			return;
		String newName = ev.player.username;
		if (playerDisplayNames.containsKey(newName)) {
			newName = playerDisplayNames.get(newName);
		}
		BukkitPlayer whom = new BukkitPlayer(ev.player);

		AsyncPlayerChatEvent ev1 = new AsyncPlayerChatEvent(false, whom, ev.message, Sets.newHashSet(BukkitServer.instance().getOnlinePlayers()));
		ev1 = BukkitEventFactory.callEvent(ev1);
		String newLine = String.format(ev1.getFormat(),new Object[] {newName, ev1.getMessage()});
		ev.line = newLine;
	}

	@ForgeSubscribe
	public void saplingGrow(SaplingGrowTreeEvent ev) {
		int blockID = ev.world.getBlockId(ev.x, ev.y, ev.z);
		int blockMeta = ev.world.getBlockMetadata(ev.x, ev.y, ev.z);

		if (Block.blocksList[blockID] == Block.sapling) {
			TreeType type = TreeType.TREE;
			

			StructureGrowEvent bev = new StructureGrowEvent(new Location(BukkitServer.instance().getWorld(ev.world.getWorldInfo().getDimension()),ev.x,ev.y,ev.z), type, false, null, new ArrayList<BlockState>());
		}
	}


	// begin BukkitForge-added events


	@ForgeSubscribe
	public void tryPlaceBlock(PlayerUseItemEvent ev) {
		if (ev.stack.getItem() instanceof ItemBlock) {
			ItemBlock block = (ItemBlock) ev.stack.getItem();
			BukkitChunk chunk = new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.x, ev.z));
			ChunkCoordinates spawn = ev.world.getSpawnPoint();
			int spawnRadius = BukkitServer.instance().getSpawnRadius();
			boolean canBuild = AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(spawn.posX, spawn.posY, spawn.posZ, spawn.posX + spawnRadius, spawn.posY + spawnRadius, spawn.posZ + spawnRadius).isVecInside(Vec3.createVectorHelper(ev.x, ev.y, ev.z));
			BukkitBlock bblock = new BukkitBlock(chunk, ev.x, ev.y, ev.z);
			BlockCanBuildEvent bukkitEv = new BlockCanBuildEvent(bblock, block.getBlockID(), canBuild);
			if (!bukkitEv.isBuildable() && canBuild) {
				// it was changed
				// and since we were called from AFTER the actual placement, we can just break the block.
				bblock.breakNaturally();
			}
		}
	}

	@ForgeSubscribe
	public void dispenseItem(DispenseItemEvent ev) {
		ItemStack item = ev.stackToDispense.copy();
		item.stackSize = 1;

		IRegistry dispenserRegistry = BlockDispenser.dispenseBehaviorRegistry;
		IBehaviorDispenseItem theBehaviour = (IBehaviorDispenseItem) dispenserRegistry.func_82594_a(item.getItem());
		BlockDispenseEvent bev = new BlockDispenseEvent(
				new BukkitBlock(
						new BukkitChunk(ev.blockWorld.getChunkFromBlockCoords(ev.blockX, ev.blockZ)),
						ev.blockX, ev.blockY, ev.blockZ), new BukkitItemStack(item), new Vector());
		Bukkit.getPluginManager().callEvent(bev);
		if (bev.isCancelled()) {
			ev.setCanceled(true);
		}
		else {
			ev.setCanceled(false);
		}
	}

	@ForgeSubscribe
	public void playerDamageBlock(PlayerDamageBlockEvent ev) {
		BlockDamageEvent bev = new BlockDamageEvent(new BukkitPlayer((EntityPlayerMP) ev.entityPlayer), 
				new BukkitBlock(new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.blockX, ev.blockZ)), ev.blockX, ev.blockY, ev.blockZ),
				new BukkitItemStack(ev.entityPlayer.inventory.getCurrentItem()), 
				((EntityPlayerMP) ev.entityPlayer).capabilities.isCreativeMode);
		Bukkit.getPluginManager().callEvent(bev);
		if (bev.isCancelled()) {
			ev.setCanceled(true);
			return;
		}
		if (bev.getInstaBreak()) {
			Block.blocksList[ev.blockID].breakBlock(ev.world, ev.blockX, ev.blockY, ev.blockZ, ev.blockID, ev.world.getBlockMetadata(ev.blockX, ev.blockY, ev.blockZ));
			return;
		}

	}

	@ForgeSubscribe
	public void blockBreakSomehow(BlockDestroyEvent ev) {
		BlockBreakEvent bev = new BlockBreakEvent(new BukkitBlock(new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.x, ev.y)), ev.x, ev.y, ev.z), new BukkitPlayer(BukkitContainer.MOD_PLAYER));
		Bukkit.getPluginManager().callEvent(bev);

		if (bev.isCancelled()) {
			ev.setCanceled(true);
		}
		//ignore XP etc
	}

}
