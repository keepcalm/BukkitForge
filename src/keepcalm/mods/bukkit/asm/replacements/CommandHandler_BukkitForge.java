package keepcalm.mods.bukkit.asm.replacements;

import com.eoware.asm.asmagic.AsmagicReplaceMethod;
import keepcalm.mods.bukkit.nmsforge.CommandHandlerImpl;
import net.minecraft.command.*;

import java.util.*;

public class CommandHandler_BukkitForge implements ICommandManager {

        @AsmagicReplaceMethod(obfuscatedName = "")
        public int executeCommand(ICommandSender par1ICommandSender, String par2Str)
        {
            CommandHandlerImpl.getInstance().executeCommand(par1ICommandSender,  par2Str);
			return CommandHandlerImpl.getInstance().executeCommand(par1ICommandSender,  par2Str);
        }

        /**
         * adds the command and any aliases it has to the internal map of available commands
         */
        @AsmagicReplaceMethod(obfuscatedName = "a")
        public ICommand a(ICommand par1ICommand)
        {
            return CommandHandlerImpl.getInstance().registerCommand(par1ICommand);
        }

        /**
         * Performs a "begins with" string match on each token in par2. Only returns commands that par1 can use.
         */
        @AsmagicReplaceMethod(obfuscatedName = "")
        public List getPossibleCommands(ICommandSender par1ICommandSender, String par2Str)
        {
            return CommandHandlerImpl.getInstance().getPossibleCommands(par1ICommandSender,par2Str);
        }

        /**
         * returns all commands that the commandSender can use
         */
        @AsmagicReplaceMethod(obfuscatedName = "")
        public List getPossibleCommands(ICommandSender par1ICommandSender)
        {
            return CommandHandlerImpl.getInstance().getPossibleCommands(par1ICommandSender);
        }

        /**
         * returns a map of string to commads. All commands are returned, not just ones which someone has permission to use.
         */
        @AsmagicReplaceMethod(obfuscatedName = "")
        public Map getCommands()
        {
            return CommandHandlerImpl.getInstance().getCommands();
        }
    }


