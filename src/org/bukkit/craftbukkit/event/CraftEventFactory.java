package org.bukkit.craftbukkit.event;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.InventoryView;

public class CraftEventFactory {
    // helper methods
	private static CraftEntity getBukkitEntity(net.minecraft.entity.Entity entity) {
		return CraftEntity.getEntity((CraftServer) Bukkit.getServer(), entity);
	}
    private static boolean canBuild(CraftWorld world, Player player, int x, int z) {
        WorldServer worldServer = world.getHandle();
        int spawnSize = Bukkit.getServer().getSpawnRadius();

        // TODO: Review the next check, is this true?
        if (world.getHandle().provider.dimensionId != 0) return true;
        if (spawnSize <= 0) return true;
        if (player.isOp()) return true;

        ChunkCoordinates chunkcoordinates = worldServer.getSpawnPoint();

        int distanceFromSpawn = (int) Math.max(Math.abs(x - chunkcoordinates.posX), Math.abs(z - chunkcoordinates.posZ));
        return distanceFromSpawn >= spawnSize;
    }

    public static <T extends Event> T callEvent(T event) {
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    
    public static void handleBlockSpreadEvent(Block block, Block source, int type, int data) {
        BlockState state = block.getState();
        state.setTypeId(type);
        state.setRawData((byte) data);

        BlockSpreadEvent event = new BlockSpreadEvent(block, source, state);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            state.update(true);
        }
    }
    /**
     * Block place methods
     */
    public static BlockPlaceEvent callBlockPlaceEvent(World world, EntityPlayer par2EntityPlayer, BlockState replacedBlockState, int clickedX, int clickedY, int clickedZ) {
        CraftWorld craftWorld = (CraftWorld) CraftServer.instance().getWorld(((WorldServer) world).provider.dimensionId);
        CraftServer craftServer = (CraftServer) Bukkit.getServer();

        Player player = (par2EntityPlayer == null) ? null : (Player) CraftEntity.getEntity(craftServer, par2EntityPlayer);

        Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        Block placedBlock = replacedBlockState.getBlock();

        boolean canBuild = canBuild(craftWorld, player, placedBlock.getX(), placedBlock.getZ());

        BlockPlaceEvent event = new BlockPlaceEvent(placedBlock, replacedBlockState, blockClicked, player.getItemInHand(), player, canBuild);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Bucket methods
     */
    public static PlayerBucketEmptyEvent callPlayerBucketEmptyEvent(EntityPlayer par3EntityPlayer, int clickedX, int clickedY, int clickedZ, int clickedFace, ItemStack itemInHand) {
        return (PlayerBucketEmptyEvent) getPlayerBucketEvent(false, par3EntityPlayer, clickedX, clickedY, clickedZ, clickedFace, itemInHand, Item.bucketEmpty);
    }

    public static PlayerBucketFillEvent callPlayerBucketFillEvent(EntityPlayer par1EntityPlayer, int clickedX, int clickedY, int clickedZ, int clickedFace, ItemStack itemInHand, net.minecraft.item.Item bucket) {
        return (PlayerBucketFillEvent) getPlayerBucketEvent(true, par1EntityPlayer, clickedX, clickedY, clickedZ, clickedFace, itemInHand, bucket);
    }

    private static PlayerEvent getPlayerBucketEvent(boolean isFilling, EntityPlayer par1EntityPlayer, int clickedX, int clickedY, int clickedZ, int clickedFace, ItemStack itemstack, net.minecraft.item.Item item) {
        Player player = (par1EntityPlayer == null) ? null : (Player) CraftEntity.getEntity((CraftServer) Bukkit.getServer(), par1EntityPlayer);
        CraftItemStack itemInHand = new CraftItemStack(new ItemStack(item));
        Material bucket = Material.getMaterial(itemstack.itemID);

        CraftWorld craftWorld = (CraftWorld) player.getWorld();
        CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);

        PlayerEvent event = null;
        if (isFilling) {
            event = new PlayerBucketFillEvent(player, blockClicked, blockFace, bucket, itemInHand);
            ((PlayerBucketFillEvent) event).setCancelled(!canBuild(craftWorld, player, clickedX, clickedZ));
        } else {
            event = new PlayerBucketEmptyEvent(player, blockClicked, blockFace, bucket, itemInHand);
            ((PlayerBucketEmptyEvent) event).setCancelled(!canBuild(craftWorld, player, clickedX, clickedZ));
        }

        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * Player Interact event
     */
    /*public static PlayerInteractEvent callPlayerInteractEvent(EntityPlayerMP who, Action action, ItemStack itemstack) {
        if (action != Action.LEFT_CLICK_AIR && action != Action.RIGHT_CLICK_AIR) {
            return null;
        }
        return callPlayerInteractEvent(who, action, 0, 256, 0, 0, itemstack);
    }*/

    public static PlayerInteractEvent callPlayerInteractEvent(EntityPlayer par5Entity, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action leftClickBlock, int clickedX, int clickedY, int clickedZ, int clickedFace, CraftItemStack craftItemStack) {
        Player player = (Player) ((par5Entity == null) ? null : CraftEntity.getEntity((CraftServer) Bukkit.getServer(), par5Entity));
        CraftItemStack itemInHand = new CraftItemStack(craftItemStack);

        CraftWorld craftWorld = (CraftWorld) player.getWorld();
        CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);
        Action act = null;
        if (clickedY > 255) {
            blockClicked = null;
            switch (leftClickBlock) {
            case LEFT_CLICK_BLOCK:
                act = Action.LEFT_CLICK_AIR;
                break;
            case RIGHT_CLICK_BLOCK:
                act = Action.RIGHT_CLICK_AIR;
                break;
			/*case LEFT_CLICK_AIR:
				act = Action.LEFT_CLICK_AIR;
				break;
			case PHYSICAL:
				act = Action.PHYSICAL;
				break;*/
			case RIGHT_CLICK_AIR:
				act = Action.RIGHT_CLICK_AIR;
				break;
			default:
				break;
            }
        }

        if (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0) {
            itemInHand = null;
        }

        PlayerInteractEvent event = new PlayerInteractEvent(player, act, itemInHand, blockClicked, blockFace);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }
    
