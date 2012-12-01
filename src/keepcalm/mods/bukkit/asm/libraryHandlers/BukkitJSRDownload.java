package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

// for guava 10
public class BukkitJSRDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		return new String[] {"40719ea6961c0cb6afaeb6a921eaa1f6afd4cfdf"};
	}

	@Override
	public String[] getLibraries() {
		return new String[] {"jsr305-1.3.9.jar"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/com/google/code/findbugs/jsr305/1.3.9/%s";
	}

}
