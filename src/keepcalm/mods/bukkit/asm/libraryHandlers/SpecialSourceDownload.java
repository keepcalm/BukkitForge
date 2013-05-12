package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class SpecialSourceDownload implements ILibrarySet {

	@Override
	public String[] getLibraries() {
		return new String[] {"SpecialSource-1.6.jar"};
	}

	@Override
	public String[] getHashes() {
		return new String[] {"60f76cfe1c1e5818480ff11514b863566a95db10"};
	}

	@Override
	public String getRootURL() {
		return "https://github.com/keepcalm/BukkitForge/blob/master/lib/%s?raw=true";
	}

}
