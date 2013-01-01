package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class BukkitMySQLDownload implements ILibrarySet {

	@Override
	public String[] getLibraries() {
		return new String[] { "mysql-connector-java-5.1.14.jar" };
	}

	@Override
	public String[] getHashes() {
		return new String[] { "94f32ab65801741e0f19e2b506d130f6792334d5" };
	}

	@Override
	public String getRootURL() {
		// TODO Auto-generated method stub
		return "http://repo.maven.apache.org/maven2/mysql/mysql-connector-java/5.1.14/%s";
	}

}