	public static PlayerInteractEvent callPlayerInteractEvent(EntityPlayer par5Entity,
			Action leftClickBlock, int clickedX, int clickedY, int clickedZ, int clickedFace,
			CraftItemStack craftItemStack) {
        Player player = (Player) ((par5Entity == null) ? null : CraftEntity.getEntity((CraftServer) Bukkit.getServer(), par5Entity));
        CraftItemStack itemInHand = new CraftItemStack(craftItemStack);

        CraftWorld craftWorld = (CraftWorld) player.getWorld();
        CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = craftWorld.getBlockAt(clickedX, clickedY, clickedZ);
        BlockFace blockFace = CraftBlock.notchToBlockFace(clickedFace);
        Action act = null;
        if (clickedY > 255) {
            blockClicked = null;
           /* switch (leftClickBlock) {
            case LEFT_CLICK_BLOCK:
                act = Action.LEFT_CLICK_AIR;
                break;
            case RIGHT_CLICK_BLOCK:
                act = Action.RIGHT_CLICK_AIR;
                break;
			/*case LEFT_CLICK_AIR:
				act = Action.LEFT_CLICK_AIR;
				break;
			case PHYSICAL:
				act = Action.PHYSICAL;
				break;*/
			/*case RIGHT_CLICK_AIR:
				act = Action.RIGHT_CLICK_AIR;
				break;
			default:
				break;
            }*/
        }

        if (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0) {
            itemInHand = null;
        }

        PlayerInteractEvent event = new PlayerInteractEvent(player, leftClickBlock, itemInHand, blockClicked, blockFace);
        craftServer.getPluginManager().callEvent(event);

        return event;
	}

