package keepcalm.mods.bukkit.forgeHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.events.events.BlockDestroyEvent;
import keepcalm.mods.events.events.CreeperExplodeEvent;
import keepcalm.mods.events.events.DispenseItemEvent;
import keepcalm.mods.events.events.LightningStrikeEvent;
import keepcalm.mods.events.events.LiquidFlowEvent;
import keepcalm.mods.events.events.PlayerDamageBlockEvent;
import keepcalm.mods.events.events.PlayerMoveEvent;
import keepcalm.mods.events.events.PressurePlateInteractEvent;
import keepcalm.mods.events.events.SheepDyeEvent;
import keepcalm.mods.events.events.SignChangeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EnumStatus;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.CommandEvent;
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
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.util.Vector;

import com.google.common.base.Joiner;
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
		if (ds == DamageSource.anvil)
			dc = DamageCause.CUSTOM;
		else if (ds == DamageSource.cactus)
			dc = DamageCause.CONTACT;
		else if (ds == DamageSource.drown)
			dc = DamageCause.DROWNING;
		else if (ds == DamageSource.explosion)
			dc = DamageCause.BLOCK_EXPLOSION;
		else if (ds == DamageSource.fall)
			dc = DamageCause.FALL;
		else if (ds == DamageSource.fallingBlock)
			dc = DamageCause.FALL;
		else if (ds == DamageSource.explosion2)
			dc = DamageCause.ENTITY_EXPLOSION;
		else if (ds == DamageSource.generic)
			dc = DamageCause.CUSTOM;
		else if (ds == DamageSource.inFire)
			dc = DamageCause.FIRE;
		else if (ds == DamageSource.inWall)
			dc = DamageCause.SUFFOCATION;
		else if (ds == DamageSource.lava)
			dc = DamageCause.LAVA;
		else if (ds == DamageSource.magic)
			dc = DamageCause.MAGIC;
		else if (ds == DamageSource.onFire)
			dc = DamageCause.FIRE_TICK;
		else if (ds == DamageSource.outOfWorld)
			dc = DamageCause.VOID;
		else if (ds == DamageSource.starve)
			dc = DamageCause.STARVATION;
		else if (ds == DamageSource.wither)
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

			BukkitEntity e = BukkitEntity.getEntity(BukkitServer.instance(), ev.entity);
			if (!(e instanceof BukkitLivingEntity)) {
				e = new BukkitLivingEntity(BukkitServer.instance(), (EntityLiving) ev.entity);
			}
			CreatureSpawnEvent bev = new CreatureSpawnEvent((LivingEntity) e, SpawnReason.DEFAULT);
			Bukkit.getPluginManager().callEvent(bev);
			if (bev.isCancelled()) {
				ev.setCanceled(true);
			}

			//BukkitEventFactory.callCreatureSpawnEvent((EntityLiving) ev.entity, SpawnReason.DEFAULT);
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
		EntityDamageEvent bev;
		if (ev.source.getSourceOfDamage() != null) {
			bev = new EntityDamageByEntityEvent(
					BukkitEntity.getEntity(BukkitServer.instance(), 
							ev.source.getSourceOfDamage()), 
							BukkitEntity.getEntity(BukkitServer.instance(), 
									ev.entityLiving), 
									getDamageCause(ev.source), ev.ammount);

		}
		else {
			bev = new EntityDamageEvent(
					BukkitEntity.getEntity(BukkitServer.instance(), ev.entityLiving),
					getDamageCause(ev.source),
					ev.ammount);
		}

		Bukkit.getPluginManager().callEvent(bev);

		if (bev.isCancelled()) {
			ev.setCanceled(true);
		}
	}
	@ForgeSubscribe
	public void onLivingDeathEvent(LivingDeathEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		LivingEntity e;
		BukkitEntity j = BukkitEntity.getEntity(BukkitServer.instance(), ev.entityLiving);
		if (!(j instanceof LivingEntity)) {
			e = new BukkitLivingEntity(BukkitServer.instance(), ev.entityLiving);
		}
		else {
			e = (LivingEntity) j;
		}
		List<org.bukkit.inventory.ItemStack> stacks = new ArrayList<org.bukkit.inventory.ItemStack>();
		for (EntityItem i : ev.entityLiving.capturedDrops) {
			ItemStack vanilla = i.getEntityItem();
			stacks.add(new BukkitItemStack(vanilla));
		}
		EntityDeathEvent bev = new EntityDeathEvent(e, stacks);
		bev.setDroppedExp(ev.entityLiving.experienceValue);
		Bukkit.getPluginManager().callEvent(bev);

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
			if (ev.entityPlayer.inventory.getCurrentItem() == null) {
				return;
			}
			if (ev.entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemBlock) {
				if (BukkitContainer.DEBUG)
					System.out.println("PLACE!");
				final BukkitItemStack itemInHand = new BukkitItemStack(ev.entityPlayer.inventory.getCurrentItem());
				final int blockX = ev.x + ForgeDirection.getOrientation(ev.face).offsetX;
				final int blockY = ev.y + ForgeDirection.getOrientation(ev.face).offsetY;
				final int blockZ = ev.z + ForgeDirection.getOrientation(ev.face).offsetZ;
				EntityPlayerMP forgePlayerMP;
				if (!(ev.entityPlayer instanceof EntityPlayerMP)) {

					forgePlayerMP = BukkitContainer.MOD_PLAYER;

				}
				else {
					forgePlayerMP = (EntityPlayerMP) ev.entityPlayer;
				}

				final BukkitPlayer thePlayer = BukkitPlayerCache.getBukkitPlayer(forgePlayerMP);
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

				BlockCanBuildEvent can = new BlockCanBuildEvent(beforeBlock, beforeBlock.getTypeId(), canBuild);
				Bukkit.getPluginManager().callEvent(can);


				BukkitBlock placedBlock = new BukkitBlockFake(new BukkitChunk(ev.entity.worldObj.getChunkFromBlockCoords(ev.x, ev.y)), blockX, blockY, blockZ, itemInHand.getTypeId(), itemInHand.getDurability());
				BlockPlaceEvent bev = new BlockPlaceEvent(placedBlock, beforeBlock.getState(), placedBlock, itemInHand, thePlayer, can.isBuildable());

				Bukkit.getPluginManager().callEvent(bev);

				if (bev.isCancelled() || !bev.canBuild()) {
					ev.setCanceled(true);
				}

			}
			else if (ev.entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemFlintAndSteel) {

				// ignite
				EntityPlayerMP fp;

				if (!(ev.entityPlayer instanceof EntityPlayerMP)) {
					fp = BukkitContainer.MOD_PLAYER;
				}
				else {
					fp = (EntityPlayerMP) ev.entityPlayer;
				}

				BlockIgniteEvent bev = new BlockIgniteEvent(new BukkitBlock(new BukkitChunk(ev.entity.worldObj.getChunkFromBlockCoords(ev.x, ev.y)), ev.x, ev.y, ev.z), IgniteCause.FLINT_AND_STEEL, BukkitPlayerCache.getBukkitPlayer(fp));

				Bukkit.getPluginManager().callEvent(bev);
				if (bev.isCancelled()) {
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
		EntityPlayerMP fp;

		if (!(ev.entityPlayer instanceof EntityPlayerMP)) {
			fp = BukkitContainer.MOD_PLAYER;
		}
		else {
			fp = (EntityPlayerMP) ev.entityPlayer;
		}
		PlayerPickupItemEvent bev = new PlayerPickupItemEvent(BukkitPlayerCache.getBukkitPlayer(fp), new BukkitItem(BukkitServer.instance(), ev.item), 0);
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
		EntityPlayerMP fp;

		if (!(ev.entityPlayer instanceof EntityPlayerMP)) {
			fp = BukkitContainer.MOD_PLAYER;
		}
		else {
			fp = (EntityPlayerMP) ev.entityPlayer;
		}
		BukkitBlock blk = new BukkitBlock(new BukkitChunk(ev.entity.worldObj.getChunkFromBlockCoords(ev.target.blockX, ev.target.blockZ)), ev.target.blockX, ev.target.blockY, ev.target.blockZ);
		PlayerBucketFillEvent bev = new PlayerBucketFillEvent(BukkitPlayerCache.getBukkitPlayer(fp), blk, BukkitBlock.notchToBlockFace(ev.target.sideHit), Material.BUCKET, new BukkitItemStack(ev.result));
		Bukkit.getPluginManager().callEvent(bev);
		if (bev.isCancelled()) {
			ev.setCanceled(true);
			ev.setResult(Result.DENY);
		}
	}

	@ForgeSubscribe(receiveCanceled=true)
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

		EntityPlayerMP fp;

		if (!(ev.entityPlayer instanceof EntityPlayerMP)) {
			fp = BukkitContainer.MOD_PLAYER;
		}
		else {
			fp = (EntityPlayerMP) ev.entityPlayer;
		}

		BukkitBlock bb = new BukkitBlock(new BukkitChunk(ev.entity.worldObj.getChunkFromBlockCoords(ev.x, ev.z)), ev.x,ev.y,ev.z);
		BlockFace face = BukkitBlock.notchToBlockFace(ev.face);
		org.bukkit.event.player.PlayerInteractEvent bev = 
				new org.bukkit.event.player.PlayerInteractEvent(BukkitPlayerCache.getBukkitPlayer(fp), act, new BukkitItemStack(ev.entityPlayer.inventory.getCurrentItem()), bb, face);

		Bukkit.getPluginManager().callEvent(bev);

		if (bev.isCancelled()) {
			ev.setCanceled(true);
			//ev.setResult(Result.DENY);
		}

		//BukkitEventFactory.callPlayerInteractEvent((EntityPlayerMP) ev.entityPlayer, act, ev.entityPlayer.inventory.getCurrentItem());

	}

	@ForgeSubscribe
	public void playerGoToSleep(PlayerSleepInBedEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		org.bukkit.event.player.PlayerBedEnterEvent bev = new PlayerBedEnterEvent(BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) ev.entityPlayer), new BukkitBlock(new BukkitChunk(ev.entityPlayer.worldObj.getChunkFromBlockCoords(ev.x, ev.z)), ev.x, ev.y, ev.z));

		Bukkit.getPluginManager().callEvent(bev);

		if (bev.isCancelled()) {
			ev.result = EnumStatus.OTHER_PROBLEM;
		}
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
		BukkitPlayer whom = BukkitPlayerCache.getBukkitPlayer(ev.player);

		AsyncPlayerChatEvent ev1 = new AsyncPlayerChatEvent(false, whom, ev.message, Sets.newHashSet(BukkitServer.instance().getOnlinePlayers()));
		ev1 = BukkitEventFactory.callEvent(ev1);
		String newLine = String.format(ev1.getFormat(),new Object[] {newName, ev1.getMessage()});
		ev.line = newLine;
		if (ev1.isCancelled()) {
			ev.setCanceled(true);
		}
	}

	@ForgeSubscribe
	public void saplingGrow(SaplingGrowTreeEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		int blockID = ev.world.getBlockId(ev.x, ev.y, ev.z);
		//int blockMeta = ev.world.getBlockMetadata(ev.x, ev.y, ev.z);

		if (Block.blocksList[blockID] == Block.sapling) {
			TreeType type = TreeType.TREE;


			//StructureGrowEvent bev = 
					new StructureGrowEvent(new Location(BukkitServer.instance().getWorld(ev.world.getWorldInfo().getDimension()),ev.x,ev.y,ev.z), type, false, null, new ArrayList<BlockState>());
		}
	}

	/*@ForgeSubscribe
	public void worldLoad(WorldEvent.Load ev) {
		if (!ForgeEventHandler.ready || FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;

			WorldLoadEvent bev = new WorldLoadEvent(bukkit);
			Bukkit.getPluginManager().callEvent(bev);
		}



	}*/
	
	// TODO: This breaks commands for some reason.
    //@ForgeSubscribe
	public void serverCmd(CommandEvent ev) {
		if (ev.sender instanceof EntityPlayerMP) {
			PlayerCommandPreprocessEvent bev=new PlayerCommandPreprocessEvent(BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) ev.sender), "/" + ev.command.getCommandName() + " " + Joiner.on(' ').join(ev.parameters));
			Bukkit.getPluginManager().callEvent(bev);
			String[] msg = bev.getMessage().split(" ");
			ev.parameters = Arrays.copyOfRange(msg, 1, msg.length);
			if (bev.isCancelled()) {
				ev.setCanceled(true);
				return;
			}
		}
		
		CommandSender s;
		if (ev.sender instanceof EntityPlayerMP) s = BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP)ev.sender);
		else s = Bukkit.getConsoleSender();
		
		ServerCommandEvent bev = new ServerCommandEvent(s, ev.command.getCommandName() + " " + Joiner.on(' ').join(ev.parameters));
		Bukkit.getPluginManager().callEvent(bev);
	}
	
	// begin BukkitForge-added events

	// used PlayerInteractEvent for this
	/*@ForgeSubscribe
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
	}*/

	@ForgeSubscribe
	public void dispenseItem(DispenseItemEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		ItemStack item = ev.stackToDispense.copy();
		item.stackSize = 1;
		//IRegistry dispenserRegistry = BlockDispenser.dispenseBehaviorRegistry;
		//IBehaviorDispenseItem theBehaviour = (IBehaviorDispenseItem) dispenserRegistry.func_82594_a(item.getItem());
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
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		BlockDamageEvent bev = new BlockDamageEvent(BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP) ev.entityPlayer), 
				new BukkitBlock(new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.blockX, ev.blockZ)), ev.blockX, ev.blockY, ev.blockZ),
				new BukkitItemStack(ev.entityPlayer.inventory.getCurrentItem()), 
				((EntityPlayerMP) ev.entityPlayer).capabilities.isCreativeMode);
		Bukkit.getPluginManager().callEvent(bev);
		if (bev.isCancelled()) {
			ev.setCanceled(true);
			return;
		}
		if (bev.getInstaBreak()) {
			Block blck = Block.blocksList[ev.blockID];
			blck.dropBlockAsItem(ev.world, ev.blockX, ev.blockY, ev.blockZ, ev.world.getBlockMetadata(ev.blockX, ev.blockY, ev.blockZ), 0);
			ev.world.setBlockAndMetadata(ev.blockX, ev.blockY, ev.blockZ, 0, 0);
			//Block.blocksList[ev.blockID].breakBlock(ev.world, ev.blockX, ev.blockY, ev.blockZ, ev.blockID, ev.world.getBlockMetadata(ev.blockX, ev.blockY, ev.blockZ));
			return;
		}

	}

	@ForgeSubscribe
	public void blockBreakSomehow(BlockDestroyEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		BlockBreakEvent bev = new BlockBreakEvent(new BukkitBlock(new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.x, ev.y)), ev.x, ev.y, ev.z), BukkitPlayerCache.getBukkitPlayer(BukkitContainer.MOD_PLAYER));
		Bukkit.getPluginManager().callEvent(bev);

		if (bev.isCancelled()) {
			ev.setCanceled(true);
		}
		//ignore XP etc
	}

	@ForgeSubscribe
	public void onCreeperExplode(CreeperExplodeEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		int x = MathHelper.floor_double(ev.creeper.posX);
		int y = MathHelper.floor_double(ev.creeper.posY);
		int z = MathHelper.floor_double(ev.creeper.posZ);
		
		int minX = x - ev.explosionRadius;
		int maxX = x + ev.explosionRadius;
		int minY = y - ev.explosionRadius;
		int maxY = y + ev.explosionRadius;
		int minZ = z - ev.explosionRadius;
		int maxZ = z + ev.explosionRadius;
		
		List<org.bukkit.block.Block> blocks = new ArrayList<org.bukkit.block.Block>();
		
		for (x = minX; x <= maxX; x++) {
			for (y = minY; y <= maxY; y++) {
				for (z = minZ; z <= maxZ; z++) {
					BukkitBlock b = new BukkitBlock(
							new BukkitChunk(ev.creeper.worldObj.getChunkFromBlockCoords(x, z)),
							x,y,z);
					blocks.add(b);
					
				}
			}
		}
		
		
		//AxisAlignedBB blocks = AxisAlignedBB.getBoundingBox(x - ev.explosionRadius, y - ev.explosionRadius, z - ev.explosionRadius, x + ev.explosionRadius, y + ev.explosionRadius, z + ev.explosionRadius);
		//world.block
		Location loc = new Location(BukkitServer.instance().getWorld(ev.creeper.worldObj.getWorldInfo().getDimension()), ev.creeper.posX, ev.creeper.posY, ev.creeper.posZ);
		EntityExplodeEvent bev = new EntityExplodeEvent(new BukkitCreeper(BukkitServer.instance(), ev.creeper), loc, blocks, 1.0f);
		Bukkit.getPluginManager().callEvent(bev);
		if (bev.isCancelled()) {
			ev.setCanceled(true);
		}
		
	}
	
	@ForgeSubscribe
	public void liquidFlow(LiquidFlowEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		BukkitBlockFake newBlk = new BukkitBlockFake(
				new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.flowToX, ev.flowToZ)), 
				ev.flowToX, 
				ev.flowToY,
				ev.flowToZ, 
				ev.liquid.blockID + 1,
				0
				);

		BukkitBlock source = new BukkitBlock(				
				new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.flowFromX, ev.flowFromZ)), 
				ev.flowFromX, 
				ev.flowFromY,
				ev.flowFromZ
				);

		BlockSpreadEvent bev = new BlockSpreadEvent(newBlk, source, newBlk.getState());
		Bukkit.getPluginManager().callEvent(bev);
		
		if (bev.isCancelled()) {
			ev.setCanceled(true);
		}
	}

	@ForgeSubscribe
	public void onSheepDye(SheepDyeEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		SheepDyeWoolEvent bev = new SheepDyeWoolEvent(new BukkitSheep(BukkitServer.instance(), ev.sheep), DyeColor.getByData((byte)ev.newColour));

		Bukkit.getPluginManager().callEvent(bev);

		if (bev.isCancelled()) {
			ev.setCanceled(true);
		}

	}

	@ForgeSubscribe
	public void onPlayerMove(PlayerMoveEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		if (!(ev.entityPlayer instanceof EntityPlayerMP)) return;
		BukkitPlayer player = BukkitPlayerCache.getBukkitPlayer(BukkitServer.instance(), (EntityPlayerMP) ev.entityPlayer);
		Location old = new Location(BukkitServer.instance().getWorld(ev.entityPlayer.worldObj.getWorldInfo().getDimension()), ev.oldX, ev.oldY, ev.oldZ);
		Location now = new Location(BukkitServer.instance().getWorld(ev.entityPlayer.worldObj.getWorldInfo().getDimension()), ev.newX, ev.newY, ev.newZ);
		org.bukkit.event.player.PlayerMoveEvent bev = new org.bukkit.event.player.PlayerMoveEvent(player, old, now);
		
		/*if (old.equals(now)) {
			return;
		}*/
		if (BukkitContainer.DEBUG){
			System.out.println(ev.entityPlayer.username + ": " + old + " => " + now);
		}
		
		Bukkit.getPluginManager().callEvent(bev);
		if (bev.isCancelled()) {
			ev.setCanceled(true);
		}

	}

	@ForgeSubscribe
	public void onPressurePlate(PressurePlateInteractEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		if (ev.entity instanceof EntityPlayerMP) {
			EntityPlayerMP fp = (EntityPlayerMP) ev.entity;
			BukkitPlayer player = BukkitPlayerCache.getBukkitPlayer(BukkitServer.instance(), (EntityPlayerMP) ev.entity);

			BukkitItemStack item = new BukkitItemStack(fp.inventory.getCurrentItem());
			org.bukkit.event.player.PlayerInteractEvent bev = new org.bukkit.event.player.PlayerInteractEvent(player, Action.PHYSICAL, item, new BukkitBlock(new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.x, ev.z)), ev.x,ev.y,ev.z), BukkitBlock.notchToBlockFace(-1));
			Bukkit.getPluginManager().callEvent(bev);
			if (bev.isCancelled()) {
				ev.setCanceled(true);
			}
		}
	}

	@ForgeSubscribe
	public void onLightningStrike(LightningStrikeEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		if (ev.bolt != null) {

			org.bukkit.event.weather.LightningStrikeEvent bev1 = new org.bukkit.event.weather.LightningStrikeEvent(BukkitServer.instance().getWorld(ev.world.getWorldInfo().getDimension()), new BukkitLightningStrike(BukkitServer.instance(), ev.bolt));
			Bukkit.getPluginManager().callEvent(bev1);
			if (bev1.isCancelled()) {
				cancelled.add(ev.bolt);
				ev.setCanceled(true);
				return;
			}
		}
		if (cancelled.contains(ev.bolt)) {
			// same as before
			ev.setCanceled(true);
			return;
		}
		
		//BlockIgniteEvent bev = 
				new BlockIgniteEvent(new BukkitBlock(new BukkitChunk(ev.world.getChunkFromBlockCoords(ev.x, ev.z)), ev.x, ev.y, ev.z), IgniteCause.LIGHTNING, null);
	}
	
        @ForgeSubscribe
	public void onSignChange(SignChangeEvent ev) {
		if (!ready|| FMLCommonHandler.instance().getEffectiveSide().isClient())
			return;
		BukkitBlock theBlock = new BukkitBlock(
					new BukkitChunk(ev.signChanger.worldObj.getChunkFromBlockCoords(ev.x, ev.z)),
					ev.x,
					ev.y,
					ev.z
				);
		BukkitPlayer thePlayer;
		if (ev.signChanger instanceof EntityPlayerMP)
		thePlayer = BukkitPlayerCache.getBukkitPlayer((EntityPlayerMP)ev.signChanger);
		
		else thePlayer = BukkitPlayerCache.getBukkitPlayer(BukkitContainer.MOD_PLAYER);
		
		org.bukkit.event.block.SignChangeEvent bev = new org.bukkit.event.block.SignChangeEvent(theBlock, thePlayer, ev.lines);
		
		Bukkit.getPluginManager().callEvent(bev);
		
		if (bev.isCancelled()) {
			ev.setCanceled(true);
		}
		
		ev.lines = bev.getLines();
		
	}
	
	private List<EntityLightningBolt> cancelled = new ArrayList<EntityLightningBolt>();
}

