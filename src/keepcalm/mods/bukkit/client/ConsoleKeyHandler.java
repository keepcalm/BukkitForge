package keepcalm.mods.bukkit.client;

import java.util.EnumSet;

import keepcalm.mods.bukkit.BukkitContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.settings.KeyBinding;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class ConsoleKeyHandler extends KeyHandler {

	private final static KeyBinding theKeyBinding = new KeyBinding("BukkitForge Console", 46);
	
	public ConsoleKeyHandler() {
		super(new KeyBinding[] { theKeyBinding });
		System.out.println("Keybindings ARE GO!!!");
		
	}

	@Override
	public String getLabel() {
		return "BukkitForgeKeyHandler";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		System.out.println("Opening!");
		if (Minecraft.getMinecraft().inGameHasFocus) {
			
			EntityClientPlayerMP p = Minecraft.getMinecraft().thePlayer;
			Minecraft.getMinecraft().thePlayer.openGui(BukkitContainer.instance, 0, p.worldObj, p.serverPosX, p.serverPosY, p.serverPosZ);
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		System.out.println("Hai!");
	}

	@Override
	public EnumSet<TickType> ticks() {
		EnumSet<TickType> j = EnumSet.allOf(TickType.class);
		return j;
	}

}
