package keepcalm.mods.bukkit.asm;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;

public class StubSetupClass implements IFMLCallHook {
	private Map<String,Object> myData = new HashMap();
	@Override
	public Void call() throws Exception {
		
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		myData.putAll(data);
	}

}
