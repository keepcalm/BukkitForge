package keepcalm.mods.bukkit.asm;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class BukkitAccessTransformer extends AccessTransformer {
	private static BukkitAccessTransformer instance;
	private static List mapFileList = new LinkedList();
	
	
	public BukkitAccessTransformer() throws IOException {
		//super();
		//this.readMapFile("/bukkit_at.cfg");
		if (ObfuscationMappings.isObfuscated) {
			instance = this;
			mapFileList.add("bukkit_at.cfg");
			Iterator var2 = mapFileList.iterator();
			while(var2.hasNext()) {
				
				String file = (String)var2.next();
				System.out.println("Running var2.hasNext(): " + file);
				this.readMapFile(file);
			}
			
		}
		mapFileList = null;
	}
	
	
	public static void addTransformerMap(String mapFile) {
		if(instance == null) {
			mapFileList.add(mapFile);
		} else {
			instance.readMapFile(mapFile);
		}

	}
	
	
	private void readMapFile(String mapFile) {
		System.out.println("Adding Accesstransformer map: " + mapFile);

		try {
			Method e = AccessTransformer.class.getDeclaredMethod("readMapFile", new Class[]{String.class});
			e.setAccessible(true);
			e.invoke(this, mapFile);
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}


}