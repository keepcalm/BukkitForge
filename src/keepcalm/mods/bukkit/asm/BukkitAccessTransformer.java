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
		instance = this;
		Iterator var2 = mapFileList.iterator();
		 while(var2.hasNext()) {
	         String file = (String)var2.next();
	         this.readMapFile(file);
	      }
		 if (!ObfuscationMappings.isObfuscated) {
			 try {
				 readMapFile("/bukkit_at.cfg");
			 }
			 catch (RuntimeException e) {
				 e.printStackTrace();
				 System.out.println("Assuming this is a development environment, continuing...");
			 }
		 }
	    //mapFileList = null;
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
			e.invoke(this, new Object[]{mapFile});
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}
	

}