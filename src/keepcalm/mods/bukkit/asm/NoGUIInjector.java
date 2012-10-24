package keepcalm.mods.bukkit.asm;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.relauncher.IFMLCallHook;

public class NoGUIInjector implements IFMLCallHook {
	private Map<String, Object> dataInjected = new HashMap();
	@Override
	public Void call() throws Exception {
		System.out.println("Keys injected into IFMLCallHook:");
		for (String i : dataInjected.keySet()) {
			System.out.println(i);
		}
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		dataInjected.putAll(data);
	}

}
