package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitGSonDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		return new String[] {"881bf5cf5adb67e406df8dd5061c61bb4c3e143a"};
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
