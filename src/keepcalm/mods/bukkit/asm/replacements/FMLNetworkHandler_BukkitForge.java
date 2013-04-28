package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import cpw.mods.fml.common.registry.GameRegistry;
//import keepcalm.mods.bukkit.CraftNetworkHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.server.MinecraftServer;

import org.bukkit.craftbukkit.v1_5_R2.CraftServer;

public class FMLNetworkHandler_BukkitForge {

    @AsmagicReplaceMethod
    public static void handlePlayerLogin(EntityPlayerMP player, NetServerHandler netHandler, INetworkManager manager)
    {
//        NetServerHandler nsh = new CraftNetworkHandler(MinecraftServer.getServer(),manager, player);

        NetworkRegistry_BukkitForge.instance().playerLoggedIn(player, netHandler, manager);

        GameRegistry.onPlayerLogin(player);
    }
}
