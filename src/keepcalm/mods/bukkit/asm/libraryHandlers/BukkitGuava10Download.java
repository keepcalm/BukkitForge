package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

// we need guava 10 for essentials compat
public class BukkitGuava10Download implements ILibrarySet {

	@Override
	public String[] getHashes() {
		return new String[] {"d05cd8b0a7ee4466994d64779eb28190"};
	}

	@Override
	public String[] getLibraries() {
		return new String[] {"guava-10.0.1.jar"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/com/google/guava/guava/10.0.1/";
	}

}
