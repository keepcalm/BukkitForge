package keepcalm.mods.bukkit.asm.replacements;

import keepcalm.mods.bukkit.asm.asmagic.AsmagicMethodReplace;
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