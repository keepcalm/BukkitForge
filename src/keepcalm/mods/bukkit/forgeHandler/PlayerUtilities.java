package keepcalm.mods.bukkit.forgeHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Vec3;

public class PlayerUtilities {
	private EntityPlayerMP thePlayer;
	
	public PlayerUtilities(EntityPlayerMP who) {
		thePlayer = who;
	}
	
	public static MovingObjectPosition getTargetBlock(EntityPlayerMP thePlayer) {
		float par1 = 0.1F;
		Vec3 var4 = getPosition(256, thePlayer);
        Vec3 var5 = thePlayer.getLook(256);
        Vec3 var6 = var4.addVector(var5.xCoord * par1, var5.yCoord * par1, var5.zCoord * par1);
        return thePlayer.worldObj.rayTraceBlocks(var4, var6);
	}
	
	public static EntityPlayerMP getPlayerFromUsername(String user) {
		return MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(user);
	}
	// why on earth is this client-side only?
	private static Vec3 getPosition(float par1, EntityPlayerMP player) {
        if (par1 == 1.0F)
        {
            return player.worldObj.getWorldVec3Pool().getVecFromPool(player.posX, player.posY, player.posZ);
        }
        else
        {
            double var2 = player.prevPosX + (player.posX - player.prevPosX) * (double)par1;
            double var4 = player.prevPosY + (player.posY - player.prevPosY) * (double)par1;
            double var6 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)par1;
            return player.worldObj.getWorldVec3Pool().getVecFromPool(var2, var4, var6);
        }
    }
}
