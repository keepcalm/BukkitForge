package keepcalm.mods.bukkit.forgeHandler;

import keepcalm.mods.events.forgeex.SignChangeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/22/13
 * Time: 1:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ForgeEventUtils {
    public static Packet130UpdateSign onSignChange(EntityPlayerMP player, Packet130UpdateSign pack) {
        SignChangeEvent ev = new SignChangeEvent(pack.xPosition, pack.yPosition, pack.zPosition, player, pack.signLines);
        MinecraftForge.EVENT_BUS.post(ev);

        if (ev.isCanceled()) {
            return null;
        }
        pack.signLines = ev.lines;

        return pack;
    }
}
