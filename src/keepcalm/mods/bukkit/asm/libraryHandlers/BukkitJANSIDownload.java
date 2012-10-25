package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitJANSIDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		// TODO Auto-generated method stub
		return new String[] {"3ce7490622cb94ae6293cd9b6ca3f617516a2696"};
	}

	@Override
	public String[] getLibraries() {
		// TODO Auto-generated method stub
		return new String[] {"jansi-1.9.jar"};
	}

	@Override
	public String getRootURL() {
		// TODO Auto-generated method stub
		return "http://repo.maven.apache.org/maven2/org/fusesource/jansi/jansi/1.9/%s";
	}

}
