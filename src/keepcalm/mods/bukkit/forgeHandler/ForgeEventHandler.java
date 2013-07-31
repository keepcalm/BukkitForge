package keepcalm.mods.bukkit.forgeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import keepcalm.mods.bukkit.*;
import keepcalm.mods.bukkitforge.BukkitForgePlayerCache;
import keepcalm.mods.events.forgeex.BlockDestroyEvent;
import keepcalm.mods.events.forgeex.CreeperExplodeEvent;
import keepcalm.mods.events.forgeex.DispenseItemEvent;
import keepcalm.mods.events.forgeex.LightningStrikeEvent;
import keepcalm.mods.events.forgeex.LiquidFlowEvent;
import keepcalm.mods.events.forgeex.PlayerDamageBlockEvent;
import keepcalm.mods.events.forgeex.PlayerMoveEvent;
import keepcalm.mods.events.forgeex.PlayerUseItemEvent;
import keepcalm.mods.events.forgeex.PressurePlateInteractEvent;
import keepcalm.mods.events.forgeex.SheepDyeEvent;
import keepcalm.mods.events.forgeex.SignChangeEvent;
import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
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
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.ChunkCoordinates;
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
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_6_R2.CraftServer;
import org.bukkit.craftbukkit.v1_6_R2.block.CraftBlock;
import org.bukkit.craftbukkit.v1_6_R2.block.CraftBlockFake;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftCreeper;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftItem;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLightningStrike;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftSheep;
import org.bukkit.craftbukkit.v1_6_R2.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_6_R2.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
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
	
	private List<EntityLightningBolt> cancelled = new ArrayList<EntityLightningBolt>();
	
	public static boolean ready = false;
	
	private final boolean isClient = FMLCommonHandler.instance().getEffectiveSide().isClient();

	@ForgeSubscribe(receiveCanceled = true)
	public void onEntityJoinWorld(EntityJoinWorldEvent ev) {

		if (!ready || isClient) return;
		if (ev.entity instanceof EntityLiving && !(ev.entity instanceof EntityPlayer)) {// || ev.entity instanceof EntityPlayerMP) {

			ev.setCanceled(BukkitEventRouters.Entity.CreatureSpawn.callEvent(ev.isCanceled(), ToBukkitEvent.CreatureSpawn(ev)).isCancelled());
		}
	}
	
	@ForgeSubscribe(receiveCanceled = true)
	public void onItemExpire(ItemExpireEvent ev) {

		if (!ready || isClient) return;
        BukkitEventRouters.Entity.ItemDespawn.callEvent(ev.isCanceled(), ToBukkitEvent.ItemDespawn(ev));
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void onItemTossEvent(ItemTossEvent ev) {

		if (!ready || isClient) return;
        BukkitEventRouters.Entity.ItemSpawn.callEvent(ev.isCanceled(), ToBukkitEvent.ItemSpawn(ev));
	}
	
	@ForgeSubscribe(receiveCanceled = true)
	public void onLivingAttack(LivingAttackEvent ev) {
		if (!ready || isClient)
			return;
		EntityDamageEvent bev;
		if (ev.source.getSourceOfDamage() != null) {
			bev = new EntityDamageByEntityEvent(
					CraftEntity.getEntity(CraftServer.instance(), 
							ev.source.getSourceOfDamage()), 
							CraftEntity.getEntity(CraftServer.instance(), 
									ev.entityLiving), 
									getDamageCause(ev.source), ev.ammount);

		} else {
			bev = new EntityDamageEvent(
					CraftEntity.getEntity(CraftServer.instance(), ev.entityLiving),
					getDamageCause(ev.source),
					ev.ammount);
		}
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);

		ev.setCanceled(bev.isCancelled());
	}

    private DamageCause getDamageCause(DamageSource source) {
        return ToBukkit.damageCause(source);
    }

    @ForgeSubscribe(receiveCanceled = true)
	public void onLivingDeathEvent(LivingDeathEvent ev) {
		if (!ready || isClient)
			return;
		LivingEntity e;
		CraftEntity j = CraftEntity.getEntity(CraftServer.instance(), ev.entityLiving);
		if (!(j instanceof LivingEntity)) {
			e = new CraftLivingEntity(CraftServer.instance(), ev.entityLiving);
		} else {
			e = (LivingEntity) j;
		}
		
		List<org.bukkit.inventory.ItemStack> stacks = new ArrayList<org.bukkit.inventory.ItemStack>();
		
		for (EntityItem i : ev.entityLiving.capturedDrops) {
			ItemStack vanilla = i.getEntityItem();
			stacks.add(new CraftItemStack(vanilla));
		}
		
		EntityDeathEvent bev = new EntityDeathEvent(e, stacks);
		
		bev.setDroppedExp(ev.entityLiving.getExpReward());
		Bukkit.getPluginManager().callEvent(bev);
	}
	
	/*@ForgeSubscribe(receiveCanceled = true)
	public void onLivingFall(LivingFallEvent ev) {
		CraftEventFactory.callE
	}*/
	
	@ForgeSubscribe(receiveCanceled = true)
	public void onLivingDamage(LivingHurtEvent ev) {
		if (!ready || isClient)
			return;
		DamageCause dc = getDamageCause(ev.source);

		CraftEventFactory.callEntityDamageEvent(ev.source.getEntity(), ev.entity, dc, ev.ammount);
	}
	
	@ForgeSubscribe(receiveCanceled = true)
	public void onTarget(LivingSetAttackTargetEvent ev) {
		if (!ready || isClient)
			return;
		CraftEventFactory.callEntityTargetEvent(ev.entity, ev.target, TargetReason.CUSTOM);
	}
	
	/*@ForgeSubscribe(receiveCanceled = true)
	public void onCartCollide(MinecartCollisionEvent ev) {
		CraftEventFactory.
	}*/
	
