package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitGuava10Download implements ILibrarySet {

	@Override
	public String[] getLibraries() {
		return new String[] {"guava10-renamed.jar"};
	}

	@Override
	public String[] getHashes() {
		return new String[] {"be77eccdc699f3f8ec1623700ba0fa38270680fe"};
	}

	@Override
	public String getRootURL() {
		return "https://github.com/keepcalm/BukkitForge/blob/master/lib/%s?raw=true";
	}

}
