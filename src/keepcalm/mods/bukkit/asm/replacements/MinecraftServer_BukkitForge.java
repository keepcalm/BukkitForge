package keepcalm.mods.bukkit.asm.replacements;

import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.profiler.IPlayerUsage;

// Not used yet

public abstract class MinecraftServer_BukkitForge
        implements ICommandSender, Runnable, IPlayerUsage
{
    private final ICommandManager commandManager = null;

    public ICommandManager getCommandManager()
    {
        return null;
    }
}