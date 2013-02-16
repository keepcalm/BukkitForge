package keepcalm.mods.bukkit.asm;

import java.util.Map;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class BukkitASMLoader implements IFMLLoadingPlugin {
	//public static final boolean isObfuscated = ClassLoader.getSystemClassLoader().getSystemResourceAsStream("net/minecraft/src") == null;
	
	@Override
	public String[] getASMTransformerClass() {

        System.out.println( "Bukkitforge call to getASMTransformer ..." ); // Don't use log so that it doesnt preload Minecraft classes

		try {
			Class.forName("keepcalm.mods.events.asm.BlockBreakEventAdder");
			return new String[] {"keepcalm.mods.bukkit.asm.transformers.BukkitAccessTransformer",
					"keepcalm.mods.bukkit.asm.transformers.BukkitAPIHelperTransformer",
					"keepcalm.mods.events.asm.BlockBreakEventAdder",
					/*"keepcalm.mods.bukkit.asm.transformers.BukkitVanishTransformer", */
					"keepcalm.mods.events.asm.transformers.events.BlockEventHelpers",
					"keepcalm.mods.events.asm.transformers.events.EntityEventHelpers",
                    "keepcalm.mods.bukkit.asm.asmagic.AsmagicClassTransformer"};
		}
		catch (ClassNotFoundException e) {
			return new String[] {"keepcalm.mods.bukkit.asm.transformers.BukkitAccessTransformer", 
					"keepcalm.mods.bukkit.asm.transformers.BukkitAPIHelperTransformer",
					/*"keepcalm.mods.bukkit.asm.transformers.BukkitVanishTransformer", */
					"keepcalm.mods.events.asm.transformers.events.BlockEventHelpers",
					"keepcalm.mods.events.asm.transformers.events.EntityEventHelpers",
                    "keepcalm.mods.bukkit.asm.asmagic.AsmagicClassTransformer"};
		}
		
	}
	@Override
	public String[] getLibraryRequestClass() {
		return new String[] {"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitCommonsLangDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitEbeanDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitGSonDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitSQLiteDownload", 
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitJsonDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitYAMLDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitJANSIDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitGuava10Download",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitMySQLDownload"}; 
	}
	@Override
	public String getModContainerClass() {
		return null;
	}
	@Override
	public String getSetupClass() {
		return null;
	}
	@Override
	public void injectData(Map<String, Object> data) {
		
		
	}
}
