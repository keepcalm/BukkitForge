package keepcalm.mods.bukkit;

import org.bukkit.Material;

/**
 * This is the class which will be called
 * from ASM-injected code to, for example, add materials, etc.
 * @author keepcalm
 *
 */
public class BukkitHelpers {

	public static void addMaterial(int unshiftedIndex) {
		if (BukkitContainer.DEBUG) {
			System.out.println("Adding material: " + (256 + unshiftedIndex));
		}
		Material.addMaterial(256 + unshiftedIndex);
	}
	
}
