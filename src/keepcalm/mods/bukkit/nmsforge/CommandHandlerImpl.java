package keepcalm.mods.bukkit.nmsforge;

import java.util.*;
import java.util.Map.Entry;

import keepcalm.mods.bukkit.BukkitEventRouters;
import keepcalm.mods.bukkitforge.BukkitForgePlayerCache;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandHandlerImpl implements ICommandManager {
       /** Map of Strings to the ICommand objects they represent */
        private final Map commandMap = new HashMap();

        /** The set of ICommand objects currently loaded. */
        private final Set commandSet = new HashSet();

        public int executeCommand(ICommandSender par1ICommandSender, String par2Str)
        {
            if( par1ICommandSender instanceof EntityPlayer)
            {
                PlayerCommandPreprocessEvent bev = BukkitEventRouters.Player.PlayerCommandPreprocess.callEvent(false, null, BukkitForgePlayerCache.getCraftPlayer((EntityPlayerMP) par1ICommandSender), par2Str);
                if(bev.isCancelled())
                {
                    return 0;
                }


            }

            if (par2Str.startsWith("/"))
            {
                par2Str = par2Str.substring(1);
            }

            String[] var3 = par2Str.split(" ");
            String var4 = var3[0];
            var3 = dropFirstString(var3);
            ICommand var5 = (ICommand)this.commandMap.get(var4);
            int var6 = this.getUsernameIndex(var5, var3);

            try
            {
                if (var5 == null)
                {
                    throw new CommandNotFoundException();
                }

                if (var5.canCommandSenderUseCommand(par1ICommandSender))
                {
                    CommandEvent event = new CommandEvent(var5, par1ICommandSender, var3);
                    if (MinecraftForge.EVENT_BUS.post(event))
                    {
                        if (event.exception != null)
                        {
                            throw event.exception;
                        }
                        return 0;
                    }

                    if (var6 > -1)
                    {
                        EntityPlayerMP[] var7 = PlayerSelector.matchPlayers(par1ICommandSender, var3[var6]);
                        String var8 = var3[var6];
                        EntityPlayerMP[] var9 = var7;
                        int var10 = var7.length;

                        for (int var11 = 0; var11 < var10; ++var11)
                        {
                            EntityPlayerMP var12 = var9[var11];
                            var3[var6] = var12.getEntityName();

                            try
                            {
                                var5.processCommand(par1ICommandSender, var3);
                            }
                            catch (PlayerNotFoundException var14)
                            {
                                par1ICommandSender.sendChatToPlayer(ChatMessageComponent.func_111082_b("\u00a7c" + var14.getMessage(), var14.getErrorOjbects()));
                            }
                        }

                        var3[var6] = var8;
                    }
                    else
                    {
                        var5.processCommand(par1ICommandSender, var3);
                    }
                }
                else
                {
                    par1ICommandSender.sendChatToPlayer(ChatMessageComponent.func_111082_b("\u00a7cYou do not have permission to use this command."));
                }
            }
            catch (WrongUsageException var15)
            {
                par1ICommandSender.sendChatToPlayer(ChatMessageComponent.func_111082_b("\u00a7c" + "commands.generic.usage", new Object[] {ChatMessageComponent.func_111082_b(var15.getMessage(), var15.getErrorOjbects())}).func_111059_a(EnumChatFormatting.RED));
            }
            catch (CommandException var16)
            {
                par1ICommandSender.sendChatToPlayer(ChatMessageComponent.func_111082_b("\u00a7c" + var16.getMessage(), var16.getErrorOjbects()).func_111059_a(EnumChatFormatting.RED));
            }
            catch (Throwable var17)
            {
                par1ICommandSender.sendChatToPlayer(ChatMessageComponent.func_111077_e("commands.generic.exception").func_111059_a(EnumChatFormatting.RED));
                var17.printStackTrace();
            }
			return 0;
        }

        /**
         * adds the command and any aliases it has to the internal map of available commands
         */
        public ICommand registerCommand(ICommand par1ICommand)
        {
            List var2 = par1ICommand.getCommandAliases();
            this.commandMap.put(par1ICommand.getCommandName(), par1ICommand);
            this.commandSet.add(par1ICommand);

            if (var2 != null)
            {
                Iterator var3 = var2.iterator();

                while (var3.hasNext())
                {
                    String var4 = (String)var3.next();
                    ICommand var5 = (ICommand)this.commandMap.get(var4);

                    if (var5 == null || !var5.getCommandName().equals(var4))
                    {
                        this.commandMap.put(var4, par1ICommand);
                    }
                }
            }

            return par1ICommand;
        }

        /**
         * creates a new array and sets elements 0..n-2 to be 0..n-1 of the input (n elements)
         */
        private static String[] dropFirstString(String[] par0ArrayOfStr)
        {
            String[] var1 = new String[par0ArrayOfStr.length - 1];

            for (int var2 = 1; var2 < par0ArrayOfStr.length; ++var2)
            {
                var1[var2 - 1] = par0ArrayOfStr[var2];
            }

            return var1;
        }

        /**
         * Performs a "begins with" string match on each token in par2. Only returns commands that par1 can use.
         */
        public List getPossibleCommands(ICommandSender par1ICommandSender, String par2Str)
        {
            String[] var3 = par2Str.split(" ", -1);
            String var4 = var3[0];

            if (var3.length == 1)
            {
                ArrayList var8 = new ArrayList();
                Iterator var6 = this.commandMap.entrySet().iterator();

                while (var6.hasNext())
                {
                    Entry var7 = (Entry)var6.next();

                    if (CommandBase.doesStringStartWith(var4, (String)var7.getKey()) && ((ICommand)var7.getValue()).canCommandSenderUseCommand(par1ICommandSender))
                    {
                        var8.add(var7.getKey());
                    }
                }

                return var8;
            }
            else
            {
                if (var3.length > 1)
                {
                    ICommand var5 = (ICommand)this.commandMap.get(var4);

                    if (var5 != null)
                    {
                        return var5.addTabCompletionOptions(par1ICommandSender, dropFirstString(var3));
                    }
                }

                return null;
            }
        }

        /**
         * returns all commands that the commandSender can use
         */
        public List getPossibleCommands(ICommandSender par1ICommandSender)
        {
            ArrayList var2 = new ArrayList();
            Iterator var3 = this.commandSet.iterator();

            while (var3.hasNext())
            {
                ICommand var4 = (ICommand)var3.next();

                if (var4.canCommandSenderUseCommand(par1ICommandSender))
                {
                    var2.add(var4);
                }
            }

            return var2;
        }

        /**
         * returns a map of string to commads. All commands are returned, not just ones which someone has permission to use.
         */
        public Map getCommands()
        {
            return this.commandMap;
        }

        /**
         * Return a command's first parameter index containing a valid username.
         */
        private int getUsernameIndex(ICommand par1ICommand, String[] par2ArrayOfStr)
        {
            if (par1ICommand == null)
            {
                return -1;
            }
            else
            {
                for (int var3 = 0; var3 < par2ArrayOfStr.length; ++var3)
                {
                    if (par1ICommand.isUsernameIndex(par2ArrayOfStr, var3) && PlayerSelector.matchesMultiplePlayers(par2ArrayOfStr[var3]))
                    {
                        return var3;
                    }
                }

                return -1;
            }
        }
        public static CommandHandlerImpl getInstance()
        {
            return impl;
        }

        protected static CommandHandlerImpl impl = new CommandHandlerImpl();

}
