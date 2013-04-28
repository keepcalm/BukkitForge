package keepcalm.mods.bukkit;

import keepcalm.mods.events.forgeex.*;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.bukkit.craftbukkit.v1_5_R2.block.CraftBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.*;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.world.*;

public class ToBukkitEvent {

    public static CreatureSpawnEvent CreatureSpawn(EntityJoinWorldEvent ev) {

        return new CreatureSpawnEvent((LivingEntity) ToBukkit.livingEntity(ev.entity), CreatureSpawnEvent.SpawnReason.DEFAULT);
    }

    public static ItemDespawnEvent ItemDespawn(ItemExpireEvent ev) {

        org.bukkit.entity.Item entity = (org.bukkit.entity.Item) ToBukkit.item(ev.entityItem);

        return new ItemDespawnEvent(entity, entity.getLocation());
    }

    public static ItemSpawnEvent ItemSpawn(ItemTossEvent ev) {

        org.bukkit.entity.Item entity = (org.bukkit.entity.Item) ToBukkit.item(ev.entityItem);

        return new ItemSpawnEvent(entity, entity.getLocation());
    }

    public static EntityDamageByEntityEvent EntityDamageByEntity(LivingAttackEvent ev) {
        return null;
    }

    public static EntityDamageEvent EntityDamage(LivingAttackEvent ev) {
        return null;
    }
    
    public static EntityDeathEvent EntityDeath(LivingDeathEvent ev) {
        return null;
    }

    public static EntityDamageEvent EntityDamage(LivingHurtEvent ev) {
        return null;
    }

    public static EntityTargetEvent EventTarget(LivingSetAttackTargetEvent ev) {
        return null;
    }

/*	public static ????(MinecartCollisionEvent ev) {
		return null;
	}      */

	public static EntityShootBowEvent ShootBowEvent(ArrowLooseEvent ev) {
		return null;
    }
    
    public static EntityDamageEvent EntityDamage(AttackEntityEvent ev) {
        return null;
    }

    public static BlockCanBuildEvent BlockCanBuild(PlayerInteractEvent ev) {
        return null;
    }

    public static BlockPlaceEvent BlockPlace(PlayerInteractEvent ev) {
        return null;
    }
    public static BlockIgniteEvent BlockIgnite(PlayerInteractEvent ev) {
        return null;
    }
    
    public static PlayerPickupItemEvent PlayerPickupItem(EntityItemPickupEvent ev) {
        return null;
    }

    public static PlayerBucketFillEvent PlayerBucketFill(FillBucketEvent ev) {
        return null;
    }
    
    public static org.bukkit.event.player.PlayerInteractEvent PlayerInteract(PlayerInteractEvent ev) {
        return new org.bukkit.event.player.PlayerInteractEvent(ToBukkit.player(ev.entityPlayer),
                                                               ToBukkit.action(ev.action),
                                                               ToBukkit.itemStack(ev.entityPlayer.inventory.getCurrentItem()),
                                                               ToBukkit.blockFromCoords( ev.entity.worldObj, ev.x, ev.y, ev.z ),
                                                               CraftBlock.notchToBlockFace(ev.face));
    }

    public static PlayerBedEnterEvent PlayerBedEnter(PlayerSleepInBedEvent ev) {
        return null;
    }
    
    public static ChunkLoadEvent ChunkLoad(ChunkEvent.Load ev) {
        return new ChunkLoadEvent( ToBukkit.chunk(ev.getChunk()), false);
    }
    
    public static ChunkUnloadEvent ChunkUnload(ChunkEvent.Unload ev) {
        return new ChunkUnloadEvent(ToBukkit.chunk(ev.getChunk()));
    }

    public static AsyncPlayerChatEvent AsyncPlayerChat(ServerChatEvent ev) {
        return null;
    }

    public static PlayerChatEvent PlayerChat(ServerChatEvent ev) {
        return null;
    }
    
    public static StructureGrowEvent StructureGrow(SaplingGrowTreeEvent ev) {
        return null;
    }
    
    public static ServerCommandEvent ServerCommand(CommandEvent ev) {
        return null;
    }
    
    public static BlockDispenseEvent BlockDispense(DispenseItemEvent ev) {
        return null;
    }
    
    public static BlockDamageEvent BlockDamage(PlayerDamageBlockEvent ev) {
        return null;
    }
    
    public static BlockBreakEvent BlockBreak(BlockDestroyEvent ev) {
        return null;
    }

    public static EntityExplodeEvent EntityExplode(CreeperExplodeEvent ev) {
        return null;
    }
    
    public static BlockSpreadEvent BlockSpread(LiquidFlowEvent ev) {
        return null;
    }

    public static SheepDyeWoolEvent SheepDyeWool(SheepDyeEvent ev) {
        return null;
    }
    
    public static PlayerMoveEvent PlayerMove(PlayerMoveEvent ev) {
        return null;
    }

    public static PlayerInteractEvent PlayerInteract(PressurePlateInteractEvent ev) {
        return null;
    }
    
    public static org.bukkit.event.weather.LightningStrikeEvent LightningStrike(LightningStrikeEvent ev) {
        return null;
    }
    
    public static org.bukkit.event.block.SignChangeEvent SignChange(SignChangeEvent ev) {
        return null;
    }
    
    public static WorldInitEvent WorldInit(WorldEvent.Load event) {
        return null;
    }

    public static WorldSaveEvent WorldSave(WorldEvent.Save event) {
        return null;
    }

    public static WorldUnloadEvent WorldUnload(WorldEvent.Unload event) {
        return null;
    }
    
    public static ChunkPopulateEvent ChunkPopulate(PopulateChunkEvent.Post event) {
        return null;
    }
}