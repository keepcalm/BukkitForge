package keepcalm.mods.bukkit.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiConsole extends GuiScreen {
	private GuiTextField theEntryTextField;
	private GuiTextField theTextDisplayField;
	
	private final int display_x = 50;
	private final int display_y = 50;
	private final int display_height;
	private final int display_width;
	private FontRenderer theFontRenderer = Minecraft.getMinecraft().fontRenderer;
	
	public static GuiConsole instance;
	
	public GuiConsole() {
		// x, y, width, height
		super();
		
		display_height = Minecraft.getMinecraft().displayHeight - 650;
		display_width = Minecraft.getMinecraft().displayWidth - 350;
		instance = this;
		theTextDisplayField = new GuiTextField(fontRenderer, display_x, display_y, display_width, display_height);
		
		theTextDisplayField.drawTextBox();
		theTextDisplayField.writeText("BukkitForge Console\n");
		theTextDisplayField.setVisible(true);
		
	}
	
	@Override
	public void keyTyped(char key, int i) {
		/* TODO
		 * if (theEntryTextField.isFocused()) {
		 *  ...
		 * }
		 */
	}

	

}
