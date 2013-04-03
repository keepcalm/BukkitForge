package keepcalm.mods.bukkit.asm.replacements;

import org.bukkit.event.block.Action;

import keepcalm.mods.events.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet18Animation;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;

public class Packet18Animation_BukkitForge 
{
    //processPacket
    @AsmagicReplaceMethod
    public void a(NetHandler par1NetHandler)
    {
        //BukkitForge start
        Packet18Animation newThis = null;
        try {
            newThis = (Packet18Animation)Class.forName(this.getClass().getName()).cast(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        ItemStack stack = par1NetHandler.getPlayer().getHeldItem();
        //@see Bukkit implementation at CraftEventFactory: return callPlayerInteractEvent(who, action, 0, 256, 0, 0, itemstack);
        if(EventFactory.onPlayerInteract(par1NetHandler.getPlayer(), Action.LEFT_CLICK_AIR, stack, 0, 256, 0, 0))
            return;
        //BukkitForge end
        par1NetHandler.handleAnimation(newThis);
    }
}