    /**
     * EntityShootBowEvent
     */
    public static EntityShootBowEvent callEntityShootBowEvent(EntityLiving who, ItemStack itemstack, EntityArrow entityArrow, float force) {
        LivingEntity shooter = (LivingEntity) getBukkitEntity(who);
        CraftItemStack itemInHand = new CraftItemStack(itemstack);
        Arrow arrow = (Arrow) getBukkitEntity(entityArrow);

        if (itemInHand != null && (itemInHand.getType() == Material.AIR || itemInHand.getAmount() == 0)) {
            itemInHand = null;
        }

        EntityShootBowEvent event = new EntityShootBowEvent(shooter, itemInHand, arrow, force);
        Bukkit.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * BlockDamageEvent
     */
    public static BlockDamageEvent callBlockDamageEvent(EntityPlayerMP who, int x, int y, int z, ItemStack itemstack, boolean instaBreak) {
        Player player = (who == null) ? null : (Player) getBukkitEntity(who);
        CraftItemStack itemInHand = new CraftItemStack(itemstack);

        CraftWorld craftWorld = (CraftWorld) player.getWorld();
        CraftServer craftServer = (CraftServer) player.getServer();

        Block blockClicked = craftWorld.getBlockAt(x, y, z);

        BlockDamageEvent event = new BlockDamageEvent(player, blockClicked, itemInHand, instaBreak);
        craftServer.getPluginManager().callEvent(event);

        return event;
    }

    /**
     * CreatureSpawnEvent
     */
    public static CreatureSpawnEvent callCreatureSpawnEvent(EntityLiving entityliving, SpawnReason spawnReason) {
        LivingEntity entity = (LivingEntity) getBukkitEntity(entityliving);
        CraftServer craftServer = (CraftServer) entity.getServer();

        CreatureSpawnEvent event = new CreatureSpawnEvent(entity, spawnReason);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * EntityTameEvent
     */
    public static EntityTameEvent callEntityTameEvent(EntityLiving entity, EntityPlayer par1EntityPlayer) {
        org.bukkit.entity.Entity bukkitEntity = getBukkitEntity(entity);
        org.bukkit.entity.AnimalTamer bukkitTamer = (par1EntityPlayer != null ? (AnimalTamer) getBukkitEntity(par1EntityPlayer) : null);
        CraftServer craftServer = (CraftServer) bukkitEntity.getServer();

        EntityTameEvent event = new EntityTameEvent((LivingEntity) bukkitEntity, bukkitTamer);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * ItemSpawnEvent
     */
    public static ItemSpawnEvent callItemSpawnEvent(EntityItem entityitem) {
        org.bukkit.entity.Item entity = (org.bukkit.entity.Item) getBukkitEntity(entityitem);
        CraftServer craftServer = (CraftServer) entity.getServer();

        ItemSpawnEvent event = new ItemSpawnEvent(entity, entity.getLocation());

        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * ItemDespawnEvent
     */
    public static ItemDespawnEvent callItemDespawnEvent(EntityItem entityitem) {
        org.bukkit.entity.Item entity = (org.bukkit.entity.Item) getBukkitEntity(entityitem);

        ItemDespawnEvent event = new ItemDespawnEvent(entity, entity.getLocation());

        ((CraftServer) entity.getServer()).getPluginManager().callEvent(event);
        return event;
    }

    /**
     * PotionSplashEvent
     */
    public static PotionSplashEvent callPotionSplashEvent(EntityPotion potion, Map<LivingEntity, Double> affectedEntities) {
        ThrownPotion thrownPotion = (ThrownPotion) getBukkitEntity(potion);

        PotionSplashEvent event = new PotionSplashEvent(thrownPotion, affectedEntities);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * BlockFadeEvent
     */
    public static BlockFadeEvent callBlockFadeEvent(Block block, int type) {
        BlockState state = block.getState();
        state.setTypeId(type);

        BlockFadeEvent event = new BlockFadeEvent(block, state);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static EntityDeathEvent callEntityDeathEvent(EntityLiving victim) {
        return callEntityDeathEvent(victim, new ArrayList<org.bukkit.inventory.ItemStack>(0));
    }

    public static EntityDeathEvent callEntityDeathEvent(EntityLiving victim, List<org.bukkit.inventory.ItemStack> drops) {
        CraftLivingEntity entity = (CraftLivingEntity) getBukkitEntity(victim);
        EntityDeathEvent event = new EntityDeathEvent(entity, drops, victim.experienceValue);
        org.bukkit.World world = entity.getWorld();
        Bukkit.getServer().getPluginManager().callEvent(event);

        victim.experienceValue = event.getDroppedExp();

        for (org.bukkit.inventory.ItemStack stack : event.getDrops()) {
            if (stack == null || stack.getType() == Material.AIR) continue;

            if (stack instanceof CraftItemStack) {
                // Use the internal item to preserve possible data.
                victim.dropItemWithOffset(stack.getTypeId(), stack.getAmount(), 0.0F);
            }
            else {
                world.dropItemNaturally(entity.getLocation(), stack);
            }
        }

        return event;
    }

    public static PlayerDeathEvent callPlayerDeathEvent(EntityPlayer victim, List<org.bukkit.inventory.ItemStack> drops, String deathMessage) {
        CraftPlayer entity = (CraftPlayer) getBukkitEntity(victim);
        PlayerDeathEvent event = new PlayerDeathEvent(entity, drops, victim.experienceValue, 0, deathMessage);
        org.bukkit.World world = entity.getWorld();
        Bukkit.getServer().getPluginManager().callEvent(event);

        /*victim.keepLevel = event.getKeepLevel();
        victim.newLevel = event.getNewLevel();
        victim.newTotalExp = event.getNewTotalExp();
        victim.expToDrop = event.getDroppedExp();
        victim.newExp = event.getNewExp();*/

        for (org.bukkit.inventory.ItemStack stack : event.getDrops()) {
            if (stack == null || stack.getType() == Material.AIR) continue;

            if (stack instanceof CraftItemStack) {
                // Use the internal item to preserve possible data.
            	victim.dropItemWithOffset(stack.getTypeId(), stack.getAmount(), 0.0F);
            } else {
                world.dropItemNaturally(entity.getLocation(), stack);
            }
        }

        return event;
    }

    /**
     * Server methods
     */
    public static ServerListPingEvent callServerListPingEvent(Server craftServer, InetAddress address, String motd, int numPlayers, int maxPlayers) {
        ServerListPingEvent event = new ServerListPingEvent(address, motd, numPlayers, maxPlayers);
        craftServer.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * EntityDamage(ByEntityEvent)
     */
    public static EntityDamageEvent callEntityDamageEvent(Entity damager, Entity damagee, DamageCause cause, int damage) {
        EntityDamageEvent event;
        if (damager != null) {
            event = new EntityDamageByEntityEvent(getBukkitEntity(damager), getBukkitEntity(damagee), cause, damage);
        } else {
            event = new EntityDamageEvent(getBukkitEntity(damagee), cause, damage);
        }
        callEvent(event);



        return event;
    }

    public static EntityDamageEvent handleEntityDamageEvent(Entity entity, DamageSource source, int damage) {
        Entity damager = source.getEntity();
        DamageCause cause = DamageCause.ENTITY_ATTACK;

        if (source instanceof EntityDamageSourceIndirect) {
            damager = ((EntityDamageSourceIndirect) source).getSourceOfDamage();
            if (getBukkitEntity(damager) instanceof ThrownPotion) {
                cause = DamageCause.MAGIC;
            } else if (getBukkitEntity(damager) instanceof Projectile) {
                cause = DamageCause.PROJECTILE;
            }
        }

        return callEntityDamageEvent(damager, entity, cause, damage);
    }

    // Non-Living Entities such as EntityEnderCrystal need to call this
    public static boolean handleNonLivingEntityDamageEvent(Entity entity, DamageSource source, int damage) {
        if (!(source instanceof EntityDamageSource)) {
            return false;
        }
        EntityDamageEvent event = handleEntityDamageEvent(entity, source, damage);
        return event.isCancelled() || event.getDamage() == 0;
    }

    public static PlayerLevelChangeEvent callPlayerLevelChangeEvent(Player player, int oldLevel, int newLevel) {
        PlayerLevelChangeEvent event = new PlayerLevelChangeEvent(player, oldLevel, newLevel);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static PlayerExpChangeEvent callPlayerExpChangeEvent(EntityPlayer par1EntityPlayer, int expAmount) {
        Player player = (Player) getBukkitEntity(par1EntityPlayer);
        PlayerExpChangeEvent event = new PlayerExpChangeEvent(player, expAmount);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static void handleBlockGrowEvent(World world, int x, int y, int z, int type, int data) {
        Block block = new CraftBlock(new CraftChunk(world.getChunkFromBlockCoords(x, z)), x, y, z);
        CraftBlockState state = (CraftBlockState) block.getState();
        state.setTypeId(type);
        state.setRawData((byte) data);

        BlockGrowEvent event = new BlockGrowEvent(block, state);
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            state.update(true);
        }
    }

    public static FoodLevelChangeEvent callFoodLevelChangeEvent(EntityPlayerMP entity, int level) {
        FoodLevelChangeEvent event = new FoodLevelChangeEvent((Player) getBukkitEntity(entity), level);
        getBukkitEntity(entity).getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(org.bukkit.entity.Entity entity, Block block, Material material) {
        EntityChangeBlockEvent event = new EntityChangeBlockEvent((LivingEntity) entity, block, material);
        entity.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static PigZapEvent callPigZapEvent(Entity pig, Entity lightning, Entity pigzombie) {
        PigZapEvent event = new PigZapEvent((Pig) getBukkitEntity(pig), (LightningStrike) getBukkitEntity(lightning), (PigZombie) getBukkitEntity(pigzombie));
        getBukkitEntity(pig).getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(Entity entity, Block block, Material material) {
        EntityChangeBlockEvent event = new EntityChangeBlockEvent((LivingEntity) getBukkitEntity(entity), block, material);
        getBukkitEntity(entity).getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityChangeBlockEvent callEntityChangeBlockEvent(Entity entity, int x, int y, int z, int type, int data) {
        Block block = new CraftBlock(new CraftChunk(entity.worldObj.getChunkFromBlockCoords(x, z)),x, y, z);
        Material material = Material.getMaterial(type);

        return callEntityChangeBlockEvent(entity, block, material);
    }

    public static CreeperPowerEvent callCreeperPowerEvent(Entity creeper, Entity lightning, CreeperPowerEvent.PowerCause cause) {
        CreeperPowerEvent event = new CreeperPowerEvent((Creeper) getBukkitEntity(creeper), (LightningStrike) getBukkitEntity(lightning), cause);
        getBukkitEntity(creeper).getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityTargetEvent callEntityTargetEvent(Entity entity, Entity target, EntityTargetEvent.TargetReason reason) {
        EntityTargetEvent event = new EntityTargetEvent(getBukkitEntity(entity), target == null ? null : getBukkitEntity(target), reason);
        getBukkitEntity(entity).getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityTargetLivingEntityEvent callEntityTargetLivingEvent(Entity entity, EntityLiving target, EntityTargetEvent.TargetReason reason) {
        EntityTargetLivingEntityEvent event = new EntityTargetLivingEntityEvent(getBukkitEntity(entity), (LivingEntity) getBukkitEntity(target), reason);
        getBukkitEntity(entity).getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static EntityBreakDoorEvent callEntityBreakDoorEvent(Entity entity, int x, int y, int z) {
        org.bukkit.entity.Entity entity1 = getBukkitEntity(entity);
        Block block = entity1.getWorld().getBlockAt(x, y, z);

        EntityBreakDoorEvent event = new EntityBreakDoorEvent((LivingEntity) entity1, block);
        entity1.getServer().getPluginManager().callEvent(event);

        return event;
    }

    /**
     * @unimplemented
     * FIXME
     */
    public static Container callInventoryOpenEvent(EntityPlayer player, Container container) {
        //if (player.craftingInventory != player.inventory) { // fire INVENTORY_CLOSE if one already open
        //    player.netServerHandler.handleContainerClose(new Packet101CloseWindow(player.activeContainer.windowId));
        //}
    	
        CraftServer server = (CraftServer) Bukkit.getServer();
        CraftPlayer craftPlayer = (CraftPlayer) getBukkitEntity(player);
        //player.craftingInventory.(container, craftPlayer);
        int i = 0;
        InventoryBasic inv = new InventoryBasic("", container.getInventory().size());
        for (Object j : container.inventoryItemStacks) {
        	net.minecraft.item.ItemStack v = (net.minecraft.item.ItemStack) j;
        	inv.setInventorySlotContents(i, v);
        	i++;
        }
        InventoryOpenEvent event = new InventoryOpenEvent(new CraftInventoryView((HumanEntity) getBukkitEntity(player), new CraftInventory(inv) ,container));
        server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            container.setPlayerIsPresent(craftPlayer.getHandle(), false);
            return null;
        }
	
        return container;
    }

    public static ItemStack callPreCraftEvent(InventoryCrafting matrix, InventoryCraftResult inv, ItemStack result, InventoryView lastCraftView, boolean isRepair) {
    	//matrix.eventHandler.;
        CraftInventoryCrafting inventory = new CraftInventoryCrafting(matrix,inv);
        inventory.setResult(new CraftItemStack(result));

        PrepareItemCraftEvent event = new PrepareItemCraftEvent(inventory, lastCraftView, isRepair);
        Bukkit.getPluginManager().callEvent(event);

        org.bukkit.inventory.ItemStack bitem = event.getInventory().getResult();

        return CraftItemStack.createNMSItemStack(bitem);
    }

    public static ProjectileLaunchEvent callProjectileLaunchEvent(Entity entity) {
        Projectile bukkitEntity = (Projectile) getBukkitEntity(entity);
        ProjectileLaunchEvent event = new ProjectileLaunchEvent(bukkitEntity);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static ExpBottleEvent callExpBottleEvent(Entity entity, int exp) {
        ThrownExpBottle bottle = (ThrownExpBottle) getBukkitEntity(entity);
        ExpBottleEvent event = new ExpBottleEvent(bottle, exp);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    public static BlockRedstoneEvent callRedstoneChange(World world, int x, int y, int z, int oldCurrent, int newCurrent) {
        BlockRedstoneEvent event = new BlockRedstoneEvent(new CraftBlock(new CraftChunk(world.getChunkFromBlockCoords(x, z)), x, y ,z), oldCurrent, newCurrent);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static NotePlayEvent callNotePlayEvent(World world, int x, int y, int z, byte instrument, byte note) {
        NotePlayEvent event = new NotePlayEvent(new CraftBlock(new CraftChunk(world.getChunkFromBlockCoords(x, z)), x, y ,z), org.bukkit.Instrument.getByType(instrument), new org.bukkit.Note(note));
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    public static void callPlayerItemBreakEvent(EntityPlayerMP human, ItemStack brokenItem) {
        CraftItemStack item = new CraftItemStack(brokenItem);
        PlayerItemBreakEvent event = new PlayerItemBreakEvent((Player) CraftEntity.getEntity((CraftServer) Bukkit.getServer(), human) , item);
        Bukkit.getPluginManager().callEvent(event);
    }

}
