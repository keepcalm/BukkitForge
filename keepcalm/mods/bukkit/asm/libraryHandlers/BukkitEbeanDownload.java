package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitEbeanDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		return new String[] {"70d2120ba676811fb6413056d4eadbdf95556a2f"};
	}

	@Override
	public String[] getLibraries() {
		return new String[] {"ebean-2.8.1.jar"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/org/avaje/ebean/2.8.1/%s";
	}

}
