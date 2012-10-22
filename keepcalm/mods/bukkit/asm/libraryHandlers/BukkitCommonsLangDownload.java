package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitCommonsLangDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		// TODO Auto-generated method stub
		return new String[] {"0eecdac8c86bc84b4bdfc24371ba8c785a1fc552"};
	}

	@Override
	public String[] getLibraries() {
		// TODO Auto-generated method stub
		return new String[] {"commons-lang-2.3.jar"};
	}

	@Override
	public String getRootURL() {
		// TODO Auto-generated method stub
		return "http://repo.maven.apache.org/maven2/commons-lang/commons-lang/2.3/%s";
	}

}
