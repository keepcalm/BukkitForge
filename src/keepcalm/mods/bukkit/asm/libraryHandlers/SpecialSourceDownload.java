package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class SpecialSourceDownload implements ILibrarySet {

	@Override
	public String[] getLibraries() {
		return new String[] {"SpecialSource-1.5.jar"};
	}

	@Override
	public String[] getHashes() {
		return new String[] {"e0ffc9bb99ff8f2e12539a867a16751928d83d14"};
	}

	@Override
	public String getRootURL() {
		return "https://github.com/keepcalm/BukkitForge/blob/master/lib/%s?raw=true";
	}

}
