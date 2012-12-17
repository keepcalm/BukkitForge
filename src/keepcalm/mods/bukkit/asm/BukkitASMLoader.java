package keepcalm.mods.bukkit.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class BukkitASMLoader implements IFMLLoadingPlugin {
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"keepcalm.mods.bukkit.asm.BukkitAccessTransformer"};
	}
	@Override
	public String[] getLibraryRequestClass() {
		return new String[] {"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitCommonsLangDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitEbeanDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitGSonDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitSQLiteDownload", 
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitYAMLDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitJANSIDownload"};//, "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitJSRDownload"};
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
	public void injectData(Map<String, Object> data) {}
}
