package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class OpencsvDownload implements ILibrarySet {

	@Override
	public String[] getLibraries() {
		return new String[] {"opencsv-2.3.jar"};
	}

	@Override
	public String[] getHashes() {
		return new String[] {"c23708cdb9e80a144db433e23344a788a1fd6599"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/net/sf/opencsv/opencsv/2.3/%s";
	}

}
