package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitJsonDownload implements ILibrarySet {

	@Override
	public String[] getLibraries() {
		return new String[] {"json-simple-1.1.1.jar"};
	}

	@Override
	public String[] getHashes() {
		return new String[] {"c9ad4a0850ab676c5c64461a05ca524cdfff59f1"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/com/googlecode/json-simple/json-simple/1.1.1/%s";
	}

}
