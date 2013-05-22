package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitGuava10Download implements ILibrarySet {

	@Override
	public String[] getLibraries() {
		return new String[] {"guava10-renamed.jar", "SpecialSource-1.3-SNAPSHOT.jar"};
	}

	@Override
	public String[] getHashes() {
		return new String[] {"be77eccdc699f3f8ec1623700ba0fa38270680fe", 
				"40b73f47d8e568d763c99d55d400ec0a9084a433"
		};
	}

	@Override
	public String getRootURL() {
		return "https://github.com/keepcalm/BukkitForge/blob/master/lib/%s?raw=true";
	}

}
