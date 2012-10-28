package keepcalm.mods.bukkit.utils;

import net.minecraft.src.ChunkPosition;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumMovingObjectType;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Vec3;

public class MiscForgeHelpers {
	/**
	 * I admit to blatant copying of CB's source code.
	 * 
	 * @author chickenbones
	 * @param player - player to use
	 * @param reach - distance to raytrace for
	 * @return ChunkPosition of the target block or null
	 */
	public static ChunkPosition getPlayerLookingAtBlock(EntityPlayerMP player, float reach) {
		Vec3 vec3d = Vec3.createVectorHelper(player.posX, (player.posY + 1.6200000000000001D) - (double)player.yOffset, player.posZ);
		Vec3 vec3d1 = player.getLook(1.0F);
		Vec3 vec3d2 = vec3d.addVector(vec3d1.xCoord * reach, vec3d1.yCoord * reach, vec3d1.zCoord * reach);
        MovingObjectPosition hit = player.worldObj.rayTraceBlocks(vec3d, vec3d2);
        if(hit == null || hit.typeOfHit != EnumMovingObjectType.TILE)
        {
        	return null;
        }
        
        
        return new ChunkPosition(hit.blockX, hit.blockY, hit.blockZ);
	}
}
