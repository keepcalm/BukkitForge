package keepcalm.mods.bukkit.asm.transformers;

import java.util.HashMap;

import keepcalm.mods.events.asm.transformers.events.ObfuscationHelper;
import cpw.mods.fml.relauncher.IClassTransformer;

public class BukkitCommandFixerTransformer implements IClassTransformer {

	private HashMap<String,String> names = ObfuscationHelper.getRelevantMappings();
	
	@Override
	public byte[] transform(String name, byte[] bytes) {
		
		if (name.equals(names.get("commandHandler_className"))) {
			
		}
		
		return bytes;
	}
	
	private byte[] transformCommandHandler(byte[] bytes) {
		
		
		return bytes;
	}

}
