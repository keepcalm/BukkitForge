package keepcalm.mods.events.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class BlockBreakLoadingPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getLibraryRequestClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		try {
			// if bukkitforge is installed, don't worry - it will return for us
			Class.forName("keepcalm.mods.bukkit.BukkitContainer");
			return null;
		}
		catch (ClassNotFoundException e) {
			return new String[] { "keepcalm.mods.events.asm.BlockBreakEventAdder",
			"keepcalm.mods.events.asm.transformers.events.BlockEventHelpers",
			"keepcalm.mods.events.asm.transformers.events.EntityEventHelpers"
			};
		}
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
	public void injectData(Map<String, Object> data) {
		
	}
	
}
