package keepcalm.mods.bukkit;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/19/13
 * Time: 11:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToBukkit {

    public static org.bukkit.World world( net.minecraft.world.World nmsWorld )
    {
        return CraftServer.instance().worlds.get( nmsWorld.provider.dimensionId );
    }

    public static org.bukkit.entity.Player playerOrMod( EntityPlayer ep )
    {
        if (ep instanceof EntityPlayerMP)
            return CraftPlayerCache.getCraftPlayer((EntityPlayerMP)ep);
        else
            return CraftPlayerCache.getCraftPlayer(BukkitContainer.MOD_PLAYER);
    }

    public static org.bukkit.entity.Player player( EntityPlayer ep )
    {
        return CraftPlayerCache.getCraftPlayer((EntityPlayerMP)ep);
    }

    public static org.bukkit.entity.Player player( net.minecraft.entity.Entity ep )
    {
        if (ep instanceof EntityPlayerMP)
            return CraftPlayerCache.getCraftPlayer((EntityPlayerMP)ep);
        else
            return CraftPlayerCache.getCraftPlayer(BukkitContainer.MOD_PLAYER);
    }

    public static org.bukkit.block.Block blockFromCoords( net.minecraft.world.World world, int x, int y, int z  )
    {
        return new CraftBlock((CraftChunk)chunkFromCoords(world,x,z), x, y, z);
    }

    public static org.bukkit.Chunk chunkFromCoords( net.minecraft.world.World world, int x, int z  )
    {
        return new CraftChunk(world.getChunkFromBlockCoords(x, z));
    }

    public static org.bukkit.Chunk chunk( net.minecraft.world.chunk.Chunk chunk )
    {
        return new CraftChunk( chunk );
    }

    public static LivingEntity livingEntity( net.minecraft.entity.Entity entity )
    {
        CraftEntity e = CraftEntity.getEntity(CraftServer.instance(), entity);
        if (!(e instanceof CraftLivingEntity)) {
            e = new CraftLivingEntity(CraftServer.instance(), (EntityLiving) entity);
        }
        return (LivingEntity) e;
    }

    public static Item item( EntityItem item )
    {
        return (org.bukkit.entity.Item) entity((net.minecraft.entity.Entity) item);
    }

    public static Entity entity(net.minecraft.entity.Entity entity) {
        return CraftEntity.getEntity((CraftServer) Bukkit.getServer(), entity);
    }

    public static Action action( net.minecraftforge.event.entity.player.PlayerInteractEvent.Action action )
    {
        Action act;
        switch (action)
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

        return act;
    }



    public static EntityDamageEvent.DamageCause damageCause(DamageSource ds) {
        EntityDamageEvent.DamageCause dc;
        if (ds == DamageSource.anvil)
            dc = EntityDamageEvent.DamageCause.CUSTOM;
        else if (ds == DamageSource.cactus)
            dc = EntityDamageEvent.DamageCause.CONTACT;
        else if (ds == DamageSource.drown)
            dc = EntityDamageEvent.DamageCause.DROWNING;
        else if (ds == DamageSource.explosion)
            dc = EntityDamageEvent.DamageCause.BLOCK_EXPLOSION;
        else if (ds == DamageSource.fall)
            dc = EntityDamageEvent.DamageCause.FALL;
        else if (ds == DamageSource.fallingBlock)
            dc = EntityDamageEvent.DamageCause.FALL;
        else if (ds == DamageSource.explosion2)
            dc = EntityDamageEvent.DamageCause.ENTITY_EXPLOSION;
        else if (ds == DamageSource.generic)
            dc = EntityDamageEvent.DamageCause.CUSTOM;
        else if (ds == DamageSource.inFire)
            dc = EntityDamageEvent.DamageCause.FIRE;
        else if (ds == DamageSource.inWall)
            dc = EntityDamageEvent.DamageCause.SUFFOCATION;
        else if (ds == DamageSource.lava)
            dc = EntityDamageEvent.DamageCause.LAVA;
        else if (ds == DamageSource.magic)
            dc = EntityDamageEvent.DamageCause.MAGIC;
        else if (ds == DamageSource.onFire)
            dc = EntityDamageEvent.DamageCause.FIRE_TICK;
        else if (ds == DamageSource.outOfWorld)
            dc = EntityDamageEvent.DamageCause.VOID;
        else if (ds == DamageSource.starve)
            dc = EntityDamageEvent.DamageCause.STARVATION;
        else if (ds == DamageSource.wither)
            dc = EntityDamageEvent.DamageCause.WITHER;
        else
            dc = EntityDamageEvent.DamageCause.CUSTOM;
        return dc;
    }


    public static CraftItemStack itemStack(ItemStack currentItem) {
        return new CraftItemStack(currentItem);
    }
}
