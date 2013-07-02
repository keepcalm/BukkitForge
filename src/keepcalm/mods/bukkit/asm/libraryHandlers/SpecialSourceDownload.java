package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class SpecialSourceDownload implements ILibrarySet {

	@Override
	public String[] getLibraries() {
		return new String[] {"SpecialSource-1.6.jar"};
	}

	@Override
	public String[] getHashes() {
		return new String[] {"4a3a07f9553a0131c6cabb352cfa663c1c48153e"};
	}

	@Override
	public String getRootURL() {
		return "https://github.com/keepcalm/BukkitForge/blob/master/lib/%s?raw=true";
	}

}