/*	@ForgeSubscribe(receiveCanceled = true)
	*//**
	 * Only called when a player fires
	 * Forge doesn't give us the EntityArrow.
	 * @param ev
	 *//*
	public void bowFire(ArrowLooseEvent ev) {
		if (!ready || isClient)
			return;
		CraftEventFactory.callEntityShootBowEvent(ev.entityPlayer, ev.bow, (EntityArrow)ev.entity, ev.charge);
	}*/

	@ForgeSubscribe(receiveCanceled = true)
	public void playerVEntity(AttackEntityEvent ev) {
		if (!ready || isClient)
			return;
		CraftEventFactory.callEntityDamageEvent(ev.entityPlayer, ev.target, DamageCause.ENTITY_ATTACK, 1F);
	}
	
/*	@ForgeSubscribe(receiveCanceled = true)
	public void bonemeal(BonemealEvent ev) {
		
	}*/
	
	@ForgeSubscribe(receiveCanceled = true)
	public void onPlayerInteraction(final PlayerInteractEvent ev) {
		if (!ready || isClient)
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
				final CraftItemStack itemInHand = new CraftItemStack(ev.entityPlayer.inventory.getCurrentItem());
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

				final CraftPlayer thePlayer = BukkitForgePlayerCache.getCraftPlayer(forgePlayerMP);
				final CraftBlock beforeBlock = new CraftBlock(new CraftChunk(ev.entity.worldObj.getChunkFromBlockCoords(blockX, blockZ)), blockX, blockY, blockZ);
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
				if (aabb.isVecInside(Vec3.fakePool.getVecFromPool(blockX, blockY, blockZ))) {
					canBuild = false;
				} else {
					canBuild = true;
				}



				BlockCanBuildEvent can = new BlockCanBuildEvent(beforeBlock, beforeBlock.getTypeId(), canBuild);
				
				can.setBuildable(!ev.isCanceled());
				
				Bukkit.getPluginManager().callEvent(can);

				final CraftBlock placedBlock = new CraftBlockFake(new CraftChunk(ev.entity.worldObj.getChunkFromBlockCoords(ev.x, ev.y)), blockX, blockY, blockZ, itemInHand.getTypeId(), itemInHand.getDurability());

                BlockPlaceEvent bev = new BlockPlaceEvent(placedBlock, beforeBlock.getState(), placedBlock, itemInHand, thePlayer, can.isBuildable());
				
				bev.setCancelled(ev.isCanceled());
				
				Bukkit.getPluginManager().callEvent(bev);

				ev.setCanceled(bev.isCancelled() || !bev.canBuild());
					
			} else if (ev.entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemFlintAndSteel) {

				// ignite
				EntityPlayerMP fp;

				if (!(ev.entityPlayer instanceof EntityPlayerMP)) {
					fp = BukkitContainer.MOD_PLAYER;
				} else {
					fp = (EntityPlayerMP) ev.entityPlayer;
				}

				BlockIgniteEvent bev = new BlockIgniteEvent(new CraftBlock(new CraftChunk(ev.entity.worldObj.getChunkFromBlockCoords(ev.x, ev.y)), ev.x, ev.y, ev.z), IgniteCause.FLINT_AND_STEEL, BukkitForgePlayerCache.getCraftPlayer(fp));
				bev.setCancelled(ev.isCanceled());
				Bukkit.getPluginManager().callEvent(bev);
				ev.setCanceled(bev.isCancelled());
				
			}
			
		}
		
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void pickUp(EntityItemPickupEvent ev) {
		if (!ready || isClient)
			return;
		// assume all picked up at the same time
		EntityPlayerMP fp;

		if (!(ev.entityPlayer instanceof EntityPlayerMP)) {
			fp = BukkitContainer.MOD_PLAYER;
		} else {
			fp = (EntityPlayerMP) ev.entityPlayer;
		}
		PlayerPickupItemEvent bev = new PlayerPickupItemEvent(BukkitForgePlayerCache.getCraftPlayer(fp), new CraftItem(CraftServer.instance(), ev.item), 0);
		bev.setCancelled(ev.entityLiving.captureDrops);
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);
		if (bev.isCancelled()) {
			ev.setCanceled(true);
			ev.setResult(Result.DENY);
		}
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void fillCraft(FillBucketEvent ev) {
		if (!ready || isClient)
			return;
		EntityPlayerMP fp;

		if (!(ev.entityPlayer instanceof EntityPlayerMP)) {
			fp = BukkitContainer.MOD_PLAYER;
		}
		else {
			fp = (EntityPlayerMP) ev.entityPlayer;
		}
		CraftBlock blk = new CraftBlock(new CraftChunk(ev.entity.worldObj.getChunkFromBlockCoords(ev.target.blockX, ev.target.blockZ)), ev.target.blockX, ev.target.blockY, ev.target.blockZ);
		PlayerBucketFillEvent bev = new PlayerBucketFillEvent(BukkitForgePlayerCache.getCraftPlayer(fp), blk, CraftBlock.notchToBlockFace(ev.target.sideHit), Material.BUCKET, new CraftItemStack(ev.result));
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);
		if (bev.isCancelled()) {
			ev.setCanceled(true);
			ev.setResult(Result.DENY);
		}
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void interactEvent(PlayerInteractEvent ev) {
		if (!ready || isClient)
			return;

        org.bukkit.event.player.PlayerInteractEvent bev = BukkitEventRouters.Player.PlayerInteract.callEvent(ev.isCanceled(), ToBukkitEvent.PlayerInteract(ev));

		if (bev.isCancelled()) {
			ev.setCanceled(true);
			ev.setResult(Result.DENY);
		}
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void playerGoToSleep(PlayerSleepInBedEvent ev) {
		if (!ready || isClient)
			return;
		org.bukkit.event.player.PlayerBedEnterEvent bev = new PlayerBedEnterEvent(BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) ev.entityPlayer), new CraftBlock(new CraftChunk(ev.entityPlayer.worldObj.getChunkFromBlockCoords(ev.x, ev.z)), ev.x, ev.y, ev.z));
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);

		if (bev.isCancelled()) {
			ev.setCanceled(true);
			
			ev.result = EnumStatus.OTHER_PROBLEM;
		}
	}
	@ForgeSubscribe(receiveCanceled = true)
	public void chunkLoadEvent(ChunkEvent.Load ev) {
		if (!ready || isClient)
			return;

        BukkitEventRouters.World.ChunkLoad.callEvent( false, ToBukkitEvent.ChunkLoad(ev) );
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void chunkUnloadEvent(ChunkEvent.Unload ev) {
		if (!ready || isClient)
			return;

        BukkitEventRouters.World.ChunkUnload.callEvent(false, ToBukkitEvent.ChunkUnload(ev));
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void serverChat(ServerChatEvent ev) {
		if (!ready)
			return;
		String newName = ev.player.username;
		if (playerDisplayNames.containsKey(newName)) {
			newName = playerDisplayNames.get(newName);
		}
		CraftPlayer whom = BukkitForgePlayerCache.getCraftPlayer(ev.player);

		AsyncPlayerChatEvent ev1 = new AsyncPlayerChatEvent(false, whom, ev.message, Sets.newHashSet(CraftServer.instance().getOnlinePlayers()));
		PlayerChatEvent bev = new PlayerChatEvent(whom, ev.message);
		bev.setCancelled(ev.isCanceled());
		ev1.setCancelled(ev.isCanceled());
		ev1 = CraftEventFactory.callEvent(ev1);
		bev.setCancelled(ev1.isCancelled());
		bev = CraftEventFactory.callEvent(bev);
		
		String newLine = String.format(ev1.getFormat(), new Object[] { 
			newName, 
			ev1.getMessage()
		});
		
		ev.component = ChatMessageComponent.func_111066_d(newLine);
		
		ev.setCanceled(ev1.isCancelled() || bev.isCancelled());
		
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void saplingGrow(SaplingGrowTreeEvent ev) {
		
		
		if (!ready || isClient)
			return;
		
		int blockID = ev.world.getBlockId(ev.x, ev.y, ev.z);
		
		//int blockMeta = ev.world.getBlockMetadata(ev.x, ev.y, ev.z);

		if (Block.blocksList[blockID] == Block.sapling) {
			TreeType type = TreeType.TREE;


			StructureGrowEvent bev = new StructureGrowEvent(new Location(CraftServer.instance().getWorld(ev.world.provider.dimensionId),ev.x,ev.y,ev.z), type, false, null, new ArrayList<BlockState>());
			bev.setCancelled(ev.isCanceled());
			Bukkit.getPluginManager().callEvent(bev);
			ev.setCanceled((bev.isCancelled()));
			
		}
		
		
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void serverCmd(CommandEvent ev) {
		CommandSender s;
		
		/*
		 * TODO: Impelement more of these
		 * EntityClientPlayerMP,
		 * EntityOtherPlayerMP, 
		 * EntityPlayer, 
		 * EntityPlayerMP, 
		 * IntegratedServer, 
		 * MinecraftServer, 
		 * RConConsoleSource, 
		 * TileEntityCommandBlock
		 * 
		 */

		ICommandSender sender = ev.sender;
		
		if (sender instanceof EntityPlayerMP) {
			s = BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) ev.sender);

		} else {
			s = Bukkit.getConsoleSender();
		}

		if (ev.isCanceled()) {
			return;
		}

		ServerCommandEvent bev = new ServerCommandEvent(s, (ev.command.getCommandName() + " " + Joiner.on(' ').join(ev.parameters)).trim());
		
		Bukkit.getPluginManager().callEvent(bev);
	}

    // used PlayerInteractEvent for this
	@ForgeSubscribe(receiveCanceled = true)
	public void tryPlaceBlock(PlayerUseItemEvent ev) {
		if (ev.stack.getItem() instanceof ItemBlock) {
			ItemBlock block = (ItemBlock) ev.stack.getItem();
			CraftChunk chunk = new CraftChunk(ev.world.getChunkFromBlockCoords(ev.x, ev.z));
			ChunkCoordinates spawn = ev.world.getSpawnPoint();
			int spawnRadius = CraftServer.instance().getSpawnRadius();
			boolean canBuild = AxisAlignedBB.getAABBPool().getAABB(spawn.posX, spawn.posY, spawn.posZ, spawn.posX + spawnRadius, spawn.posY + spawnRadius, spawn.posZ + spawnRadius).isVecInside(Vec3.createVectorHelper(ev.x, ev.y, ev.z));
			CraftBlock bblock = new CraftBlock(chunk, ev.x, ev.y, ev.z);
			BlockCanBuildEvent bukkitEv = new BlockCanBuildEvent(bblock, block.getBlockID(), canBuild);

			bukkitEv.setBuildable(ev.isCanceled());
			if (!bukkitEv.isBuildable() && canBuild) {
				// it was changed
				// and since we were called from AFTER the actual placement, we can just break the block.
				bblock.breakNaturally();
			}
		}
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void dispenseItem(DispenseItemEvent ev) {
		
		if (!ready || isClient)
			return;
		
		ItemStack item = ev.stackToDispense.copy();
		
		item.stackSize = 1;
		//IRegistry dispenserRegistry = BlockDispenser.dispenseBehaviorRegistry;
		//IBehaviorDispenseItem theBehaviour = (IBehaviorDispenseItem) dispenserRegistry.func_82594_a(item.getItem());

		BlockDispenseEvent bev = new BlockDispenseEvent(
				new CraftBlock(
						new CraftChunk(ev.blockWorld.getChunkFromBlockCoords(ev.blockX, ev.blockZ)),
						ev.blockX, ev.blockY, ev.blockZ), new CraftItemStack(item), new Vector());
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);
		
		ev.setCanceled(bev.isCancelled());
		
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void playerDamageBlock(PlayerDamageBlockEvent ev) {
		if (!ready || isClient)
			return;
		BlockDamageEvent bev = new BlockDamageEvent(BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) ev.entityPlayer),
				new CraftBlock(new CraftChunk(ev.world.getChunkFromBlockCoords(ev.blockX, ev.blockZ)), ev.blockX, ev.blockY, ev.blockZ),
				new CraftItemStack(ev.entityPlayer.inventory.getCurrentItem()), 
				((EntityPlayerMP) ev.entityPlayer).capabilities.isCreativeMode);
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);
		if (bev.isCancelled()) {
			ev.setCanceled(true);
			return;
		}
		if (bev.getInstaBreak()) {
			Block blck = Block.blocksList[ev.blockID];
			blck.dropBlockAsItem(ev.world, ev.blockX, ev.blockY, ev.blockZ, ev.world.getBlockMetadata(ev.blockX, ev.blockY, ev.blockZ), 0);
			ev.world.setBlockMetadataWithNotify(ev.blockX, ev.blockY, ev.blockZ, 0, 0);
			//Block.blocksList[ev.blockID].breakBlock(ev.world, ev.blockX, ev.blockY, ev.blockZ, ev.blockID, ev.world.getBlockMetadata(ev.blockX, ev.blockY, ev.blockZ));
			return;
		}

	}

	@ForgeSubscribe(receiveCanceled = true)
	public void blockBreakSomehow(BlockDestroyEvent ev) {

        // This event is breaking something with an 'already decorating' error
		
		if (!ready || isClient)
			return;
		
		/*BlockBreakEvent bev = new BlockBreakEvent(new CraftBlock(new CraftChunk(ev.world.getChunkFromBlockCoords(ev.x, ev.y)), ev.x, ev.y, ev.z), BukkitForgePlayerCache.getCraftPlayer(BukkitContainer.MOD_PLAYER));
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);
		
		ev.setCanceled(bev.isCancelled());  */
		
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void onCreeperExplode(CreeperExplodeEvent ev) {
		if (!ready || isClient)
			return;
		int x = MathHelper.floor_double(ev.creeper.posX);
		int y = MathHelper.floor_double(ev.creeper.posY);
		int z = MathHelper.floor_double(ev.creeper.posZ);
		
		int minX = x - ev.explosionRadius; // Shouldnt you use Math.max and Math.min?
		int maxX = x + ev.explosionRadius;
		int minY = y - ev.explosionRadius;
		int maxY = y + ev.explosionRadius;
		int minZ = z - ev.explosionRadius;
		int maxZ = z + ev.explosionRadius;
		
		List<org.bukkit.block.Block> blocks = new ArrayList<org.bukkit.block.Block>();
		
		for (x = minX; x <= maxX; x++) {
			for (z = minZ; z <= maxZ; z++) {
				CraftChunk chunk = new CraftChunk(ev.creeper.worldObj.getChunkFromBlockCoords(x, z));
				for (y = minY; y <= maxY; y++) {
					CraftBlock b = new CraftBlock(chunk, x, y, z);
					blocks.add(b);
				}
			}
		}
		
		Location loc = new Location(CraftServer.instance().getWorld(ev.creeper.worldObj.provider.dimensionId), ev.creeper.posX, ev.creeper.posY, ev.creeper.posZ);
		EntityExplodeEvent bev = new EntityExplodeEvent(new CraftCreeper(CraftServer.instance(), ev.creeper), loc, blocks, 1.0f);
		
		bev.setCancelled(ev.isCanceled());
		
		Bukkit.getPluginManager().callEvent(bev);
		ev.setCanceled(bev.isCancelled());
	}
	
	@ForgeSubscribe(receiveCanceled = true)
	public void liquidFlow(LiquidFlowEvent ev) {
		if (!ready || isClient)
			return;
		CraftBlockFake newBlk = new CraftBlockFake(new CraftChunk(ev.world.getChunkFromBlockCoords(ev.flowToX, ev.flowToZ)), ev.flowToX, ev.flowToY, ev.flowToZ, ev.liquid.blockID + 1, 0);

		CraftBlock source = new CraftBlock(new CraftChunk(ev.world.getChunkFromBlockCoords(ev.flowFromX, ev.flowFromZ)), ev.flowFromX, ev.flowFromY, ev.flowFromZ);

		BlockSpreadEvent bev = new BlockSpreadEvent(newBlk, source, newBlk.getState());
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);
		
		ev.setCanceled(bev.isCancelled());
		
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void onSheepDye(SheepDyeEvent ev) {
		
		if (!ready || isClient)
			return;
		
		SheepDyeWoolEvent bev = new SheepDyeWoolEvent(new CraftSheep(CraftServer.instance(), ev.sheep), DyeColor.getByData((byte)ev.newColour));
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);
		
		ev.setCanceled(bev.isCancelled());
		
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void onPlayerMove(PlayerMoveEvent ev) {
		
		if (!ready || isClient)
			return;
		
		if (!(ev.entityPlayer instanceof EntityPlayerMP)) 
			return;
		
		CraftPlayer player = BukkitForgePlayerCache.getCraftPlayer(CraftServer.instance(), (EntityPlayerMP) ev.entityPlayer);
		
		Location old = new Location(CraftServer.instance().getWorld(ev.entityPlayer.worldObj.provider.dimensionId), ev.oldX, ev.oldY, ev.oldZ);
		
		Location now = new Location(CraftServer.instance().getWorld(ev.entityPlayer.worldObj.provider.dimensionId), ev.newX, ev.newY, ev.newZ);

		org.bukkit.event.player.PlayerMoveEvent bev = new org.bukkit.event.player.PlayerMoveEvent(player, old, now);
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);

        Location to = bev.getTo();

        if( to != now && !bev.isCancelled() )
        {
            player.teleport(to, PlayerTeleportEvent.TeleportCause.UNKNOWN);
        }

		ev.setCanceled(bev.isCancelled());
		
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void onPressurePlate(PressurePlateInteractEvent ev) {
		if (!ready || isClient)
			return;
		if (ev.entity instanceof EntityPlayerMP) {
			EntityPlayerMP fp = (EntityPlayerMP) ev.entity;
			CraftPlayer player = BukkitForgePlayerCache.getCraftPlayer(CraftServer.instance(), (EntityPlayerMP) ev.entity);

			CraftItemStack item = new CraftItemStack(fp.inventory.getCurrentItem());
			org.bukkit.event.player.PlayerInteractEvent bev = new org.bukkit.event.player.PlayerInteractEvent(player, Action.PHYSICAL, item, new CraftBlock(new CraftChunk(ev.world.getChunkFromBlockCoords(ev.x, ev.z)), ev.x,ev.y,ev.z), CraftBlock.notchToBlockFace(-1));
			bev.setCancelled(ev.isCanceled());
			Bukkit.getPluginManager().callEvent(bev);
			ev.setCanceled(bev.isCancelled());
		}
	}

	@ForgeSubscribe(receiveCanceled = true)
	public void onLightningStrike(LightningStrikeEvent ev) {
		if (!ready || isClient)
			return;
		if (ev.bolt != null) {

			org.bukkit.event.weather.LightningStrikeEvent bev1 = new org.bukkit.event.weather.LightningStrikeEvent(CraftServer.instance().getWorld(ev.world.provider.dimensionId), new CraftLightningStrike(CraftServer.instance(), ev.bolt));
			bev1.setCancelled(ev.isCanceled());
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
		
		BlockIgniteEvent bev = new BlockIgniteEvent(new CraftBlock(new CraftChunk(ev.world.getChunkFromBlockCoords(ev.x, ev.z)), ev.x, ev.y, ev.z), IgniteCause.LIGHTNING, null);
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);
		
		ev.setCanceled(bev.isCancelled());
		
	}
	
    @ForgeSubscribe(receiveCanceled = true)
	public void onSignChange(SignChangeEvent ev) {
		if (!ready || isClient)
			return;
		if (BukkitContainer.DEBUG)
			System.out.println(String.format("SignChange: player %s x %s y %s z %s text %s", new Object[] {ev.signChanger.username, ev.x, ev.y, ev.z, Joiner.on(", ").join(ev.lines) }));
		CraftBlock theBlock = new CraftBlock(
					new CraftChunk(ev.signChanger.worldObj.getChunkFromBlockCoords(ev.x, ev.z)),
					ev.x,
					ev.y,
					ev.z
				);
		CraftPlayer thePlayer;
		if (ev.signChanger instanceof EntityPlayerMP)
		thePlayer = BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) ev.signChanger);
		
		else thePlayer = BukkitForgePlayerCache.getCraftPlayer(BukkitContainer.MOD_PLAYER);
		
		org.bukkit.event.block.SignChangeEvent bev = new org.bukkit.event.block.SignChangeEvent(theBlock, thePlayer, ev.lines);
		bev.setCancelled(ev.isCanceled());
		Bukkit.getPluginManager().callEvent(bev);
		
		ev.setCanceled(bev.isCancelled());
		
		ev.lines = bev.getLines();
		
	}
	@ForgeSubscribe(receiveCanceled = true)
	public void worldLoadEvent(WorldEvent.Load event) {
    	
		if (!ready) {
			return;
		}
		
    	World w = CraftServer.instance().getWorld(event.world.provider.dimensionId);
    	
    	WorldInitEvent init = new WorldInitEvent(w);
    	
    	Bukkit.getPluginManager().callEvent(init);
    	
    	/*WorldLoadEvent worldLoad = new WorldLoadEvent(w);
    	
    	Bukkit.getPluginManager().callEvent(worldLoad); */
    	
    }
    
    @ForgeSubscribe(receiveCanceled = true)
    public void worldSaveEvent(WorldEvent.Save event) {
    	
    	if (!ready) {
			return;
		}
    	
    	WorldSaveEvent save = new WorldSaveEvent(CraftServer.instance().getWorld(event.world.provider.dimensionId));
    	
    	Bukkit.getPluginManager().callEvent(save);
    	
    }
    
    @ForgeSubscribe(receiveCanceled = true)
    public void worldUnloadEvent(WorldEvent.Unload event) {
    	
    	if (!ready) {
			return;
		}
    	
    	WorldUnloadEvent unload = new WorldUnloadEvent(CraftServer.instance().getWorld(event.world.provider.dimensionId));
    	
    	Bukkit.getPluginManager().callEvent(unload);
    	
    }
    
    @ForgeSubscribe(receiveCanceled = true)
    public void populateChunks(PopulateChunkEvent.Post event) {
    	//ChunkPopulateEvent e = new ChunkPopulateEvent(new CraftChunk(event.world.getChunkFromBlockCoords(event.chunkX, event.chunkZ)));

        // Chunk event eventually turns into exception due to bad bukkit chunk code
        //        Bukkit.getPluginManager().callEvent(e);
    }
}

