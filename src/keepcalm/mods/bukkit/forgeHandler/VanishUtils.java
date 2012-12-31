package keepcalm.mods.bukkit.forgeHandler;

import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import com.google.common.collect.Lists;

public class VanishUtils {

	private static HashMap<EntityPlayerMP,List<EntityPlayerMP>> hidden = new HashMap<EntityPlayerMP, List<EntityPlayerMP>>();
	
	public static boolean isHidden(EntityPlayerMP who, Entity from) {
		EntityPlayerMP ent = (EntityPlayerMP) from;
		
		if (!hidden.containsKey(who)) {
			return false;
		}
		else {
			return hidden.get(who).contains(ent);
		}
		
	}
	
	public static void setHidden(EntityPlayerMP playerToHide, EntityPlayerMP fromWhom) {
		
		if (hidden.containsKey(playerToHide)) {
			hidden.get(playerToHide).add(fromWhom);
		}
		else {
			hidden.put(playerToHide, Lists.newArrayList(fromWhom));
		}
		
	}
	
	public static void setVisible(EntityPlayerMP hiddenPlayer, EntityPlayerMP unawarePlayer) {
		if (hidden.containsKey(hiddenPlayer)) {
			if (hidden.get(hiddenPlayer).contains(unawarePlayer)) {
				hidden.get(hiddenPlayer).remove(unawarePlayer);
			}
		}
	}

}
