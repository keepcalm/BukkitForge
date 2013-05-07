package keepcalm.mods.bukkit.asm;

import java.io.File;
import java.util.Map;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import cpw.mods.fml.relauncher.RelaunchClassLoader;

public class BukkitASMLoader implements IFMLLoadingPlugin{
	
	public static RelaunchClassLoader cl;
    public static File minecraftDir;
	
	@Override
	public String[] getASMTransformerClass() {

        System.out.println( "Bukkitforge call to getASMTransformer ..." );

		try {
			Class.forName("keepcalm.mods.events.asm.BlockBreakEventAdder");
			return new String[] {"keepcalm.mods.bukkit.asm.transformers.BukkitAccessTransformer",
					"keepcalm.mods.bukkit.asm.transformers.BukkitAPIHelperTransformer",
					"keepcalm.mods.events.asm.BlockBreakEventAdder",
					"keepcalm.mods.events.asm.transformers.events.BlockEventHelpers",
					"keepcalm.mods.events.asm.transformers.events.EntityEventHelpers",
                    "keepcalm.mods.bukkit.asm.transformers.BukkitAsmagicTransformer"};
		}
		catch (ClassNotFoundException e) {
			System.out.println("Failed!");
			return new String[] {"keepcalm.mods.bukkit.asm.transformers.BukkitAccessTransformer", 
					"keepcalm.mods.bukkit.asm.transformers.BukkitAPIHelperTransformer",
					"keepcalm.mods.events.asm.transformers.events.BlockEventHelpers",
					"keepcalm.mods.events.asm.transformers.events.EntityEventHelpers",
                    "keepcalm.mods.bukkit.asm.transformers.BukkitAsmagicTransformer"};
		}
		
	}
	
	@Override
	public String[] getLibraryRequestClass() {
		return new String[] {"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitCommonsLangDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitEbeanDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitGSonDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitSQLiteDownload", 
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitJsonDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitYAMLDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitJANSIDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitGuava10Download",
				"keepcalm.mods.bukkit.asm.libraryHandlers.SpecialSourceDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.LombokDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.OpencsvDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.JoptSimpleDownload",
				"keepcalm.mods.bukkit.asm.libraryHandlers.BukkitMySQLDownload"}; 
	}
	
	@Override
	public String getModContainerClass() {
		return null;
	}
	
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data)
    {
    }
}
