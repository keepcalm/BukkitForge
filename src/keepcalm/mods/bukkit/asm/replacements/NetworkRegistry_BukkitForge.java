package keepcalm.mods.bukkit.asm.replacements;


import com.eoware.asm.asmagic.AsmagicClassProxy;
import com.eoware.asm.asmagic.AsmagicAddMethod;
import com.eoware.asm.asmagic.AsmagicMethodPublic;
import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.NetHandler;

import java.util.Set;

public class NetworkRegistry_BukkitForge {

    private Set<IConnectionHandler> connectionHandlers = Sets.newLinkedHashSet();

    private static final NetworkRegistry_BukkitForge INSTANCE = new NetworkRegistry_BukkitForge();

    public static NetworkRegistry_BukkitForge instance()
    {
        return INSTANCE;
    }

    public void playerLoggedIn(EntityPlayerMP player, NetServerHandler netHandler, INetworkManager manager)
    {
        generateChannelRegistration(player, netHandler, manager);
        for (IConnectionHandler handler : connectionHandlers)
        {
            handler.playerLoggedIn((Player)player, netHandler, manager);
        }
    }

    void generateChannelRegistration(EntityPlayer player, NetHandler netHandler, INetworkManager manager)
    {
    }
}
