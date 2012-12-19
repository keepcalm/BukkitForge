package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitSQLiteDownload implements ILibrarySet {

	@Override
	public String[] getHashes() {
		return new String[] {"7a3d67f00508d3881650579f7f228c61bfc1b196"};
	}

	@Override
	public String[] getLibraries() {
		return new String[] {"sqlite-jdbc-3.7.2.jar"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/org/xerial/sqlite-jdbc/3.7.2/%s";
	}

}
