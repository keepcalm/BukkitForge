package keepcalm.mods.bukkit.forgeHandler.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.command.CommandException;
import org.bukkit.craftbukkit.CraftPlayerCache;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CommandPermsDebug extends CommandBase {

	@Override
	public String getCommandName() {
		return "myperms";
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender par1) {
		return true;
	}
	
	@Override
	public void processCommand(ICommandSender var1, String[] var2) {
		
		if (var1 instanceof EntityPlayerMP) {
			CraftPlayer craft = CraftPlayerCache.getCraftPlayer((EntityPlayerMP) var1);
			
			File file = new File(var1.getCommandSenderName() + "_perms.txt");
			BufferedWriter buf;
			try {
				buf = new BufferedWriter(new FileWriter(file));
			}
			catch (Exception e) {
				throw new CommandException("Failed to open file: " + file.getAbsolutePath(), e);
			}
			
			for (PermissionAttachmentInfo j : craft.perm.getEffectivePermissions()) {
				PermissionAttachment x = j.getAttachment();
				try {
					buf.write(x.getPlugin().getDescription().getFullName());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				for (String i : x.getPermissions().keySet()) {
					try {
						buf.write("    " +i + " : " + x.getPermissions().get(i));
					} catch (IOException e) {
						var1.sendChatToPlayer("Failed to write a line, consult log for info.");
						e.printStackTrace();
					}
				}
				
			}
			
			try {
				buf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
