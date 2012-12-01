package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

// for guava 10
public class BukkitJSRDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		return new String[] {"e535b25d5c2bf5639eedd9ac407a0cf0039b5f41"};
	}

	@Override
	public String[] getLibraries() {
		return new String[] {"jsr305-1.3.9.jar"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/com/google/code/findbugs/jsr305/1.3.9/";
	}

}
