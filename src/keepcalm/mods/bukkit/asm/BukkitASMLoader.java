package keepcalm.mods.bukkit.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class BukkitASMLoader implements IFMLLoadingPlugin {
	//public static final boolean isObfuscated = ClassLoader.getSystemClassLoader().getSystemResourceAsStream("net/minecraft/src") == null;
	
	@Override
	public String[] getASMTransformerClass() {
		try {
			Class.forName("keepcalm.mods.blockbreak.asm.BlockBreakEventAdder");
			return new String[] {"keepcalm.mods.bukkit.asm.transformers.BukkitAccessTransformer", 
					"keepcalm.mods.blockbreak.asm.BlockBreakEventAdder",
					/*"keepcalm.mods.bukkit.asm.transformers.BukkitVanishTransformer", */
					"keepcalm.mods.bukkit.asm.transformers.events.BlockEventHelpers",
					"keepcalm.mods.bukkit.asm.transformers.events.EntityEventHelpers"};
		}
		catch (ClassNotFoundException e) {
			return new String[] {"keepcalm.mods.bukkit.asm.transformers.BukkitAccessTransformer", 
					/*"keepcalm.mods.bukkit.asm.transformers.BukkitVanishTransformer", */
					"keepcalm.mods.bukkit.asm.transformers.events.BlockEventHelpers",
					"keepcalm.mods.bukkit.asm.transformers.events.EntityEventHelpers"};
		}
		
	}
	@Override
	public String[] getLibraryRequestClass() {
		return new String[] {"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitCommonsLangDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitEbeanDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitGSonDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitSQLiteDownload", 
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitYAMLDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitJANSIDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitGuava10Download"}; 
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
