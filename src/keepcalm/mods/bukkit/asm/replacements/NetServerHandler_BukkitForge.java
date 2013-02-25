package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import keepcalm.mods.bukkit.forgeHandler.ForgeEventUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.world.WorldServer;

public class NetServerHandler_BukkitForge {

    @AsmagicReplaceMethod
    public void a(Packet130UpdateSign par1Packet130UpdateSign)
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

    public EntityPlayerMP getPlayer() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
