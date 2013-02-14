package keepcalm.mods.bukkit.asm.replacements;

import keepcalm.mods.bukkit.asm.asmext.AsmExtMethodReplace;
import keepcalm.mods.bukkit.forgeHandler.CommandManagerWrapper;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.profiler.IPlayerUsage;

public abstract class MinecraftServer_BukkitForge
        implements ICommandSender, Runnable, IPlayerUsage
{
    private final ICommandManager commandManager = null;

    @AsmExtMethodReplace
    public ICommandManager getCommandManager()
    {
        if(CommandManagerWrapper.getInstance() == null )
        {
            CommandManagerWrapper.createInstance(commandManager);
        }
        return (ICommandManager)CommandManagerWrapper.getInstance();
    }
}