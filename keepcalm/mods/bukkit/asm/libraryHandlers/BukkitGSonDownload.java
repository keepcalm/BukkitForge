package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitGSonDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		return new String[] {"1f96456ca233dec780aa224bff076d8e8bca3908"};
	}

	@Override
	public String[] getLibraries() {
		return new String[] {"gson-2.2.2.jar"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.2.2/%s";
	}

}
