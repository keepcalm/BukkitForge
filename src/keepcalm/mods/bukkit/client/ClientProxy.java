package keepcalm.mods.bukkit.client;

import keepcalm.mods.bukkit.BukkitContainer;
import keepcalm.mods.bukkit.common.CommonProxy;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerKeyBindings() {
		KeyBindingRegistry.registerKeyBinding(new ConsoleKeyHandler());
		NetworkRegistry.instance().registerGuiHandler(BukkitContainer.instance, new ConsoleGuiHandler());
	}
}
