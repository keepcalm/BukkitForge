package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitGSonDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		return new String[] {"2e66da15851f9f5b5079228f856c2f090ba98c38"};
	}

	@Override
	public String[] getLibraries() {
		return new String[] {"gson-2.1.jar"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/com/google/code/gson/gson/2.1/%s";
	}

}
