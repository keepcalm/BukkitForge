package keepcalm.mods.bukkit.asm;

import java.util.Map;


import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@TransformerExclusions
public class BukkitASMLoader implements IFMLLoadingPlugin {
	@Override
	public String[] getASMTransformerClass() {
		return new String[] {"keepcalm.mods.bukkit.asm.BukkitAccessTransformer"};
	}
	@Override
	public String[] getLibraryRequestClass() {
		return new String[] {"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitCommonsLangDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitEbeanDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitGSonDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitSQLiteDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitYAMLDownload", "keepcalm.mods.bukkit.asm.libraryHandlers.BukkitJANSIDownload"};
	}
	@Override
	public String getModContainerClass() {
		return "keepcalm.mods.bukkit.asm.BukkitContainer";
	}
	@Override
	public String getSetupClass() {
		return "keepcalm.mods.bukkit.asm.StubSetupClass";
	}
	@Override
	public void injectData(Map<String, Object> data) {}
}
