package keepcalm.mods.bukkit;

import keepcalm.mods.bukkit.forgeHandler.ForgeEventUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.NetworkListenThread;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.WorldServer;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: jtdossett
 * Date: 2/22/13
 * Time: 8:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class CraftNetServerHandler extends NetServerHandler {
    public CraftNetServerHandler(MinecraftServer par1, INetworkManager par2, EntityPlayerMP par3) {
        super(par1, par2, par3);

    }

    static void Test()
    {
        NetworkListenThread ntl = null;
        ntl.addPlayer(new CraftNetServerHandler(null, null, null));
    }


    public static final Logger a = Logger.getLogger("Minecraft");

    @Override
    public void handleUpdateSign(Packet130UpdateSign par1Packet130UpdateSign)
    {
        EntityPlayerMP ep = getPlayer();

        WorldServer var2 = MinecraftServer.getServer().worldServerForDimension(ep.dimension);

        if (var2.blockExists(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition))
        {
            TileEntity var3 = var2.getBlockTileEntity(par1Packet130UpdateSign.xPosition, par1Packet130UpdateSign.yPosition, par1Packet130UpdateSign.zPosition);

            if ((var3 instanceof TileEntitySign))
            {
                TileEntitySign var4 = (TileEntitySign)var3;

                if (!var4.isEditable())
                {
                    //e.logWarning(new StringBuilder().append("Player ").append(ep.username).append(" just tried to change non-editable sign").toString());
                    return;
                }
            }

            for (int var8 = 0; var8 < 4; var8++)
            {
                boolean var5 = true;

                if (par1Packet130UpdateSign.signLines[var8].length() > 15)
                {
                    var5 = false;
                }
                else
                {
                    for (int var6 = 0; var6 < par1Packet130UpdateSign.signLines[var8].length(); var6++)
                    {
                        if (ChatAllowedCharacters.allowedCharacters.indexOf(par1Packet130UpdateSign.signLines[var8].charAt(var6)) < 0)
                        {
                            var5 = false;
                        }
                    }
                }

                if (!var5)
                {
                    par1Packet130UpdateSign.signLines[var8] = "!?";
                }
            }

            par1Packet130UpdateSign = ForgeEventUtils.onSignChange(getPlayer(), par1Packet130UpdateSign);

            if ((var3 instanceof TileEntitySign))
            {
                int var8 = par1Packet130UpdateSign.xPosition;
                int var9 = par1Packet130UpdateSign.yPosition;
                int var6 = par1Packet130UpdateSign.zPosition;
                TileEntitySign var7 = (TileEntitySign)var3;
                System.arraycopy(par1Packet130UpdateSign.signLines, 0, var7.signText, 0, 4);
                var7.onInventoryChanged();
                var2.markBlockForUpdate(var8, var9, var6);
            }
        }
    }
}
