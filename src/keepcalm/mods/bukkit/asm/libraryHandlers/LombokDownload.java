package keepcalm.mods.bukkit.asm.libraryHandlers;

import cpw.mods.fml.relauncher.ILibrarySet;

public class LombokDownload implements ILibrarySet {

	@Override
	public String[] getLibraries() {
		return new String[] {"lombok-0.11.6.jar"};
	}

	@Override
	public String[] getHashes() {
		return new String[] {"ba171d45e78f08ccca3cf531d285f13cfb4de2c7"};
	}

	@Override
	public String getRootURL() {
		return "http://repo.maven.apache.org/maven2/org/projectlombok/lombok/0.11.6/%s";
	}

}
