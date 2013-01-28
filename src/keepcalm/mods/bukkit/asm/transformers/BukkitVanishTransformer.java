package keepcalm.mods.bukkit.asm.transformers;

import java.util.HashMap;

import cpw.mods.fml.relauncher.IClassTransformer;

public class BukkitVanishTransformer implements IClassTransformer {

	public HashMap<String, String> names = new HashMap<String, String>();
	
	@Override
	public byte[] transform(String name, byte[] bytes) {
		
		
		return bytes;
	}

}
