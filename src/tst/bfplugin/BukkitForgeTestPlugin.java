package tst.bfplugin;

import keepcalm.mods.bukkit.BukkitEventRouter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.*;
import org.bukkit.event.vehicle.*;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.*;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BukkitForgeTestPlugin extends JavaPlugin implements Listener
{
    public void receivedEvent( String eventName, org.bukkit.event.Event event )
    {
        System.out.println( "Received Event: " + event + " EventData:" + event.toString() );
    }

    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
    }

        @EventHandler
    public void onBlockBreak(BlockBreakEvent event) { receivedEvent( "BlockBreakEvent", event ); }
        @EventHandler
    public void onBlockBurn(BlockBurnEvent event) { receivedEvent( "BlockBurnEvent", event ); }
        @EventHandler
    public void onBlockCanBuild(BlockCanBuildEvent event) { receivedEvent( "BlockCanBuildEvent", event ); }
        @EventHandler
    public void onBlockDamage(BlockDamageEvent event) { receivedEvent( "BlockDamageEvent", event ); }
        @EventHandler
    public void onBlockDispense(BlockDispenseEvent event) { receivedEvent( "BlockDispenseEvent", event ); }
        @EventHandler
    public void onBlockExp(BlockExpEvent event) { receivedEvent( "BlockExpEvent", event ); }
        @EventHandler
    public void onBlockFade(BlockFadeEvent event) { receivedEvent( "BlockFadeEvent", event ); }
        @EventHandler
 public void onBlockForm(BlockFormEvent event) { receivedEvent( "BlockFormEvent", event ); }
         @EventHandler
 public void onBlockFromTo(BlockFromToEvent event) { receivedEvent( "BlockFromToEvent", event ); }
         @EventHandler
 public void onBlockGrow(BlockGrowEvent event) { receivedEvent( "BlockGrowEvent", event ); }
         @EventHandler
 public void onBlockIgnite(BlockIgniteEvent event) { receivedEvent( "BlockIgniteEvent", event ); }
         @EventHandler
 public void onBlockPhysics(BlockPhysicsEvent event) { receivedEvent( "BlockPhysicsEvent", event ); }
         @EventHandler
 public void onBlockPistonExtend(BlockPistonExtendEvent event) { receivedEvent( "BlockPistonExtendEvent", event ); }
         @EventHandler
 public void onBlockPistonRetract(BlockPistonRetractEvent event) { receivedEvent( "BlockPistonRetractEvent", event ); }
         @EventHandler
 public void onBlockPlace(BlockPlaceEvent event) { receivedEvent( "BlockPlaceEvent", event ); }
         @EventHandler
 public void onBlockRedstone(BlockRedstoneEvent event) { receivedEvent( "BlockRedstoneEvent", event ); }
         @EventHandler
 public void onBlockSpread(BlockSpreadEvent event) { receivedEvent( "BlockSpreadEvent", event ); }
         @EventHandler
 public void onEntityBlockForm(EntityBlockFormEvent event) { receivedEvent( "EntityBlockFormEvent", event ); }
         @EventHandler
 public void onLeavesDecay(LeavesDecayEvent event) { receivedEvent( "LeavesDecayEvent", event ); }
         @EventHandler
 public void onNotePlay(NotePlayEvent event) { receivedEvent( "NotePlayEvent", event ); }
         @EventHandler
 public void onSignChange(SignChangeEvent event) { receivedEvent( "SignChangeEvent", event ); }
         @EventHandler
 public void onEnchantItem(EnchantItemEvent event) { receivedEvent( "EnchantItemEvent", event ); }
         @EventHandler
 public void onPrepareItemEnchant(PrepareItemEnchantEvent event) { receivedEvent( "PrepareItemEnchantEvent", event ); }
         @EventHandler
 public void onCreatureSpawn(CreatureSpawnEvent event) { receivedEvent( "CreatureSpawnEvent", event ); }
         @EventHandler
 public void onCreeperPower(CreeperPowerEvent event) { receivedEvent( "CreeperPowerEvent", event ); }
         @EventHandler
 public void onEntityBreakDoor(EntityBreakDoorEvent event) { receivedEvent( "EntityBreakDoorEvent", event ); }
         @EventHandler
 public void onEntityChangeBlock(EntityChangeBlockEvent event) { receivedEvent( "EntityChangeBlockEvent", event ); }
         @EventHandler
 public void onEntityCombustByBlock(EntityCombustByBlockEvent event) { receivedEvent( "EntityCombustByBlockEvent", event ); }
         @EventHandler
 public void onEntityCombustByEntity(EntityCombustByEntityEvent event) { receivedEvent( "EntityCombustByEntityEvent", event ); }
         @EventHandler
 public void onEntityCombust(EntityCombustEvent event) { receivedEvent( "EntityCombustEvent", event ); }
         @EventHandler
 public void onEntityCreatePortal(EntityCreatePortalEvent event) { receivedEvent( "EntityCreatePortalEvent", event ); }
         @EventHandler
 public void onEntityDamageByBlock(EntityDamageByBlockEvent event) { receivedEvent( "EntityDamageByBlockEvent", event ); }
         @EventHandler
 public void onEntityDamageByEntity(EntityDamageByEntityEvent event) { receivedEvent( "EntityDamageByEntityEvent", event ); }
         @EventHandler
 public void onEntityDamage(EntityDamageEvent event) { receivedEvent( "EntityDamageEvent", event ); }
         @EventHandler
 public void onEntityDeath(EntityDeathEvent event) { receivedEvent( "EntityDeathEvent", event ); }
         @EventHandler
 public void onEntityExplode(EntityExplodeEvent event) { receivedEvent( "EntityExplodeEvent", event ); }
         @EventHandler
 public void onEntityInteract(EntityInteractEvent event) { receivedEvent( "EntityInteractEvent", event ); }
         @EventHandler
 public void onEntityPortalEnter(EntityPortalEnterEvent event) { receivedEvent( "EntityPortalEnterEvent", event ); }
         @EventHandler
 public void onEntityPortal(EntityPortalEvent event) { receivedEvent( "EntityPortalEvent", event ); }
         @EventHandler
 public void onEntityPortalExit(EntityPortalExitEvent event) { receivedEvent( "EntityPortalExitEvent", event ); }
         @EventHandler
 public void onEntityRegainHealth(EntityRegainHealthEvent event) { receivedEvent( "EntityRegainHealthEvent", event ); }
         @EventHandler
 public void onEntityShootBow(EntityShootBowEvent event) { receivedEvent( "EntityShootBowEvent", event ); }
         @EventHandler
 public void onEntityTame(EntityTameEvent event) { receivedEvent( "EntityTameEvent", event ); }
         @EventHandler
 public void onEntityTarget(EntityTargetEvent event) { receivedEvent( "EntityTargetEvent", event ); }
         @EventHandler
 public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) { receivedEvent( "EntityTargetLivingEntityEvent", event ); }
         @EventHandler
 public void onEntityTeleport(EntityTeleportEvent event) { receivedEvent( "EntityTeleportEvent", event ); }
         @EventHandler
 public void onExpBottle(ExpBottleEvent event) { receivedEvent( "ExpBottleEvent", event ); }
         @EventHandler
 public void onExplosionPrime(ExplosionPrimeEvent event) { receivedEvent( "ExplosionPrimeEvent", event ); }
         @EventHandler
 public void onFoodLevelChange(FoodLevelChangeEvent event) { receivedEvent( "FoodLevelChangeEvent", event ); }
         @EventHandler
 public void onItemDespawn(ItemDespawnEvent event) { receivedEvent( "ItemDespawnEvent", event ); }
         @EventHandler
 public void onItemSpawn(ItemSpawnEvent event) { receivedEvent( "ItemSpawnEvent", event ); }
         @EventHandler
 public void onPigZap(PigZapEvent event) { receivedEvent( "PigZapEvent", event ); }
         @EventHandler
 public void onPlayerDeath(PlayerDeathEvent event) { receivedEvent( "PlayerDeathEvent", event ); }
         @EventHandler
 public void onPotionSplash(PotionSplashEvent event) { receivedEvent( "PotionSplashEvent", event ); }
         @EventHandler
 public void onProjectileHit(ProjectileHitEvent event) { receivedEvent( "ProjectileHitEvent", event ); }
         @EventHandler
 public void onProjectileLaunch(ProjectileLaunchEvent event) { receivedEvent( "ProjectileLaunchEvent", event ); }
         @EventHandler
 public void onSheepDyeWool(SheepDyeWoolEvent event) { receivedEvent( "SheepDyeWoolEvent", event ); }
         @EventHandler
 public void onSheepRegrowWool(SheepRegrowWoolEvent event) { receivedEvent( "SheepRegrowWoolEvent", event ); }
         @EventHandler
 public void onSlimeSplit(SlimeSplitEvent event) { receivedEvent( "SlimeSplitEvent", event ); }
         @EventHandler
 public void onHangingBreakByEntity(HangingBreakByEntityEvent event) { receivedEvent( "HangingBreakByEntityEvent", event ); }
         @EventHandler
 public void onHangingBreak(HangingBreakEvent event) { receivedEvent( "HangingBreakEvent", event ); }
         @EventHandler
 public void onHangingPlace(HangingPlaceEvent event) { receivedEvent( "HangingPlaceEvent", event ); }
         @EventHandler
 public void onBrew(BrewEvent event) { receivedEvent( "BrewEvent", event ); }
         @EventHandler
 public void onCraftItem(CraftItemEvent event) { receivedEvent( "CraftItemEvent", event ); }
         @EventHandler
 public void onFurnaceBurn(FurnaceBurnEvent event) { receivedEvent( "FurnaceBurnEvent", event ); }
         @EventHandler
 public void onFurnaceExtract(FurnaceExtractEvent event) { receivedEvent( "FurnaceExtractEvent", event ); }
         @EventHandler
 public void onFurnaceSmelt(FurnaceSmeltEvent event) { receivedEvent( "FurnaceSmeltEvent", event ); }
         @EventHandler
 public void onInventoryClick(InventoryClickEvent event) { receivedEvent( "InventoryClickEvent", event ); }
         @EventHandler
 public void onInventoryClose(InventoryCloseEvent event) { receivedEvent( "InventoryCloseEvent", event ); }
         @EventHandler
 public void onInventoryOpen(InventoryOpenEvent event) { receivedEvent( "InventoryOpenEvent", event ); }
         @EventHandler
 public void onPrepareItemCraft(PrepareItemCraftEvent event) { receivedEvent( "PrepareItemCraftEvent", event ); }
         @EventHandler
 public void onPaintingBreakByEntity(PaintingBreakByEntityEvent event) { receivedEvent( "PaintingBreakByEntityEvent", event ); }
         @EventHandler
 public void onPaintingBreak(PaintingBreakEvent event) { receivedEvent( "PaintingBreakEvent", event ); }
         @EventHandler
 public void onPaintingPlace(PaintingPlaceEvent event) { receivedEvent( "PaintingPlaceEvent", event ); }
         @EventHandler
 public void onAsyncPlayerChat(AsyncPlayerChatEvent event) { receivedEvent( "AsyncPlayerChatEvent", event ); }
         @EventHandler
 public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) { receivedEvent( "AsyncPlayerPreLoginEvent", event ); }
         @EventHandler
 public void onPlayerAnimation(PlayerAnimationEvent event) { receivedEvent( "PlayerAnimationEvent", event ); }
         @EventHandler
 public void onPlayerBedEnter(PlayerBedEnterEvent event) { receivedEvent( "PlayerBedEnterEvent", event ); }
         @EventHandler
 public void onPlayerBedLeave(PlayerBedLeaveEvent event) { receivedEvent( "PlayerBedLeaveEvent", event ); }
         @EventHandler
 public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) { receivedEvent( "PlayerBucketEmptyEvent", event ); }
         @EventHandler
 public void onPlayerBucketFill(PlayerBucketFillEvent event) { receivedEvent( "PlayerBucketFillEvent", event ); }
         @EventHandler
 public void onPlayerChangedWorld(PlayerChangedWorldEvent event) { receivedEvent( "PlayerChangedWorldEvent", event ); }
         @EventHandler
 public void onPlayerChat(PlayerChatEvent event) { receivedEvent( "PlayerChatEvent", event ); }
         @EventHandler
 public void onPlayerChatTabComplete(PlayerChatTabCompleteEvent event) { receivedEvent( "PlayerChatTabCompleteEvent", event ); }
         @EventHandler
 public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) { receivedEvent( "PlayerCommandPreprocessEvent", event ); }
         @EventHandler
 public void onPlayerDropItem(PlayerDropItemEvent event) { receivedEvent( "PlayerDropItemEvent", event ); }
         @EventHandler
 public void onPlayerEggThrow(PlayerEggThrowEvent event) { receivedEvent( "PlayerEggThrowEvent", event ); }
         @EventHandler
 public void onPlayerExpChange(PlayerExpChangeEvent event) { receivedEvent( "PlayerExpChangeEvent", event ); }
         @EventHandler
 public void onPlayerFish(PlayerFishEvent event) { receivedEvent( "PlayerFishEvent", event ); }
         @EventHandler
 public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) { receivedEvent( "PlayerGameModeChangeEvent", event ); }
         @EventHandler
 public void onPlayerInteractEntity(PlayerInteractEntityEvent event) { receivedEvent( "PlayerInteractEntityEvent", event ); }
         @EventHandler
 public void onPlayerInteract(PlayerInteractEvent event) { receivedEvent( "PlayerInteractEvent", event ); }
         @EventHandler
 public void onPlayerInventory(PlayerInventoryEvent event) { receivedEvent( "PlayerInventoryEvent", event ); }
         @EventHandler
 public void onPlayerItemBreak(PlayerItemBreakEvent event) { receivedEvent( "PlayerItemBreakEvent", event ); }
         @EventHandler
 public void onPlayerItemHeld(PlayerItemHeldEvent event) { receivedEvent( "PlayerItemHeldEvent", event ); }
         @EventHandler
 public void onPlayerJoin(PlayerJoinEvent event) { receivedEvent( "PlayerJoinEvent", event ); }
         @EventHandler
 public void onPlayerKick(PlayerKickEvent event) { receivedEvent( "PlayerKickEvent", event ); }
         @EventHandler
 public void onPlayerLevelChange(PlayerLevelChangeEvent event) { receivedEvent( "PlayerLevelChangeEvent", event ); }
         @EventHandler
 public void onPlayerLogin(PlayerLoginEvent event) { receivedEvent( "PlayerLoginEvent", event ); }
         @EventHandler
 public void onPlayerMove(PlayerMoveEvent event) { receivedEvent( "PlayerMoveEvent", event ); }
         @EventHandler
 public void onPlayerPickupItem(PlayerPickupItemEvent event) { receivedEvent( "PlayerPickupItemEvent", event ); }
         @EventHandler
 public void onPlayerPortal(PlayerPortalEvent event) { receivedEvent( "PlayerPortalEvent", event ); }
         @EventHandler
 public void onPlayerPreLogin(PlayerPreLoginEvent event) { receivedEvent( "PlayerPreLoginEvent", event ); }
         @EventHandler
 public void onPlayerQuit(PlayerQuitEvent event) { receivedEvent( "PlayerQuitEvent", event ); }
         @EventHandler
 public void onPlayerRegisterChannel(PlayerRegisterChannelEvent event) { receivedEvent( "PlayerRegisterChannelEvent", event ); }
         @EventHandler
 public void onPlayerRespawn(PlayerRespawnEvent event) { receivedEvent( "PlayerRespawnEvent", event ); }
         @EventHandler
 public void onPlayerShearEntity(PlayerShearEntityEvent event) { receivedEvent( "PlayerShearEntityEvent", event ); }
         @EventHandler
 public void onPlayerTeleport(PlayerTeleportEvent event) { receivedEvent( "PlayerTeleportEvent", event ); }
         @EventHandler
 public void onPlayerToggleFlight(PlayerToggleFlightEvent event) { receivedEvent( "PlayerToggleFlightEvent", event ); }
         @EventHandler
 public void onPlayerUnregisterChannel(PlayerUnregisterChannelEvent event) { receivedEvent( "PlayerUnregisterChannelEvent", event ); }
         @EventHandler
 public void onPlayerVelocity(PlayerVelocityEvent event) { receivedEvent( "PlayerVelocityEvent", event ); }
         @EventHandler
 public void onMapInitialize(MapInitializeEvent event) { receivedEvent( "MapInitializeEvent", event ); }
         @EventHandler
 public void onPluginDisable(PluginDisableEvent event) { receivedEvent( "PluginDisableEvent", event ); }
         @EventHandler
 public void onPluginEnable(PluginEnableEvent event) { receivedEvent( "PluginEnableEvent", event ); }
         @EventHandler
 public void onRemoteServerCommand(RemoteServerCommandEvent event) { receivedEvent( "RemoteServerCommandEvent", event ); }
         @EventHandler
 public void onServerCommand(ServerCommandEvent event) { receivedEvent( "ServerCommandEvent", event ); }
         @EventHandler
 public void onServerListPing(ServerListPingEvent event) { receivedEvent( "ServerListPingEvent", event ); }
         @EventHandler
 public void onServiceRegister(ServiceRegisterEvent event) { receivedEvent( "ServiceRegisterEvent", event ); }
         @EventHandler
 public void onServiceUnregister(ServiceUnregisterEvent event) { receivedEvent( "ServiceUnregisterEvent", event ); }
         @EventHandler
 public void onVehicleBlockCollision(VehicleBlockCollisionEvent event) { receivedEvent( "VehicleBlockCollisionEvent", event ); }
         @EventHandler
 public void onVehicleCreate(VehicleCreateEvent event) { receivedEvent( "VehicleCreateEvent", event ); }
         @EventHandler
 public void onVehicleDamage(VehicleDamageEvent event) { receivedEvent( "VehicleDamageEvent", event ); }
         @EventHandler
 public void onVehicleDestroy(VehicleDestroyEvent event) { receivedEvent( "VehicleDestroyEvent", event ); }
         @EventHandler
 public void onVehicleEnter(VehicleEnterEvent event) { receivedEvent( "VehicleEnterEvent", event ); }
         @EventHandler
 public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) { receivedEvent( "VehicleEntityCollisionEvent", event ); }
         @EventHandler
 public void onVehicleExit(VehicleExitEvent event) { receivedEvent( "VehicleExitEvent", event ); }
         @EventHandler
 public void onVehicleMove(VehicleMoveEvent event) { receivedEvent( "VehicleMoveEvent", event ); }
         @EventHandler
 public void onVehicleUpdate(VehicleUpdateEvent event) { receivedEvent( "VehicleUpdateEvent", event ); }
         @EventHandler
 public void onLightningStrike(LightningStrikeEvent event) { receivedEvent( "LightningStrikeEvent", event ); }
         @EventHandler
 public void onThunderChange(ThunderChangeEvent event) { receivedEvent( "ThunderChangeEvent", event ); }
         @EventHandler
 public void onWeatherChange(WeatherChangeEvent event) { receivedEvent( "WeatherChangeEvent", event ); }
         @EventHandler
 public void onChunkLoad(ChunkLoadEvent event) { receivedEvent( "ChunkLoadEvent", event ); }
         @EventHandler
 public void onChunkPopulate(ChunkPopulateEvent event) { receivedEvent( "ChunkPopulateEvent", event ); }
         @EventHandler
 public void onChunkUnload(ChunkUnloadEvent event) { receivedEvent( "ChunkUnloadEvent", event ); }
         @EventHandler
 public void onPortalCreate(PortalCreateEvent event) { receivedEvent( "PortalCreateEvent", event ); }
         @EventHandler
 public void onSpawnChange(SpawnChangeEvent event) { receivedEvent( "SpawnChangeEvent", event ); }
         @EventHandler
 public void onStructureGrow(StructureGrowEvent event) { receivedEvent( "StructureGrowEvent", event ); }
         @EventHandler
 public void onWorldInit(WorldInitEvent event) { receivedEvent( "WorldInitEvent", event ); }
         @EventHandler
 public void onWorldLoad(WorldLoadEvent event) { receivedEvent( "WorldLoadEvent", event ); }
         @EventHandler
 public void onWorldSave(WorldSaveEvent event) { receivedEvent( "WorldSaveEvent", event ); }
         @EventHandler
 public void onWorldUnload(WorldUnloadEvent event) { receivedEvent( "WorldUnloadEvent", event ); }

}