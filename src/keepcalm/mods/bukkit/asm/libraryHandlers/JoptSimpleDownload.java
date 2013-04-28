package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class JoptSimpleDownload implements ILibrarySet {

	@Override
	public String[] getLibraries() {
		return new String[] {"jopt-simple-4.4.jar"};
	}

	@Override
	public String[] getHashes() {
		return new String[] {"e3bc45034fe4d94a0c7b3012c2713f14ef76da64"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/net/sf/jopt-simple/jopt-simple/4.4/%s";
	}

}
