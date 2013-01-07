package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitGSonDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		return new String[] {"bbf47ddc2e8931665390d4de33caf0ece9d3ff75"};
	}

	@Override
	public String[] getLibraries() {
		return new String[] {"gson-2.1-cb.jar"};
	}

	@Override
	public String getRootURL() {
		return "https://github.com/keepcalm/BukkitForge/blob/master/lib/%s?raw=true";
	}

}
