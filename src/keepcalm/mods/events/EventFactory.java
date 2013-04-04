package keepcalm.mods.events;

import java.util.ArrayList;
import java.util.List;

import keepcalm.mods.bukkit.ToBukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class EventFactory {
    public static boolean onBlockHarvested(World world, int x, int y, int z, Block block, int metadata, EntityPlayer entityPlayer) {
        PlayerBreakBlockEvent ev = new PlayerBreakBlockEvent(world,x,y,z,block,metadata,entityPlayer);
        MinecraftForge.EVENT_BUS.post(ev);
        
        
        return ev.isCanceled();
    }
    
    public static boolean onEntityExplode(List blocks, World world, Entity entity, double x, double y, double z, float strength)
    {
        org.bukkit.World bworld = ToBukkit.world(world);
        org.bukkit.entity.Entity explode = entity == null ? null : ToBukkit.entity(entity);
        Location location = new Location(bworld, x, y, z);

        List<org.bukkit.block.Block> blockList = new ArrayList<org.bukkit.block.Block>();
        for (int i1 = blocks.size() - 1; i1 >= 0; i1--) {
            ChunkPosition cpos = (ChunkPosition) blocks.get(i1);
            org.bukkit.block.Block block = bworld.getBlockAt(cpos.x, cpos.y, cpos.z);
            if (block.getType() != org.bukkit.Material.AIR) {
                blockList.add(block);
            }
        }

        EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, strength);
        Bukkit.getServer().getPluginManager().callEvent(event);
        blocks.clear();

        for (org.bukkit.block.Block block : event.blockList()) {
            ChunkPosition coords = new ChunkPosition(block.getX(), block.getY(), block.getZ());
            blocks.add(coords);
        }

        return event.isCancelled();
    }
    
    public static boolean onPlayerInteract(EntityPlayer who, Action action, ItemStack item, int x, int y, int z, int notchFace)
    {
        Player player = ToBukkit.player(who);
        PlayerInteractEvent event = new PlayerInteractEvent(
            player,
            action,
            ToBukkit.itemStack(item),
            player.getWorld().getBlockAt(x, y, z),
            CraftBlock.notchToBlockFace(notchFace)
        );
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event.isCancelled();
    }
}
