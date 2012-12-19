package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitYAMLDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		return new String[] {"5372b16fee57380d07d027f7dd138b1dcf1bdb92"};
	}

	@Override
	public String[] getLibraries() {
		return new String[] {"snakeyaml-1.9.jar"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/org/yaml/snakeyaml/1.9/%s";
	}

}
